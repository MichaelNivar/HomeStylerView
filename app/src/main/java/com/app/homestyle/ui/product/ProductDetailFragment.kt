package com.app.homestyle.ui.product

import android.graphics.Bitmap
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
import com.app.homestyle.MyApplication
import com.app.homestyle.R
import com.app.homestyle.databinding.FragmentProductDetailBinding
import com.app.homestyle.model.Product
import com.app.homestyle.ui.arview.ArFragment
import com.app.homestyle.utils.SessionManager
import com.app.homestyle.viewmodel.UserProductViewModel
import com.app.homestyle.viewmodel.UserProductViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProductViewModel: UserProductViewModel
    private var isFavorite: Boolean = false
    private var string: String= ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        val repository = (requireActivity().application as MyApplication).userProductRepository
        val factory = UserProductViewModelFactory(repository)
        userProductViewModel = ViewModelProvider(this, factory)[UserProductViewModel::class.java]

        sessionManager = SessionManager(requireContext())

        // Obtener el producto de los argumentos
        val product: Product? = arguments?.getParcelable(ARG_PRODUCT)

        // Configurar detalles del producto
        product?.let {
            bindProductDetails(it)
            checkIfFavorite(it)
        }

        setClickListener(product)

        return binding.root
    }

    private fun bindProductDetails(product: Product) {
        binding.ivProductImage.setImageBitmap(byteArrayToBitmap(product.imagen))
        binding.tvProductName.text = product.nombreProducto
        binding.tvProductDescription.text = product.descripcion
        binding.tvProductCategory.text = product.categoriaId
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun updateFavoriteIcon() {
        val icon = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favoritenofilled
        binding.ibFavorite.setImageResource(icon)
    }

    private fun checkIfFavorite(product: Product) {
        viewLifecycleOwner.lifecycleScope.launch {
            sessionManager.loggedInUser.collect { userEmail ->
                if (userEmail != null) {
                    userProductViewModel.getUserIdByEmail(userEmail)
                    userProductViewModel.userId.observe(viewLifecycleOwner) { userId ->
                        if (userId != null) {
                            userProductViewModel.checkIfFavorite(userId, product.idProducto)
                        }
                    }
                }
            }
        }

        userProductViewModel.isFavorite.observe(viewLifecycleOwner) { favorite ->
            isFavorite = favorite
            updateFavoriteIcon()
        }
    }

    private fun setClickListener(product: Product?) {
        binding.ibFavorite.setOnClickListener {
            if (product == null) return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                sessionManager.loggedInUser.collect { userEmail ->
                    if (userEmail != null) {
                        userProductViewModel.getUserIdByEmail(userEmail)
                        userProductViewModel.userId.observe(viewLifecycleOwner) { userId ->
                            if (userId != null) {
                                if (isFavorite) {
                                    userProductViewModel.removeFavorite(userId, product.idProducto)
                                } else {
                                    userProductViewModel.addFavorite(userId, product.idProducto)
                                }
                                isFavorite = !isFavorite
                                updateFavoriteIcon()
                            }
                        }
                    } else {
                        string= getString(R.string.require_addfav)
                        Toast.makeText(
                            requireContext(),
                            string,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.ibArview.setOnClickListener {
            if (product == null) {
                string= getString(R.string.notproducts)
                Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("Model", "Ruta del modelo: ${product.archivoGlbPath}")
            val fragment = ArFragment()
            fragment.arguments = Bundle().apply {
                putString("model_path", product.archivoGlbPath)
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PRODUCT, product)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
