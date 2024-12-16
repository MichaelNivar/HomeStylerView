package com.app.homestyle.ui.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentProductBinding
import com.app.homestyle.model.Product
import com.app.homestyle.viewmodel.CategoryViewModel
import com.app.homestyle.viewmodel.CategoryViewModelFactory
import com.app.homestyle.viewmodel.ProductViewModel
import com.app.homestyle.viewmodel.ProductViewModelFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var selectedCategory: String
    private var selectedGlbFile: ByteArray? = null
    private var selectedImage: Bitmap? = null

    private var string: String= ""
    //private var selectedCategoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductBinding.inflate(inflater, container, false)

        val repository = (requireActivity().application as MyApplication).productRepository
        val factory = ProductViewModelFactory(repository)
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        val catrepository = (requireActivity().application as MyApplication).categoryRepository
        val catfactory = CategoryViewModelFactory(catrepository)
        categoryViewModel = ViewModelProvider(this, catfactory)[CategoryViewModel::class.java]

        setupCategorySpinner()
        setupImagePicker()
        setupGlbPicker()
        setupAddProductButton()

        return binding.root
    }
    private fun setupCategorySpinner() {
        categoryViewModel.getAllCategories { categories ->
            val categoryNames = categories.map { it.nombreCategoria }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategory.adapter = adapter

            binding.spCategory.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedCategory = categories[position].nombreCategoria
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun setupImagePicker() {
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                    selectedImage = BitmapFactory.decodeStream(inputStream)
                    binding.imImage.setImageBitmap(selectedImage)
                }
            }
        }

        binding.bImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }
    }

    private fun setupGlbPicker() {
        val pickGlbLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
                val glbData = inputStream?.readBytes()
                if (glbData != null) {
                    // Guardar el archivo GLB en el almacenamiento interno
                    val fileName = "model_${System.currentTimeMillis()}.glb"
                    val glbPath = saveFileToInternalStorage(requireContext(), fileName, glbData)
                    selectedGlbFile = glbPath.toByteArray() // Almacenar temporalmente la ruta como ByteArray
                    binding.imGlb.setImageResource(R.drawable.ic_file_selected) // Icono para archivo seleccionado
                }
            }
        }

        binding.bGlb.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/octet-stream"
            }
            pickGlbLauncher.launch("application/octet-stream")
        }
    }

    private fun setupAddProductButton() {
        binding.bAddproduct.setOnClickListener {
            val productName = binding.etAddprodcut.text.toString().trim()
            val productDescription = binding.etDescription.text.toString().trim()

            if (productName.isEmpty() || productDescription.isEmpty() || selectedCategory.isEmpty()) {
                string= getString(R.string.required_prod)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedGlbFile == null || selectedImage == null) {
                string= getString(R.string.required_glbimg)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertir Bitmap a ByteArray
            val imageByteArray = bitmapToByteArray(selectedImage!!)

            val newProduct = Product(
                idProducto = System.currentTimeMillis().toString(),
                nombreProducto = productName,
                descripcion = productDescription,
                archivoGlbPath = String(selectedGlbFile!!),
                imagen = imageByteArray,
                categoriaId = selectedCategory
            )

            productViewModel.insertProduct(newProduct)
            string= getString(R.string.succesful_prod)
            Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()

            // Limpiar los campos
            binding.etAddprodcut.text.clear()
            binding.etDescription.text.clear()
            binding.imImage.setImageResource(0)
            binding.imGlb.setImageResource(0)
            selectedImage = null
            selectedGlbFile = null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun saveFileToInternalStorage(context: Context, fileName: String, data: ByteArray): String {
        val file = File(context.filesDir, fileName)
        val fos = FileOutputStream(file)
        fos.write(data)
        fos.close()
        return file.absolutePath
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}