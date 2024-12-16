package com.app.homestyle.ui.gallery

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.ui.arview.ArFragment
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.CaptureViewModel
import com.app.homestyle.viewmodel.CaptureViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {

    private lateinit var captureViewModel: CaptureViewModel
    private lateinit var sessionManager: SessionManager

    private var string: String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el ViewModel
        val repository = (requireActivity().application as MyApplication).captureRepository
        val factory = CaptureViewModelFactory(repository)
        captureViewModel = ViewModelProvider(this, factory)[CaptureViewModel::class.java]

        // Inicializar el SessionManager
        sessionManager = SessionManager(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_gallery)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Verificar usuario actual y observar capturas
        lifecycleScope.launch {
            val currentUserEmail = sessionManager.loggedInUser.first()
            if (currentUserEmail != null) {
                captureViewModel.fetchCapturesForUser(currentUserEmail)
                captureViewModel.captures.observe(viewLifecycleOwner) { captures ->
                    if (captures.isEmpty()) {
                        Log.d("GalleryFragment", "No hay capturas para este usuario")
                        string= getString(R.string.notscreenshot)
                        Toast.makeText(requireContext(),string, Toast.LENGTH_SHORT).show()
                    } else {
                        captures.forEach {
                            Log.d("GalleryFragment", "Ruta de captura: ${it.imagePath}")
                        }
                        val images = captures.map { BitmapFactory.decodeFile(it.imagePath) }
                        recyclerView.adapter = ImageAdapter(images)
                    }
                }
            } else {
                Log.e("GalleryFragment", "No hay usuario logueado")
                string= getString(R.string.require_loggallery)
                Toast.makeText(requireContext(),string, Toast.LENGTH_SHORT).show()
            }
        }
    }
}



