package com.app.homestyle.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentHomeBinding
import com.app.homestyle.ui.category.CategoryAdapter
import com.app.homestyle.ui.product.ProductSearchResultsFragment
import com.app.homestyle.viewmodel.CategoryViewModel
import com.app.homestyle.viewmodel.CategoryViewModelFactory
import com.app.homestyle.viewmodel.ProductViewModel
import com.app.homestyle.viewmodel.ProductViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory: String? = null
    private var selectedProduct: String? = null

    private var string: String= ""
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inicializar ViewModel
        val category_repository = (requireActivity().application as MyApplication).categoryRepository
        val category_factory = CategoryViewModelFactory(category_repository)
        categoryViewModel = ViewModelProvider(this, category_factory)[CategoryViewModel::class.java]

        val product_repository = (requireActivity().application as MyApplication).productRepository
        val product_factory = ProductViewModelFactory(product_repository)
        productViewModel = ViewModelProvider(this, product_factory)[ProductViewModel::class.java]

        //productViewModel.fetchAllProducts()
        setupCategorySpinner()
        setupAutoCompleteSearch()
        setClickListener()
        return binding.root
    }
    private fun setClickListener(){
        binding.ibSearch.setOnClickListener {
            val category = selectedCategory
            val product = selectedProduct

            when {
                !product.isNullOrEmpty() && !category.isNullOrEmpty() -> {
                    // Buscar por categoría y nombre
                    productViewModel.searchByCategoryAndName(category, product)
                }
                !category.isNullOrEmpty() -> {
                    // Buscar solo por categoría
                    productViewModel.getProductsByCategoryId(category)
                }
                !product.isNullOrEmpty() -> {
                    // Buscar solo por nombre
                    productViewModel.searchByName(product)
                }
                else -> {
                    string= getString(R.string.require_select)
                    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
                }
            }
        }
        observeSearchResults()
    }

    private fun observeSearchResults() {
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            val fragment = ProductSearchResultsFragment.newInstance(products)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_container, fragment)
                .addToBackStack(null)
                .commit()
        }
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

    private fun setupAutoCompleteSearch() {
        // Llamar a fetchAllProducts para garantizar que los productos estén cargados
        //productViewModel.fetchAllProducts()

        // Observar allProducts para configurar el autocompletar
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                val productNames = products.map { it.nombreProducto }

                // Crear el adaptador para AutoCompleteTextView
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productNames)
                binding.etSearch.setAdapter(adapter)

                // Opcional: Manejar el evento de selección
                binding.etSearch.setOnItemClickListener { parent, view, position, id ->
                    selectedProduct = parent.getItemAtPosition(position).toString()
                }
            } else {
                Log.d("HomeFragment", "No hay productos disponibles para autocompletar")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}