package com.app.homestyle.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.homestyle.R
import com.app.homestyle.model.Product

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductSearchResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductSearchResultsFragment : Fragment() {
    private lateinit var productAdapter: ProductAdapter
    private var products: List<Product> = emptyList()

    companion object {
        private const val ARG_PRODUCTS = "products"

        fun newInstance(products: List<Product>): ProductSearchResultsFragment {
            val fragment = ProductSearchResultsFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_PRODUCTS, ArrayList(products))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_search_results, container, false)
        products = arguments?.getParcelableArrayList(ARG_PRODUCTS) ?: emptyList()
        setupRecyclerView(view)
        return view
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProductSearchResults)
        productAdapter = ProductAdapter(products) { product ->
            navigateToProductDetails(product) // Maneja el clic en un producto
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = productAdapter
    }

    private fun navigateToProductDetails(product: Product) {
        val fragment = ProductDetailFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}