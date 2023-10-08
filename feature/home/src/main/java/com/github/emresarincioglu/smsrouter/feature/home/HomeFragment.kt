package com.github.emresarincioglu.smsrouter.feature.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.transition.TransitionManager
import com.github.emresarincioglu.smsrouter.feature.home.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFade
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import com.github.emresarincioglu.smsrouter.core.designsystem.R as designSystemR


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val intent = result.data
                val spokenText =
                    intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                spokenText?.let {
                    binding.searchBar.text = it
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupViews()
        observeUiState()

        return binding.root
    }

    private fun observeUiState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collectLatest { uiState ->

                    binding.rvSender.adapter = SenderAdapter(
                        uiState.senders,
                        homeViewModel
                    )
                    binding.rvSearch.adapter = SearchResultAdapter(
                        searchHistory = uiState.searchHistory,
                        searchResults = uiState.searchResults,
                        onHistoryItemClicked = { item ->

                            val itemText = (item as TextView).text.toString()
                            homeViewModel.getSenders(itemText)
                            binding.searchBar.text = itemText
                            binding.svSender.hide()
                        },
                        onHistoryItemLongClicked = { item ->

                            val itemText = (item as TextView).text
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.dialog_delete_history_title)
                                .setMessage(
                                    getString(
                                        R.string.format_dialog_delete_history_msg, itemText
                                    )
                                )
                                .setIcon(designSystemR.drawable.ic_delete_24)
                                .setPositiveButton(R.string.dialog_btn_delete_text) { dialog, which ->
                                    val searchHistory = itemText.toString()
                                    homeViewModel.removeSearchHistory(searchHistory)
                                }
                                .setNegativeButton(android.R.string.cancel, null)
                                .show()
                            true
                        },
                        onResultItemClicked = { item ->
                            // TODO: Navigate to sender screen
                        }
                    )

                    if (!uiState.snackbarText.isNullOrBlank()) {
                        Snackbar.make(
                            binding.clHome,
                            uiState.snackbarText,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(R.string.undo) {
                            homeViewModel.restoreRemovedSender()
                        }.show()
                    }
                }
            }
        }
    }

    private fun setupViews() {

        setupAppBar()
        setupSenderRecyclerView()
        setupSenderSearchView()
        binding.fabAddSender.setOnClickListener {
            // TODO: Navigate to create sender full screen dialog
        }
    }

    private fun setupAppBar() {

        binding.searchBar.setOnMenuItemClickListener {

            if (it.itemId == R.id.menu_item_speak) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(
                        RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_bar_item_speak_title)
                    )
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                }

                speechRecognizerLauncher.launch(intent)
            }
            true
        }
    }

    private fun setupSenderRecyclerView() {

        val context = requireContext()
        val swipeToDeleteCallback = SwipeToDeleteHelper(
            context = context,
            backgroundColorFrom = MaterialColors.getColor(
                context,
                com.google.android.material.R.attr.colorSecondaryContainer,
                Color.GRAY
            )
        ) { position ->
            homeViewModel.removeSender(context, position)
            binding.rvSender.adapter?.notifyItemRemoved(position)
        }

        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding.rvSender)
    }

    private fun setupSenderSearchView() {

        // On search view visibility change
        binding.svSender.addTransitionListener { searchView, previousState, newState ->

            if (newState == SearchView.TransitionState.SHOWING) {

                setBottomNavBarVisibility(isVisible = false)
                binding.fabAddSender.hide()
            } else if (newState == SearchView.TransitionState.HIDDEN) {

                setBottomNavBarVisibility(isVisible = true)
                binding.searchBar.text = binding.svSender.text
                binding.fabAddSender.show()

                if (binding.svSender.text.isNullOrBlank()) {
                    homeViewModel.getSenders()
                }
            }
        }

        // On query text change
        binding.svSender.editText.addTextChangedListener { query ->
            homeViewModel.getSearchResultWithHistory(query.toString())
        }

        // On query submit
        binding.svSender.editText.setOnEditorActionListener { view, actionId, keyEvent ->

            homeViewModel.getSenders(binding.svSender.text?.toString() ?: "")
            binding.svSender.hide()
            true
        }
    }

    private fun setBottomNavBarVisibility(isVisible: Boolean, animate: Boolean = true) {

        val bottomNavBar: BottomNavigationView =
            requireActivity().findViewById(designSystemR.id.bottom_nav_bar)
        val newVisibility = if (isVisible) View.VISIBLE else View.GONE

        if (newVisibility == bottomNavBar.visibility) {
            return
        }

        if (animate) {
            val transitionAnimation = MaterialFade()
            val activityRootView =
                requireActivity().findViewById<ViewGroup>(android.R.id.content)
            TransitionManager.beginDelayedTransition(activityRootView, transitionAnimation)
        }

        bottomNavBar.visibility = newVisibility
    }
}