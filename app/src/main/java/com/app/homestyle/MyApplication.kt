package com.app.homestyle

import android.app.Application
import androidx.room.Room
import com.app.homestyle.model.UserAr
import com.app.homestyle.repository.CaptureRepository
import com.app.homestyle.repository.CategoryRepository
import com.app.homestyle.repository.ProductRepository
import com.app.homestyle.repository.UserProductRepository
import com.app.homestyle.repository.UserRepository
import com.app.homestyle.utils.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MyApplication : Application() {

    private val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    val userRepository by lazy {
        UserRepository(database.userDao())
    }
    val categoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }
    val productRepository by lazy {
        ProductRepository(database.productDao())
    }
    val userProductRepository by lazy {
        UserProductRepository(database.userProductDao())
    }
    val captureRepository by lazy {
        CaptureRepository(database.captureDao())
    }
    override fun onCreate() {
        super.onCreate()
        val userRepository = UserRepository(AppDatabase.getDatabase(this).userDao())
        val adminUser = UserAr(
            idUsuario = "admin",
            nombre = "admin",
            email = "admin",
            password = "admin",
            imagenPerfil = null,
            fechaCreacion = Date()
        )

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.insert(adminUser)
        }
    }
}