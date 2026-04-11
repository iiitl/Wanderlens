package com.example.wanderlens.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.wanderlens.MainActivity
import com.example.wanderlens.databinding.FragmentRegisterBinding
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun Update(score : Int){
        if(score==0){
            binding.progressindicator.setIndicatorColor(Color.TRANSPARENT)
            binding.progressindicator.setProgress(0)
        }
        if(score==1 || score==2){
            binding.progressindicator.setIndicatorColor(Color.RED)
            binding.progressindicator.setProgress(25)
        }
        if(score==3 || score==4){
            binding.progressindicator.setIndicatorColor(Color.YELLOW)
            binding.progressindicator.setProgress(75)
        }
        if(score==5){
            binding.progressindicator.setIndicatorColor(Color.GREEN)
            binding.progressindicator.setProgress(100)
        }



    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLoginNow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.etPassword.doAfterTextChanged { s ->
            val text=s.toString();
            var score=0;
            if(text.length>0) score++;
            if(text.length>=6) score++;
            if(text.count { !it.isLetterOrDigit() }>0) score++;
            if(text.count { it.isDigit() }>0) score++;
            if(text.count{it.isLetter()}>0) score++;
            Update(score);

        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            if (name.isNotBlank() && email.isNotBlank() && pass.isNotBlank()) {
                viewModel.register(name, email, pass)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.btnRegister.isEnabled = false
                            binding.btnRegister.text = "Loading..."
                        }
                        is Resource.Success -> {
                            // Registration successful, log in automatically or navigate
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                        is Resource.Error -> {
                            binding.btnRegister.isEnabled = true
                            binding.btnRegister.text = "Create Account"
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        null -> Unit
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
