package com.example.wanderlens.ui.upload

import android.Manifest
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
import com.bumptech.glide.Glide
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

    // Gallery picker
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    selectedImageUri = uri
                    showImagePreview(uri)
                    Log.d("UploadFragment", "Image selected from gallery: $uri")
                }
            }
        } catch (e: Exception) {
            Log.e("UploadFragment", "Error handling gallery result", e)
            if (context != null) {
                Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera capture
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val file = cameraPhotoFile
                if (file != null && file.exists() && file.length() > 0) {
                    val uri = Uri.fromFile(file)
                    selectedImageUri = uri
                    showImagePreview(uri)
                    Log.d("UploadFragment", "Photo captured: ${file.absolutePath}")
                }
            }
        } catch (e: Exception) {
            Log.e("UploadFragment", "Error handling camera result", e)
            if (context != null) {
                Toast.makeText(context, "Error capturing photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera permission
    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
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

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChoosePhoto.setOnClickListener {
            openGallery()
        }

        binding.btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                launchCamera()
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnChangePhoto.setOnClickListener {
            openGallery()
        }

        binding.btnRetakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                launchCamera()
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        // Tap on the preview card to re-select
        binding.cvPhotoSelector.setOnClickListener {
            if (selectedImageUri != null) {
                // Already has image, show options
                openGallery()
            }
        }

        binding.btnUploadNow.setOnClickListener {
            val uri = selectedImageUri
            if (uri != null) {
                val imageFile = uriToFile(uri)
                if (imageFile != null && imageFile.length() > 0) {
                    Log.d("UploadFragment", "Uploading file: ${imageFile.absolutePath}, size: ${imageFile.length()}")
                    viewModel.uploadJournal(imageFile)
                    findNavController().navigate(R.id.nav_ai_processing)
                } else {
                    Toast.makeText(requireContext(), "Failed to read image file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select a photo first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePreview(uri: Uri) {
        if (_binding == null) return

        binding.llUploadPrompt.visibility = View.GONE
        binding.ivPreview.visibility = View.VISIBLE
        binding.llChangePhoto.visibility = View.VISIBLE
        binding.btnUploadNow.visibility = View.VISIBLE

        Glide.with(requireContext())
            .load(uri)
            .centerCrop()
            .into(binding.ivPreview)

        viewModel.setSelectedImageUri(uri)
    }

    private fun openGallery() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
            pickImage.launch(intent)
        } catch (e: Exception) {
            Log.e("UploadFragment", "Error launching gallery", e)
            Toast.makeText(context, "Cannot open gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            cameraPhotoFile = photoFile
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            takePhoto.launch(intent)
        } catch (e: Exception) {
            Log.e("UploadFragment", "Error launching camera", e)
            Toast.makeText(context, "Cannot open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("WL_${timestamp}_", ".jpg", storageDir)
    }

    /**
     * Copies the content from a content:// URI into a temporary file in the app's cache directory.
     */
    private fun uriToFile(uri: Uri): File? {
        // If the URI is already a file:// URI (from camera), just return the file
        if (uri.scheme == "file") {
            val path = uri.path ?: return null
            return File(path)
        }

        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            file
        } catch (e: Exception) {
            Log.e("UploadFragment", "Error converting URI to file", e)
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
