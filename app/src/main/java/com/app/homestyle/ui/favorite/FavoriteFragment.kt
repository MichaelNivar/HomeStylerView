package com.app.homestyle.ui.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentFavoriteBinding
import com.app.homestyle.model.Product
import com.app.homestyle.ui.product.ProductAdapter
import com.app.homestyle.ui.product.ProductDetailFragment
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.UserProductViewModel
import com.app.homestyle.viewmodel.UserProductViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var userProductViewModel: UserProductViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var favoriteAdapter: ProductAdapter

    private var string: String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        // Inicializar ViewModel
        val repository = (requireActivity().application as MyApplication).userProductRepository
        val factory = UserProductViewModelFactory(repository)
        userProductViewModel = ViewModelProvider(this, factory)[UserProductViewModel::class.java]

        // Inicializar SessionManager
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        checkUserSession()

        return binding.root
    }

    private fun setupRecyclerView() {
        favoriteAdapter = ProductAdapter(emptyList()) { product ->
            navigateToProductDetails(product)
        }
        binding.rvProductfav.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductfav.adapter = favoriteAdapter
    }

    private fun checkUserSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            val loggedInUser = sessionManager.loggedInUser.firstOrNull()
            if (loggedInUser != null) {
                // Recupera el ID del usuario
                userProductViewModel.getUserIdByEmail(loggedInUser)
                observeFavorites()
            } else {
                string = getString(R.string.require_logfav)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeFavorites() {
        // Observa cambios en el ID del usuario
        userProductViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                // Cargar productos favoritos
                userProductViewModel.getFavoriteProducts(userId)
            } else {
                string = getString(R.string.user_notfound)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            }
        }

        // Observa cambios en la lista de productos favoritos
        userProductViewModel.favoriteProducts.observe(viewLifecycleOwner) { favoriteProducts ->
            if (favoriteProducts.isNotEmpty()) {
                favoriteAdapter.updateProducts(favoriteProducts)
            } else {
                string= getString(R.string.notproductsfav)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToProductDetails(product: Product) {
        val fragment = ProductDetailFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


