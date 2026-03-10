package com.example.wanderlens.ui.upload

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.wanderlens.R
import com.example.wanderlens.databinding.FragmentAiProcessingBinding
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AiProcessingFragment : Fragment() {

    private var _binding: FragmentAiProcessingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UploadViewModel by activityViewModels()
    private var processingDone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPulseAnimation()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedImageUri.collect { uri ->
                    if (uri != null) {
                        com.bumptech.glide.Glide.with(this@AiProcessingFragment)
                            .load(uri)
                            .centerCrop()
                            .into(binding.ivPreview)
                    }
                }
            }
        }

        simulateProgress()

        binding.btnCancel.setOnClickListener {
            viewModel.resetState()
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            viewModel.resetState()
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            processingDone = true
                            completeAllSteps()
                            delay(800)
                            if (_binding != null) {
                                Toast.makeText(requireContext(), "✨ Journal Created!", Toast.LENGTH_SHORT).show()
                                viewModel.resetState()
                                findNavController().popBackStack(R.id.nav_home, false)
                            }
                        }
                        is Resource.Error -> {
                            if (_binding != null) {
                                binding.tvStatusTitle.text = "Something went wrong"
                                binding.tvStatusSubtitle.text = state.message
                                binding.tvProgressLabel.text = "Failed"
                                Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                                delay(2000)
                                viewModel.resetState()
                                findNavController().popBackStack()
                            }
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    private fun startPulseAnimation() {
        val pulse = AlphaAnimation(1f, 0.3f).apply {
            duration = 800
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.ivAiIcon.startAnimation(pulse)
    }

    private fun simulateProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            val steps = listOf(
                Triple(binding.tvStep1, "Uploading photo...", 20),
                Triple(binding.tvStep2, "Analyzing photo with AI...", 50),
                Triple(binding.tvStep3, "Generating fun facts & trivia...", 75),
                Triple(binding.tvStep4, "Saving journal entry...", 90)
            )

            for ((index, step) in steps.withIndex()) {
                if (_binding == null || processingDone) break
                val (stepView, label, targetProgress) = step

                activateStep(stepView)
                binding.tvProgressLabel.text = label

                animateProgress(targetProgress)

                if (index > 0) {
                    completeStep(steps[index - 1].first)
                }

                val waitTime = when (index) {
                    0 -> 3000L   // Upload (fast)
                    1 -> 8000L   // AI analysis (slow - may include retry wait)
                    2 -> 4000L   // Fun facts
                    3 -> 2000L   // Save
                    else -> 2000L
                }

                delay(waitTime)
            }

            // If processing finishes naturally, sit at 90% until real result comes in
            if (_binding != null && !processingDone) {
                binding.tvProgressLabel.text = "Finishing up..."
            }
        }
    }

    private fun activateStep(tv: TextView) {
        if (_binding == null) return
        tv.alpha = 1f
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
        tv.setCompoundDrawableTintList(
            ContextCompat.getColorStateList(requireContext(), R.color.brand_primary)
        )
        tv.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun completeStep(tv: TextView) {
        if (_binding == null) return
        tv.alpha = 1f
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
        // Change icon to checkmark
        val checkDrawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.checkbox_on_background)
        checkDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.brand_primary))
        tv.setCompoundDrawablesWithIntrinsicBounds(checkDrawable, null, null, null)
        tv.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun completeAllSteps() {
        if (_binding == null) return
        completeStep(binding.tvStep1)
        completeStep(binding.tvStep2)
        completeStep(binding.tvStep3)
        completeStep(binding.tvStep4)
        animateProgress(100)
        binding.tvProgressLabel.text = "Complete!"
        binding.tvProgressPercent.text = "100%"
        binding.tvStatusTitle.text = "Journal created!"
        binding.tvStatusSubtitle.text = "Your travel memory has been saved"
        binding.ivAiIcon.clearAnimation()
    }

    private fun animateProgress(target: Int) {
        if (_binding == null) return
        val animator = ObjectAnimator.ofInt(binding.progressBar, "progress", binding.progressBar.progress, target)
        animator.duration = 600
        animator.start()
        binding.tvProgressPercent.text = "$target%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
