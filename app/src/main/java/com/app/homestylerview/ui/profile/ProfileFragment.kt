package com.app.homestylerview.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.homestylerview.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verifica si el usuario está autenticado
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            showProfileLayout()
        } else {
            showRegisterLayout()
        }

        // registro del usuario
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                // Guarda el estado de inicio de sesión
                sharedPreferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userName", name)
                    .putString("userEmail", email)
                    .apply()

                Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                showProfileLayout()
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRegisterLayout() {
        binding.layoutRegister.visibility = View.VISIBLE
        binding.layoutProfile.visibility = View.GONE
    }

    private fun showProfileLayout() {
        val name = sharedPreferences.getString("userName", "Usuario")
        val email = sharedPreferences.getString("userEmail", "correo@ejemplo.com")

        binding.tvUserName.text = name
        binding.tvUserEmail.text = email

        binding.layoutRegister.visibility = View.GONE
        binding.layoutProfile.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}