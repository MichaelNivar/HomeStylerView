package com.app.homestyle.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentCategoryBinding
import com.app.homestyle.databinding.FragmentProductBinding
import com.app.homestyle.databinding.FragmentProductListBinding
import com.app.homestyle.ui.category.CategoryAdapter
import com.app.homestyle.viewmodel.ProductViewModel
import com.app.homestyle.viewmodel.ProductViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductListBinding.inflate(inflater, container, false)

        val repository = (requireActivity().application as MyApplication).productRepository
        val factory = ProductViewModelFactory(repository)
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { }
        binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProduct.adapter = productAdapter

        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
        }

        productViewModel.fetchAllProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}