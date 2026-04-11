package com.example.wanderlens.ui.upload

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.example.wanderlens.EntryState
import com.example.wanderlens.R
import com.example.wanderlens.databinding.FragmentUploadBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UploadViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var cameraPhotoFile: File? = null

    private var currentState = EntryState.IDLE

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    selectedImageUri = uri
                    showImagePreview(uri)
                    onImageSelected(uri)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val file = cameraPhotoFile
                if (file != null && file.exists()) {
                    val uri = Uri.fromFile(file)
                    selectedImageUri = uri
                    showImagePreview(uri)
                    onImageSelected(uri)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error capturing photo", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateAnimation(currentState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChoosePhoto.setOnClickListener {
            currentState = EntryState.PICKING
            updateAnimation(currentState)
            openGallery()
        }

        binding.btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                currentState = EntryState.PICKING
                updateAnimation(currentState)
                launchCamera()
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnUploadNow.setOnClickListener {
            val uri = selectedImageUri
            if (uri != null) {
                val file = uriToFile(uri)
                if (file != null) {
                    viewModel.uploadJournal(file)
                    findNavController().navigate(R.id.nav_ai_processing)
                } else {
                    Toast.makeText(context, "File error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Select image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onImageSelected(uri: Uri) {
        currentState = EntryState.UPLOADING
        updateAnimation(currentState)

        // simulate upload delay
        binding.lottieView.postDelayed({
            val success = true
            currentState = if (success) EntryState.SUCCESS else EntryState.ERROR
            updateAnimation(currentState)
        }, 2500)
    }

    private fun updateAnimation(state: EntryState) {
        when (state) {

            EntryState.IDLE -> {
                binding.lottieView.setAnimation(R.raw.photcamera)
                binding.lottieView.repeatCount = LottieDrawable.INFINITE
            }

            EntryState.PICKING -> {
                binding.lottieView.setAnimation(R.raw.click_animation)
                binding.lottieView.repeatCount = 0
            }

            EntryState.UPLOADING -> {
                binding.lottieView.setAnimation(R.raw.uploading)
                binding.lottieView.repeatCount = LottieDrawable.INFINITE
            }

            EntryState.SUCCESS -> {
                binding.lottieView.setAnimation(R.raw.success)
                binding.lottieView.repeatCount = 0
            }

            EntryState.ERROR -> {
                binding.lottieView.setAnimation(R.raw.failed)
                binding.lottieView.repeatCount = 0
            }
        }

        binding.lottieView.playAnimation()
        updateButtons(state)
    }

    private fun updateButtons(state: EntryState) {
        val enabled = state != EntryState.UPLOADING
        binding.btnChoosePhoto.isEnabled = enabled
        binding.btnTakePhoto.isEnabled = enabled
    }

    private fun showImagePreview(uri: Uri) {
        binding.llUploadPrompt.visibility = View.GONE
        binding.ivPreview.visibility = View.VISIBLE
        binding.btnUploadNow.visibility = View.VISIBLE

        Glide.with(requireContext())
            .load(uri)
            .centerCrop()
            .into(binding.ivPreview)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImage.launch(intent)
    }

    private fun launchCamera() {
        val file = createImageFile()
        cameraPhotoFile = file

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        takePhoto.launch(intent)
    }

    private fun createImageFile(): File {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_$time", ".jpg", dir)
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val input = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().cacheDir, "upload.jpg")
            FileOutputStream(file).use { output -> input.copyTo(output) }
            file
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}