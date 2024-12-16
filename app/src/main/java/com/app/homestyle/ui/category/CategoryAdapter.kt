package com.app.homestyle.ui.category

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.homestyle.R
import com.app.homestyle.model.Category

class CategoryAdapter(private var categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // ViewHolder para item_category
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        val categoryName: TextView = itemView.findViewById(R.id.tv_item_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        // Configurar nombre de la categoría
        holder.categoryName.text = category.nombreCategoria

        // Convertir el ByteArray a Bitmap para el ImageView
        val bitmap = byteArrayToBitmap(category.imagen)
        if (bitmap != null) {
            holder.categoryImage.setImageBitmap(bitmap)
        } else {
            holder.categoryImage.setImageResource(R.drawable.ic_image_selected) // Imagen por defecto
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    // Método para convertir ByteArray a Bitmap
    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            null
        }
    }
}