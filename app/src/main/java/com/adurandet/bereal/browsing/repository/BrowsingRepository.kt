package com.adurandet.bereal.browsing.repository

import android.util.Log
import com.adurandet.bereal.browsing.BrowsingContent
import com.adurandet.bereal.browsing.network.BrowsingApi
import com.eurosport.home.network.Resource
import com.eurosport.home.network.Status
import com.eurosport.utils.AndroidDispatcherProvider
import com.eurosport.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

class BrowsingRepository(
    private val browsingApiInterface: BrowsingApi,
    private val dispatcherProvider: DispatcherProvider = AndroidDispatcherProvider()
) {

    private val _browsingContentFlow = MutableSharedFlow<Resource<List<BrowsingContent>>>(1)
    val browsingContentFlow: SharedFlow<Resource<List<BrowsingContent>>> = _browsingContentFlow

    suspend fun getBrowsingContent(id: String) {
        withContext(dispatcherProvider.io()) {
            var response: Resource<List<BrowsingContent>> = Resource.loading()
            try {
                _browsingContentFlow.tryEmit(Resource.loading())
                val browsingContent = browsingApiInterface.getContent(id)
                response = Resource.success(browsingContent)
                handleBrowsingContentFlow(response)
            } catch (e: Exception) {
                Log.e("BrowsingRepository", e.stackTraceToString())
                response = Resource.error(e.message ?: "")
            } finally {
                handleBrowsingContentFlow(response)
            }
        }
    }

    private fun handleBrowsingContentFlow(browsingContentResponse: Resource<List<BrowsingContent>>) {
        when (browsingContentResponse.status) {
            Status.LOADING -> _browsingContentFlow.tryEmit(Resource.loading())
            Status.ERROR -> _browsingContentFlow.tryEmit(
                Resource.error(
                    browsingContentResponse.message ?: ""
                )
            )
            Status.SUCCESS -> browsingContentResponse.data?.let {
                handleHomeContentFlowSuccess(it)
            }
        }
    }

    private fun handleHomeContentFlowSuccess(browsingContentResponse: List<BrowsingContent>) {
        val browsingContent: List<BrowsingContent> = browsingContentResponse
        _browsingContentFlow.tryEmit(Resource.success(browsingContent))
    }
}