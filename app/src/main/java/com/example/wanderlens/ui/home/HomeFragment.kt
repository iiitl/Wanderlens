package com.example.wanderlens.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wanderlens.R
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.databinding.FragmentHomeBinding
import com.example.wanderlens.utils.Resource
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var journalAdapter: JournalAdapter

    private var allJournals: List<JournalEntry> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipSelection()
        setupSearch()
        observeViewModel()

        binding.btnUpload.setOnClickListener {
            findNavController().navigate(R.id.nav_upload)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchJournals()
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            if (binding.cvSearchBar.visibility == View.VISIBLE) {
                closeSearch()
            } else {
                binding.cvSearchBar.visibility = View.VISIBLE
                binding.etSearch.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.btnCloseSearch.setOnClickListener {
            closeSearch()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                if (query.isEmpty()) {
                    journalAdapter.submitList(allJournals)
                } else {
                    val filtered = allJournals.filter {
                        it.title.lowercase().contains(query) ||
                        it.location.lowercase().contains(query) ||
                        it.country.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
                    }
                    journalAdapter.submitList(filtered)
                }
            }
        })
    }

    private fun closeSearch() {
        binding.cvSearchBar.visibility = View.GONE
        binding.etSearch.text?.clear()
        journalAdapter.submitList(allJournals)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    private fun setupRecyclerView() {
        journalAdapter = JournalAdapter { entry ->
            val bundle = Bundle().apply {
                putString("journalId", entry.id)
            }
            findNavController().navigate(R.id.nav_detail, bundle)
        }
        binding.rvJournals.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = journalAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupChipSelection() {
        binding.chipAll.setOnClickListener {
            journalAdapter.submitList(allJournals)
            highlightChip(binding.chipAll)
        }
    }

    private fun buildCountryChips(journals: List<JournalEntry>) {
        if (_binding == null) return

        val chipGroup = binding.chipGroup
        while (chipGroup.childCount > 1) {
            chipGroup.removeViewAt(1)
        }

        // Extract unique countries
        val countries = journals.mapNotNull { it.country.takeIf { c -> c.isNotBlank() && c != "World" } }
            .distinct()
            .sorted()

        for (country in countries) {
            val chip = Chip(requireContext()).apply {
                text = country
                isCheckable = false
                chipBackgroundColor = resources.getColorStateList(R.color.brand_tint_10, null)
                setTextColor(resources.getColor(R.color.brand_primary, null))
                chipStrokeWidth = 0f
                setOnClickListener {
                    val filtered = allJournals.filter { it.country == country }
                    journalAdapter.submitList(filtered)
                    highlightChip(this)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun highlightChip(selected: Chip) {
        if (_binding == null) return
        val chipGroup = binding.chipGroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip ?: continue
            if (chip == selected) {
                chip.chipBackgroundColor = resources.getColorStateList(R.color.brand_primary, null)
                chip.setTextColor(resources.getColor(R.color.white, null))
            } else {
                chip.chipBackgroundColor = resources.getColorStateList(R.color.brand_tint_10, null)
                chip.setTextColor(resources.getColor(R.color.brand_primary, null))
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.journalsState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("HomeFragment", "Loading journals...")
                        }
                        is Resource.Success -> {
                            allJournals = resource.data ?: emptyList()
                            journalAdapter.submitList(allJournals)
                            buildCountryChips(allJournals)

                            // Show empty state or feed
                            if (allJournals.isEmpty()) {
                                binding.llEmptyState.visibility = View.VISIBLE
                                binding.rvJournals.visibility = View.GONE

                            } else {
                                binding.llEmptyState.visibility = View.GONE
                                binding.rvJournals.visibility = View.VISIBLE
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
