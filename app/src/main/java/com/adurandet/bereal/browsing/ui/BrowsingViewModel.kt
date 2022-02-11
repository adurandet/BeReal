package com.adurandet.bereal.browsing.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adurandet.bereal.browsing.BrowsingContent
import com.adurandet.bereal.browsing.repository.BrowsingRepository
import com.adurandet.bereal.common.network.ApiHelper
import com.eurosport.home.network.Resource
import com.eurosport.home.network.Status
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BrowsingViewModel : ViewModel(), OnDirectoryClickListener, OnFileClickListener {

    // A usecase in between repo and viewmodel will match a better architecture
    // That should be instantiated by DI
    private val api = ApiHelper.getBrowsingApiInterface()
    private val repository = BrowsingRepository(api)

    private val _viewState: MutableLiveData<BrowsingViewState> = MutableLiveData()

    val viewState: LiveData<BrowsingViewState> = _viewState

    private val _actionsSharedFlow = MutableSharedFlow<BrowsingAction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: Flow<BrowsingAction> = _actionsSharedFlow.asSharedFlow()

    private var browsingDirectoryId: String = "8d1b283d75d6ae7892ad8bdd8f305dedd6e93a36"
    fun setBrowsingDirectoryId(directoryId: String) {
        browsingDirectoryId = directoryId
    }

    init {
        initFlow()
    }

    private fun initFlow() {
        viewModelScope.launch {
            repository.browsingContentFlow.collect {
                handleBrowsingContentFlow(it)
            }
        }
    }

    private fun handleBrowsingContentFlow(browsingContentList: Resource<List<BrowsingContent>>) {
        when (browsingContentList.status) {
            Status.LOADING -> _viewState.value = BrowsingViewState.Loading
            Status.ERROR -> _viewState.value =
                BrowsingViewState.Error(browsingContentList.message ?: "")
            Status.SUCCESS -> browsingContentList.data?.let {
                _viewState.value = BrowsingViewState.Success(it)
            }
        }
    }

    fun getHomeContent() {
        viewModelScope.launch {
            _viewState.value = BrowsingViewState.Loading
            repository.getBrowsingContent(id = browsingDirectoryId)
        }
    }

    override fun onDirectoryClicked(id: String, parentId: String) {
        _actionsSharedFlow.tryEmit(BrowsingAction.GoToDirectory(id, parentId))
    }

    override fun onFileClicked(id: String) {
        _actionsSharedFlow.tryEmit(BrowsingAction.OpenImage(id))
    }

    sealed class BrowsingViewState {
        object Loading : BrowsingViewState()
        data class Error(val message: String) : BrowsingViewState()
        data class Success(val contentList: List<BrowsingContent>) : BrowsingViewState()
    }

    sealed class BrowsingAction {
        data class GoToDirectory(val id: String, val parentId: String) : BrowsingAction()
        data class OpenImage(val id: String) : BrowsingAction()
    }
}