package com.app.homestyle.ui.signin

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentSigninBinding
import com.app.homestyle.model.UserAr
import com.app.homestyle.repository.UserRepository
import com.app.homestyle.ui.login.LoginFragment
import com.app.homestyle.utils.AppDatabase
import com.app.homestyle.viewmodel.UserViewModel
import com.app.homestyle.viewmodel.UserViewModelFactory
import java.util.Date
import java.util.UUID

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

    private lateinit var userViewModel: UserViewModel

    private var isPasswordVisible = false

    private var string: String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)

        val userRepository = (requireActivity().application as MyApplication).userRepository
        val viewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        binding.bRegister.setOnClickListener { handleRegisterUser() }

        binding.tvLoginlink.setOnClickListener {
            openFragment(LoginFragment())
        }
        setupPasswordToggle()
        return binding.root
    }

    private fun handleRegisterUser() {
        val fullName = binding.etRegisterFullname.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            string= getString(R.string.required)
            Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            return
        }else {

            val newUser = UserAr(
                idUsuario = UUID.randomUUID().toString(),
                nombre = fullName,
                email = email,
                password = password,
                fechaCreacion = Date()
            )

            userViewModel.insert(newUser)
            Log.d("Signin", "Email: ${newUser.email} Password: ${newUser.password}")

            string= getString(R.string.succesful_user)
            Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT)
                .show()

            binding.etRegisterFullname.text.clear()
            binding.etRegisterEmail.text.clear()
            binding.etRegisterPassword.text.clear()

            openFragment(LoginFragment())
        }
    }

    private fun setupPasswordToggle() {
        binding.etRegisterPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etRegisterPassword.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.etRegisterPassword.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
        } else {
            binding.etRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)
        }
        binding.etRegisterPassword.setSelection(binding.etRegisterPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }
    private fun openFragment(fragment:Fragment){
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