package com.app.homestylerview.repository

import android.net.Uri
import com.app.homestylerview.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val productCollection = firestore.collection("products")

    suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = productCollection.get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Subir archivo .glb y obtener la URL
    suspend fun uploadGlbFile(productId: String, glbUri: Uri): String? {
        return try {
            val storageRef = storage.reference.child("models/$productId.glb")
            val uploadTask = storageRef.putFile(glbUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    // Subir imagen y obtener la URL
    suspend fun uploadImage(productId: String, imageUri: Uri): String? {
        return try {
            val storageRef = storage.reference.child("images/$productId.jpg")
            val uploadTask = storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addProduct(product: Product): Boolean {
        return try {
            productCollection.document(product.idProducto).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateProduct(product: Product): Boolean {
        return try {
            productCollection.document(product.idProducto).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            productCollection.document(productId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}