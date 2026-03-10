package com.example.wanderlens.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanderlens.R
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.databinding.FragmentJournalDetailBinding
import com.example.wanderlens.databinding.ItemFunFactBinding
import com.example.wanderlens.repository.JournalRepository
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class JournalDetailFragment : Fragment() {

    private var _binding: FragmentJournalDetailBinding? = null
    private val binding get() = _binding!!

    private val repository = JournalRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val journalId = arguments?.getString("journalId")

        if (journalId != null) {
            fetchJournalDetail(journalId)
        } else {
            Toast.makeText(requireContext(), "Error: Journal not found", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        setupToolbar()
        setupCollapsingToolbarEffect()
        setupPreviewOverlay()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)

        binding.toolbar.setNavigationOnClickListener {
            handleBackAction()
        }
    }

    private fun handleBackAction() {
        if (binding.previewOverlay.visibility == View.VISIBLE) {
            binding.previewOverlay.visibility = View.GONE
        } else {
            findNavController().navigateUp()
        }
    }

    private fun setupPreviewOverlay() {
        binding.btnClosePreview.setOnClickListener {
            binding.previewOverlay.visibility = View.GONE
        }
        
        binding.previewOverlay.setOnClickListener {
            binding.previewOverlay.visibility = View.GONE
        }
    }

    private var journalTitle: String? = null

    private fun setupCollapsingToolbarEffect() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScroll = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / totalScroll

            if (percentage > 0.8f) {
                binding.toolbar.setBackgroundColor(requireContext().getColor(R.color.brand_primary))
                binding.toolbar.title = journalTitle
            } else {
                binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
                binding.toolbar.title = ""
            }
        }
    }

    private fun fetchJournalDetail(id: String) {
        repository.getJournalById(id)
            .onEach { resource ->
                when (resource) {

                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.nestedScrollView.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.nestedScrollView.visibility = View.VISIBLE
                        val entry = resource.data
                        bindData(entry)
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun bindData(entry: JournalEntry) {
        journalTitle = entry.title
        binding.tvDetailTitle.text = entry.title
        binding.tvDetailLocation.text = "${entry.location}, ${entry.country}"
        binding.tvHistory.text = entry.description

        Glide.with(this)
            .load(entry.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .centerCrop()
            .into(binding.ivDetailHeader)

        binding.ivDetailHeader.setOnClickListener {
            showImagePreview(entry.imageUrl)
        }

        setupFunFacts(entry.funFacts)
    }

    private fun showImagePreview(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivPreview)
        binding.previewOverlay.visibility = View.VISIBLE
    }

    private val snapHelper = PagerSnapHelper()

    private fun setupFunFacts(facts: List<String>) {
        if (facts.isEmpty()) {
            binding.rvFunFacts.visibility = View.GONE
            return
        }

        binding.rvFunFacts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = FunFactAdapter(facts)

            if (onFlingListener == null) {
                snapHelper.attachToRecyclerView(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class FunFactAdapter(
        private val facts: List<String>
    ) : RecyclerView.Adapter<FunFactAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemFunFactBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding = ItemFunFactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.tvFunFact.text = facts[position]
        }

        override fun getItemCount(): Int = facts.size
    }
}