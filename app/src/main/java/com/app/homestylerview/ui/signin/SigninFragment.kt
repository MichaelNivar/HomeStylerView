package com.app.homestylerview.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.homestylerview.R
import com.app.homestylerview.databinding.FragmentProfileBinding
import com.app.homestylerview.databinding.FragmentSigninBinding
import com.app.homestylerview.repository.UserRepository
import com.app.homestylerview.ui.login.LoginFragment
import com.app.homestylerview.viewmodel.UserArViewModel
import com.app.homestylerview.viewmodel.UserArViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SigninFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SigninFragment : Fragment() {
    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserArViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = UserRepository() // Instancia tu repositorio aquí
        val factory = UserArViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserArViewModel::class.java)
// Configurar el botón de registro
        binding.bRegister.setOnClickListener {
            val nombre = binding.etRegisterFullname.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            if (nombre.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
                viewModel.registerUser(nombre, email, password)
            } else {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }

        // Observa el estado del registro
        lifecycleScope.launchWhenStarted {
            viewModel.registrationState.collect { result ->
                result?.onSuccess {
                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val fragment = LoginFragment()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()

                }?.onFailure { e ->
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}