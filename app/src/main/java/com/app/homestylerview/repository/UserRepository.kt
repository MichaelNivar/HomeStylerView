package com.app.homestylerview.repository

import com.app.homestylerview.model.UserAr
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID


class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val auth = FirebaseAuth.getInstance()

    // Método para registrar un usuario
    suspend fun registerUserWithAuthentication(usuario: UserAr): Result<Boolean> {
        return try {
            // Crea el usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(usuario.email, usuario.password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            // Guarda los datos adicionales del usuario en Firestore
            val user = UserAr(
                idUsuario = userId,
                nombre = usuario.nombre,
                email = usuario.email,
                password = usuario.password,
                fechaCreacion = java.util.Date()
            )
            usersCollection.document(userId).set(user).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Método para obtener un usuario por ID
    suspend fun getUserById(userId: String): Result<UserAr?> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            if (snapshot.exists()) {
                val user = snapshot.toObject(UserAr::class.java)
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}