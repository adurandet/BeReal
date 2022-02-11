package com.adurandet.bereal.browsing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adurandet.bereal.R
import com.adurandet.bereal.databinding.BrowsingFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class BrowsingFragment: Fragment() {

    // That should be instantiated by DI
    private val viewModel: BrowsingViewModel by viewModels()

    private lateinit var binding: BrowsingFragmentBinding

    private val adapter: ContentListAdapter by lazy {
        ContentListAdapter(
            onDirectoryClickListener = viewModel,
            onFileClickListener = viewModel
        )
    }

    private val browsingArgs: BrowsingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BrowsingFragmentBinding.inflate(inflater)

        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)

        with(binding.listView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BrowsingFragment.adapter
        }

        initViewStateObserver(binding, browsingArgs)
        initActionFlow()

        return binding.root
    }

    private fun initActionFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.actions.collect {
                handleActions(it)
            }
        }
    }

    private fun initViewStateObserver(
        binding: BrowsingFragmentBinding,
        browsingArgs: BrowsingFragmentArgs
    ) {
        viewModel.setBrowsingDirectoryId(browsingArgs.directoryId)
        viewModel.viewState.observe(viewLifecycleOwner) {
            processLoading(it is BrowsingViewModel.BrowsingViewState.Loading)
            when (it) {
                is BrowsingViewModel.BrowsingViewState.Error -> Snackbar.make(
                    binding.root,
                    it.message,
                    Snackbar.LENGTH_SHORT
                ).show()
                is BrowsingViewModel.BrowsingViewState.Success -> adapter.submitList(it.contentList)
            }
        }
    }

    private fun processLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun handleActions(action: BrowsingViewModel.BrowsingAction) {
        when (action) {
            is BrowsingViewModel.BrowsingAction.OpenImage -> {
                val direction = BrowsingFragmentDirections.openImage(action.id)
                view?.findNavController()?.navigate(direction)
            }
            is BrowsingViewModel.BrowsingAction.GoToDirectory -> {
                val direction = BrowsingFragmentDirections.goToDirectory(action.id, action.parentId)
                view?.findNavController()?.navigate(direction)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHomeContent()
    }
}