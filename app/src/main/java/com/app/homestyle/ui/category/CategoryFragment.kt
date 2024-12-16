package com.app.homestyle.ui.category

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentCategoryBinding
import com.app.homestyle.model.Category
import com.app.homestyle.repository.CategoryRepository
import com.app.homestyle.viewmodel.CategoryViewModel
import com.app.homestyle.viewmodel.CategoryViewModelFactory
import com.app.homestyle.viewmodel.UserViewModel
import com.app.homestyle.viewmodel.UserViewModelFactory
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private var selectedImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        // Inicializar ViewModel
        val repository = (requireActivity().application as MyApplication).categoryRepository
        val factory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        setupRecyclerView()
        setupAddCategory()
        setupImagePicker()

        return binding.root
    }
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(emptyList())
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategory.adapter = categoryAdapter

        // Cargar las categorías desde el ViewModel
        categoryViewModel.getAllCategories { categories ->
            categoryAdapter = CategoryAdapter(categories)
            binding.rvCategory.adapter = categoryAdapter
        }
    }

    private fun setupAddCategory() {
        binding.bAddcat.setOnClickListener {
            val categoryName = binding.etAddcat.text.toString().trim()

            if (categoryName.isEmpty()) {
                val require_catname = getString(R.string.require_catname)
                Toast.makeText(requireContext(), require_catname, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImage == null) {
                val require_image = getString(R.string.require_image)
                Toast.makeText(requireContext(), require_image, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertir la imagen a ByteArray
            val imageByteArray = bitmapToByteArray(selectedImage!!)

            // Crear y agregar la categoría
            val newCategory = Category(
                idCategoria = System.currentTimeMillis().toString(),
                nombreCategoria = categoryName,
                imagen = imageByteArray
            )

            categoryViewModel.insertCategory(newCategory)
            val succesful_cat= getString(R.string.succesful_cat)
            Toast.makeText(requireContext(), succesful_cat, Toast.LENGTH_SHORT).show()

            // Limpiar campos
            binding.etAddcat.text.clear()
            binding.imImage.setImageResource(R.drawable.ic_image_selected)
            selectedImage = null

            // Recargar categorías
            categoryViewModel.getAllCategories { categories ->
                categoryAdapter = CategoryAdapter(categories)
                binding.rvCategory.adapter = categoryAdapter
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

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}