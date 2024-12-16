package com.app.homestyle.ui.product

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.homestyle.R
import com.app.homestyle.model.Category
import com.app.homestyle.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit // Callback para manejar clics
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder para item_category
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        val productName: TextView = itemView.findViewById(R.id.tv_item_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Configurar nombre de la categoría
        holder.productName.text = product.nombreProducto
        // Convertir el ByteArray a Bitmap para el ImageView
        val bitmap = byteArrayToBitmap(product.imagen)
        if (bitmap != null) {
            holder.productImage.setImageBitmap(bitmap)
        } else {
            holder.productImage.setImageResource(R.drawable.ic_image_selected) // Imagen por defecto
        }

        // Configurar clic en el elemento
        holder.itemView.setOnClickListener {
            onProductClick(product) // Llamar al callback
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // Método para convertir ByteArray a Bitmap
    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            null
        }
    }

    // Método para actualizar los datos del adaptador
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}