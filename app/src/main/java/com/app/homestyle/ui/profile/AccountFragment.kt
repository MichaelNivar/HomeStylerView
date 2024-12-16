package com.app.homestyle.ui.profile

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentAccountBinding
import com.app.homestyle.databinding.FragmentProfileBinding
import com.app.homestyle.model.UserAr
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.UserViewModel
import com.app.homestyle.viewmodel.UserViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager
    private var currentUser: UserAr? = null
    private var isPasswordVisible = false
    private var byteArray: ByteArray? = null
    private var string: String= ""
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            byteArray = inputStream?.readBytes() // Actualizar byteArray con la nueva imagen
            byteArray?.let { bytes ->
                binding.imAccprofile.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            byteArray = byteArrayOutputStream.toByteArray() // Actualizar byteArray con la nueva imagen
            binding.imAccprofile.setImageBitmap(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = (requireActivity().application as MyApplication).userRepository
        val viewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        sessionManager = SessionManager(requireContext())

        // Obtener y mostrar los datos del usuario actual
        lifecycleScope.launch {
            val email = sessionManager.loggedInUser.first()
            email?.let {
                userViewModel.getUserById(it).observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        currentUser = user
                        loadUserData(user)
                    }
                }
            }
        }

        binding.imAccprofile.setOnClickListener {
            showImageOptions()
        }

        binding.bSave.setOnClickListener {
            saveUserData()
        }

        setupPasswordToggle()
    }

    private fun setupPasswordToggle() {
        binding.etAccountPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etAccountPassword.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.etAccountPassword.right - drawableEnd.bounds.width())) {
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
            binding.etAccountPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etAccountPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
        } else {
            // Mostrar contraseña
            binding.etAccountPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etAccountPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0)
        }
        // Mover el cursor al final del texto
        binding.etAccountPassword.setSelection(binding.etAccountPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun loadUserData(user: UserAr) {
        binding.etAccountFullname.setText(user.nombre)
        binding.etAccountEmail.setText(user.email)
        binding.etAccountPassword.setText(user.password)

        if (user.imagenPerfil != null) {
            val bitmap = BitmapFactory.decodeByteArray(user.imagenPerfil, 0, user.imagenPerfil.size)
            binding.imAccprofile.setImageBitmap(bitmap)
            byteArray = user.imagenPerfil
        } else {
            binding.imAccprofile.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun showImageOptions() {
        val choose_gallery= getString(R.string.choose_gallery)
        val choose_camera= getString(R.string.choose_camera)
        val change_image= getString(R.string.change_image)

        val options = arrayOf(choose_gallery, choose_camera)
        AlertDialog.Builder(requireContext())
            .setTitle(change_image)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImage.launch("image/*") // Elegir de galería
                    1 -> takePicture.launch() // Tomar foto
                }
            }
            .show()
    }

    private fun saveUserData() {
        val name = binding.etAccountFullname.text.toString().trim()
        val email = binding.etAccountEmail.text.toString().trim()
        val password = binding.etAccountPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            string= getString(R.string.required_update)
            Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            return
        }

        currentUser?.let { user ->
            val updatedUser = user.copy(
                nombre = name,
                email = email,
                password = password,
                imagenPerfil = byteArray ?: user.imagenPerfil // Usar la imagen actual si no se seleccionó una nueva
            )
            userViewModel.insert(updatedUser)
            string= getString(R.string.update_save)
            Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()

            // Actualizar la sesión si se cambia el correo electrónico
            lifecycleScope.launch {
                sessionManager.saveUserSession(email)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
