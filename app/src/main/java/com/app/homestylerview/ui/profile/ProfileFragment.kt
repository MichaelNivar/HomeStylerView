package com.app.homestylerview.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.app.homestylerview.R
import com.app.homestylerview.databinding.FragmentProfileBinding
import com.app.homestylerview.repository.UserRepository
import com.app.homestylerview.ui.login.LoginFragment
import com.app.homestylerview.ui.signin.SigninFragment
import com.app.homestylerview.viewmodel.UserArViewModel
import com.app.homestylerview.viewmodel.UserArViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect

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
    private lateinit var viewModel: UserArViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = UserRepository()
        val factory = UserArViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserArViewModel::class.java)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            binding.rlAccess.visibility = View.VISIBLE
            binding.rlAcceded.visibility = View.GONE
        } else {
            binding.rlAcceded.visibility = View.VISIBLE
            binding.rlAccess.visibility = View.GONE
            fetchUserData(currentUser.uid)
        }

        setupClickListeners()
    }

    private fun fetchUserData(userId: String) {
        viewModel.fetchUserById(userId)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUsername.text = user.nombre
            } else {
                Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        val login: Button = binding.buttonLogin
        login.setOnClickListener {
            val loginFragment = LoginFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .addToBackStack(null)
                .commit()
        }

        val register: Button = binding.buttonRegister
        register.setOnClickListener {
            val registerFragment = SigninFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, registerFragment)
                .addToBackStack(null) // Añade la transacción al back stack para poder regresar
                .commit()
        }

        val logout: Button = binding.bLogout
        logout.setOnClickListener {
            viewModel.logoutUser()
            binding.rlAccess.visibility = View.VISIBLE
            binding.rlAcceded.visibility = View.GONE
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}