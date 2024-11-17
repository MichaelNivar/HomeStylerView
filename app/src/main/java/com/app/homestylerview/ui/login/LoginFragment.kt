package com.app.homestylerview.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.homestylerview.R
import com.app.homestylerview.databinding.FragmentLoginBinding
import com.app.homestylerview.databinding.FragmentSigninBinding
import com.app.homestylerview.repository.UserRepository
import com.app.homestylerview.ui.home.HomeFragment
import com.app.homestylerview.viewmodel.UserArViewModel
import com.app.homestylerview.viewmodel.UserArViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.io.InputStream

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
    private lateinit var viewModel: UserArViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Llama a la función para subir el archivo

        val repository = UserRepository() // Instancia tu repositorio aquí
        val factory = UserArViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserArViewModel::class.java)

        // Configurar el botón de inicio de sesión
        binding.bLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Observa el estado de autenticación
        lifecycleScope.launchWhenStarted {
            viewModel.authState.collect { result ->
                result?.onSuccess {
                    Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Redirigir al usuario a la pantalla principal
                    val homeFragment=HomeFragment()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit()
                    //findNavController().navigate(R.id.action_signinFragment_to_dashboardFragment)
                }?.onFailure { e ->
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //uploadGLBFile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun uploadGLBFile() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val glbRef = storageRef.child("models/sofa_single.glb") // Ruta en Firebase Storage

        try {
            // Accede al archivo .glb desde la carpeta assets
            val inputStream: InputStream? = context?.assets?.open("sofa_single.glb")
            if (inputStream != null) {
                val fileData = inputStream.readBytes()

                // Sube el archivo a Firebase Storage
                val uploadTask = glbRef.putBytes(fileData)
                uploadTask.addOnSuccessListener {
                    Toast.makeText(context, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { exception ->
                    Log.e("UploadGLBFragment", "Error al subir el archivo", exception)
                    Toast.makeText(context, "Error al subir el archivo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("UploadGLBFragment", "El archivo no se encontró en assets")
                Toast.makeText(context, "El archivo no se encontró en assets", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Log.e("UploadGLBFragment", "Error al leer el archivo .glb", e)
        }
    }

}