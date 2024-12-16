package com.app.homestyle.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.homestyle.R
import com.app.homestyle.model.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductBottomSheetFragment(
    private val products: List<Product>,
    private val onProductSelected: ((Product) -> Unit)? = null
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_products)
        val emptyView = view.findViewById<View>(R.id.empty_view) // Un TextView o Layout para mostrar cuando no haya productos

        if (products.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = ProductAdapter(products) { selectedProduct ->
                onProductSelected?.invoke(selectedProduct) // Llamar al callback si existe
                dismiss() // Cierra el BottomSheetDialogFragment
            }
        }
    }
}
