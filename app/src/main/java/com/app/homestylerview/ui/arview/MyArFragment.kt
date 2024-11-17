package com.app.homestylerview.ui.arview

import com.app.homestylerview.R
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyArFragment : Fragment() {
    private lateinit var arFragment: ArFragment
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene

    private var model: Renderable? = null
    private var modelView: ViewRenderable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MyArFragment", "Fragment de AR iniciado correctamente")
        Toast.makeText(requireContext(), "Fragment de AR cargado", Toast.LENGTH_SHORT).show()

        // Cargo el ArFragment dentro del Fragment
        arFragment = (childFragmentManager.findFragmentById(R.id.arFragment) as ArFragment).apply {
            setOnSessionConfigurationListener { session, config ->

            }
            setOnViewCreatedListener { arSceneView ->
                arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
            }
            setOnTapArPlaneListener(::onTapPlane)
        }

        // Cargar los modelos en segundo plano
        lifecycleScope.launchWhenCreated {
            loadModels()
        }
    }
    // Configuración de iluminación
    private fun setupLighting() {
        scene.addChild(Node().apply {
            light = Light.builder(Light.Type.DIRECTIONAL)
                .setColor(Color(1.0f, 1.0f, 1.0f)) // Luz blanca
                .setIntensity(1.0f)
                .setShadowCastingEnabled(true)
                .build()
        })
    }
    private suspend fun loadModels() {
        /*model = ModelRenderable.builder()
            .setSource(requireContext(), Uri.parse("file:///android_asset/sofa_single.glb"))
            .setIsFilamentGltf(true)
            .await()
        modelView = ViewRenderable.builder()
            .setView(requireContext(), R.layout.view_renderable_infos)
            .await()*/
        try {
            model = ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("file:///android_asset/seat.glb"))
                .setIsFilamentGltf(true)
                .await()
            modelView = ViewRenderable.builder()
                .setView(requireContext(), R.layout.view_renderable_infos)
                .await()
        } catch (e: Exception) {
            Log.e("MyArFragment", "Error al cargar el modelo: ${e.message}")
            Toast.makeText(requireContext(), "Error al cargar el modelo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if (model == null || modelView == null) {
            Toast.makeText(context, "Cargando modelo...", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un AnchorNode en la ubicación donde se tocó el plano
        scene.addChild(AnchorNode(hitResult.createAnchor()).apply {
            addChild(TransformableNode(arFragment.transformationSystem).apply {
                renderable = model
                localScale = Vector3(0.5f, 0.5f, 0.5f) // escala del modelo
                localRotation = Quaternion.axisAngle(Vector3(1.0f, 0.0f, 0.0f), 90f) // Rotación del modelo
                renderableInstance.setCulling(false) // Evitar que el modelo desaparezca al moverse
                renderableInstance.animate(true).start() // Iniciar animación

                // Agregar el ViewRenderable (modelView) encima del modelo
                addChild(Node().apply {
                    localPosition = Vector3(0.0f, 1f, 0.0f) // Posición relativa del ViewRenderable
                    localScale = Vector3(0.7f, 0.7f, 0.7f) // Escala del ViewRenderable
                    renderable = modelView
                })
            })
        })
    }

}