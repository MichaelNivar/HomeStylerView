package com.app.homestyle.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.homestyle.MainActivity
import com.app.homestyle.MainActivity2
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentProfileBinding
import com.app.homestyle.ui.home.HomeFragment
import com.app.homestyle.ui.login.LoginFragment
import com.app.homestyle.ui.signin.SigninFragment
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.UserViewModel
import com.app.homestyle.viewmodel.UserViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    private var string: String= ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = (requireActivity().application as MyApplication).userRepository
        val viewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        sessionManager = SessionManager(requireContext())

        checkUserSession()

        setupClickListeners()

    }

    private fun setupClickListeners() {
        val login: Button = binding.buttonLogin
        login.setOnClickListener {
            openFragment(LoginFragment())
        }

        val register: Button = binding.buttonRegister
        register.setOnClickListener {
            openFragment(SigninFragment())
        }

        val logout: Button = binding.bLogout
        logout.setOnClickListener {
            lifecycleScope.launch {
                val loggedInUser = sessionManager.loggedInUser.first()

                // Verificar si el usuario es admin antes de limpiar la sesión
                if (loggedInUser == "admin") {
                    sessionManager.clearUserSession()

                    // Lanza MainActivity para el usuario general
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // Finaliza el fragmento actual
                    requireActivity().finish()
                } else {
                    // Procede con la lógica estándar de cierre de sesión
                    sessionManager.clearUserSession()

                    // Cambiar la visibilidad de las vistas
                    binding.rlAccess.visibility = View.VISIBLE
                    binding.rlAcceded.visibility = View.GONE
                    string = getString(R.string.logout)
                    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.acbUserconfig.setOnClickListener {
            openFragment(AccountFragment())
        }

        binding.acbAppconfig.setOnClickListener {
            openFragment(InfoFragment())
        }

        binding.acbAdmin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity2::class.java)
            startActivity(intent)
        }
        binding.acbMain.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    internal fun checkUserSession() {
        lifecycleScope.launch {
            val loggedInUser = sessionManager.loggedInUser.first() // Obtiene el ID almacenado
            if (loggedInUser != null) {
                // Usuario logueado, mostrar la interfaz de usuario logueada
                binding.rlAcceded.visibility = View.VISIBLE
                binding.rlAccess.visibility = View.GONE


                // Paso el id del usuario
                userViewModel.getUserById(loggedInUser).observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        binding.tvUsername.text = user.nombre
                        binding.tvUserEmail.text = user.email

                        if (user.imagenPerfil != null) {
                            val bitmap = BitmapFactory.decodeByteArray(user.imagenPerfil, 0, user.imagenPerfil.size)
                            binding.imProfile.setImageBitmap(bitmap)
                        } else {
                            binding.imProfile.setImageResource(R.drawable.ic_profile)
                        }

                        // Verificar si el usuario es administrador
                        if (user.email == "admin") { // Identifica al administrador
                            binding.acbAdmin.visibility = View.VISIBLE
                            binding.acbMain.visibility= View.VISIBLE
                        } else {
                            binding.acbAdmin.visibility = View.GONE
                            binding.acbMain.visibility= View.GONE
                        }
                    } else {
                        binding.acbAdmin.visibility = View.GONE
                        binding.acbMain.visibility= View.GONE
                    }
                }
            } else {
                // Usuario no logueado, mostrar la interfaz de login
                binding.rlAccess.visibility = View.VISIBLE
                binding.rlAcceded.visibility = View.GONE
                binding.acbAdmin.visibility = View.GONE
                binding.acbMain.visibility= View.GONE
            }
        }
    }



    private fun fetchUserData(userId: String) {
        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUsername.text = user.nombre
                binding.tvUserEmail.text = user.email

                if (user.imagenPerfil != null) {
                    val bitmap = BitmapFactory.decodeByteArray(user.imagenPerfil, 0, user.imagenPerfil.size)
                    binding.imProfile.setImageBitmap(bitmap)
                } else {
                    binding.imProfile.setImageResource(R.drawable.ic_profile)
                }



            } else {
                string= getString(R.string.user_notfound)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        checkUserSession() // Verifica la sesión y actualiza la interfaz al volver al fragmento
    }
}
