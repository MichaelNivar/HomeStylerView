package com.app.homestyle.ui.arview

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.model.Capture
import com.app.homestyle.model.Product
import com.app.homestyle.ui.product.ProductBottomSheetFragment
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.CaptureViewModel
import com.app.homestyle.viewmodel.CaptureViewModelFactory
import com.app.homestyle.viewmodel.ProductViewModel
import com.app.homestyle.viewmodel.ProductViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Light
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private var model: Renderable? = null
    private var currentModelPath: String? = null
    private val placedAnchorNodes = mutableListOf<AnchorNode>() // Para rastrear los nodos colocados
    private val capturedImages = mutableListOf<Bitmap>() // Lista para guardar las capturas

    private lateinit var captureViewModel: CaptureViewModel
    private lateinit var sessionManager: SessionManager
    private var currentUserEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as MyApplication).productRepository
        val factory = ProductViewModelFactory(repository)
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        val repositoryCapture = (requireActivity().application as MyApplication).captureRepository
        val factoryCapture = CaptureViewModelFactory(repositoryCapture)
        captureViewModel = ViewModelProvider(this, factoryCapture)[CaptureViewModel::class.java]

        sessionManager = SessionManager(requireContext())

        lifecycleScope.launch {
            currentUserEmail = sessionManager.loggedInUser.first()
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_products)
        fab.setOnClickListener {
            openProductMenu()
        }

        val fabCamera = view.findViewById<FloatingActionButton>(R.id.fab_screenshot)
        fabCamera.setOnClickListener {
            captureScreenshot()
        }

        currentModelPath = arguments?.getString("model_path")
        if (currentModelPath.isNullOrEmpty()) {
            Log.e("ArFragment", "Ruta del modelo no válida")
            return
        }

        configArFragment()

        lifecycleScope.launchWhenCreated {
            loadModel(currentModelPath!!)
        }
    }

    private fun openProductMenu() {
        if (parentFragmentManager.findFragmentByTag("ProductBottomSheet") != null) {
            Log.d("ArFragment", "El menú ya está visible, no se creará una nueva instancia")
            return
        }

        // Observar productos con SingleLiveEvent
        productViewModel.productsEvent.observe(viewLifecycleOwner) { products ->
            val bottomSheet = ProductBottomSheetFragment(products) { selectedProduct ->
                onProductSelected(selectedProduct)
            }
            bottomSheet.show(parentFragmentManager, "ProductBottomSheet")
        }

        productViewModel.fetchAllProducts()
    }

    private fun configArFragment() {
        val arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as? com.google.ar.sceneform.ux.ArFragment
        arFragment?.setOnTapArPlaneListener { hitResult, _, _ ->
            currentModelPath?.let {
                clearScene(arFragment) // Limpio la escena antes de colocar otro modelo
                placeModel(hitResult, it)
            } ?: run {
                val model_notloaded = getString(R.string.model_notloaded)
                Toast.makeText(requireContext(), model_notloaded, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onProductSelected(product: Product) {
        currentModelPath = product.archivoGlbPath
        configArFragment()
        lifecycleScope.launchWhenCreated {
            loadModel(currentModelPath!!)
        }
    }

    private suspend fun loadModel(modelPath: String) {
        try {
            val modelFile = File(modelPath)
            if (!modelFile.exists()) {
                throw FileNotFoundException("El archivo no existe en la ruta: $modelPath")
            }

            model = ModelRenderable.builder()
                .setSource(requireContext(), Uri.fromFile(modelFile))
                .setIsFilamentGltf(true)
                .await()

            Log.d("ArFragment", "Modelo cargado exitosamente desde: $modelPath")
        } catch (e: Exception) {
            Log.e("ArFragment", "Error al cargar el modelo: ${e.message}")
        }
    }

    private fun placeModel(hitResult: HitResult, modelPath: String) {
        if (model == null) {
            val model_notloaded = getString(R.string.model_notloaded)
            Toast.makeText(requireContext(), model_notloaded, Toast.LENGTH_SHORT).show()
            return
        }

        val arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as com.google.ar.sceneform.ux.ArFragment
        val anchorNode = AnchorNode(hitResult.createAnchor())
        val transformableNode = TransformableNode(arFragment.transformationSystem)
        transformableNode.renderable = model
        transformableNode.setParent(anchorNode)

        arFragment.arSceneView.scene.addChild(anchorNode)
        placedAnchorNodes.add(anchorNode)

        Log.d("ArFragment", "Modelo colocado en el plano desde: $modelPath")
    }

    private fun clearScene(arFragment: com.google.ar.sceneform.ux.ArFragment) {
        placedAnchorNodes.forEach { node ->
            arFragment.arSceneView.scene.removeChild(node)
            node.anchor?.detach()
            node.setParent(null)
        }
        placedAnchorNodes.clear()
        Log.d("ArFragment", "Escena limpiada")
    }

    private fun captureScreenshot() {
        if (currentUserEmail.isNullOrEmpty()) {
            val require_login_image = getString(R.string.require_login_image)
            Toast.makeText(
                requireContext(),
                require_login_image,
                Toast.LENGTH_SHORT
            ).show()
            Log.e("ArFragment", "No hay usuario logueado, no se puede capturar pantalla.")
            return
        }

        val arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as? com.google.ar.sceneform.ux.ArFragment
        val arSceneView = arFragment?.arSceneView ?: return

        arSceneView.arFrame?.let { frame ->
            val bitmap = Bitmap.createBitmap(arSceneView.width, arSceneView.height, Bitmap.Config.ARGB_8888)
            PixelCopy.request(arSceneView, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    saveScreenshot(bitmap)
                } else {
                    Log.e("ArFragment", "Error al capturar pantalla: $copyResult")
                }
            }, Handler(Looper.getMainLooper()))
        }
    }


    private fun saveScreenshot(bitmap: Bitmap) {
        val filename = "screenshot_${System.currentTimeMillis()}.png"
        val file = File(requireContext().filesDir, filename)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        currentUserEmail?.let { user ->
            val capture = Capture(userEmail = user, imagePath = file.absolutePath)
            captureViewModel.saveCapture(capture)
            val screenshot_save = getString(R.string.screenshot_save)
            Toast.makeText(requireContext(), screenshot_save, Toast.LENGTH_SHORT).show()
            Log.d("ArFragment", "Captura guardada: ${file.absolutePath}")
        }
    }
}




