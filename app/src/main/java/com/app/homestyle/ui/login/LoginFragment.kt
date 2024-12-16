package com.app.homestyle.ui.login

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentLoginBinding
import com.app.homestyle.ui.profile.ProfileFragment
import com.app.homestyle.ui.signin.SigninFragment
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.UserViewModel
import com.app.homestyle.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager
    private var isPasswordVisible = false // Estado de visibilidad de la contraseña

    private var string: String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val userRepository = (requireActivity().application as MyApplication).userRepository
        val viewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        sessionManager = SessionManager(requireContext())

        binding.bLogin.setOnClickListener { handleLogin() }
        binding.tvRegisterlink.setOnClickListener {
            openFragment(SigninFragment())
        }

        // Configurar el ícono del ojo en el campo de contraseña
        setupPasswordToggle()

        return binding.root
    }

    private fun setupPasswordToggle() {
        binding.etLoginPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etLoginPassword.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.etLoginPassword.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            binding.etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etLoginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
        } else {
            // Mostrar contraseña
            binding.etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etLoginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)
        }
        // Mover el cursor al final del texto
        binding.etLoginPassword.setSelection(binding.etLoginPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun handleLogin() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            string= getString(R.string.required)
            Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            return
        }

        userViewModel.getUserById(email).observe(viewLifecycleOwner) { user ->
            if (user != null && user.password == password) {
                lifecycleScope.launch {
                    sessionManager.saveUserSession(email)
                    // Fuerzo actualizacion en profile
                    val currentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
                    if (currentFragment is ProfileFragment) {
                        currentFragment.checkUserSession()
                    }
                }

                openFragment(ProfileFragment())
                string= getString(R.string.succesful_login)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            } else {
                string= getString(R.string.failed_login)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
