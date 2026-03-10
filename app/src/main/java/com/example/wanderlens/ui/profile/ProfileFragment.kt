package com.example.wanderlens.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wanderlens.R
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.databinding.FragmentProfileBinding
import com.example.wanderlens.repository.AuthRepository
import com.example.wanderlens.ui.auth.AuthActivity
import com.example.wanderlens.utils.FirebaseConfig
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        binding.tvUserName.text = user?.displayName ?: "Traveler"
        binding.tvUserEmail.text = user?.email ?: "No email"

        loadStats()

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.nav_settings)
        }

        binding.btnLogout.setOnClickListener {
            authRepository.logout()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadStats() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseConfig.firestore
            .collection("users").document(userId)
            .collection("journals")
            .get()
            .addOnSuccessListener { snapshot ->
                if (_binding == null) return@addOnSuccessListener

                val journals = snapshot.toObjects(JournalEntry::class.java)
                val journalCount = journals.size
                val countryCount = journals.mapNotNull {
                    it.country.takeIf { c -> c.isNotBlank() && c != "World" }
                }.distinct().size

                binding.tvJournalsCount.text = journalCount.toString()
                binding.tvCountriesCount.text = countryCount.toString()
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                binding.tvJournalsCount.text = "0"
                binding.tvCountriesCount.text = "0"
            }
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
