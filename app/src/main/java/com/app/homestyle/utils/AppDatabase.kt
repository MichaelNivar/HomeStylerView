package com.app.homestyle.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.homestyle.dao.CaptureDao
import com.app.homestyle.dao.CategoryDao
import com.app.homestyle.dao.ProductDao
import com.app.homestyle.dao.UserDao
import com.app.homestyle.dao.UserProductDao
import com.app.homestyle.model.Capture
import com.app.homestyle.model.Category
import com.app.homestyle.model.Product
import com.app.homestyle.model.UserAr
import com.app.homestyle.model.UserProductCrossRef

@Database(
    entities = [UserAr::class, Category::class, Product::class, UserProductCrossRef::class, Capture::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun userProductDao(): UserProductDao // Nuevo DAO para la relación
    abstract fun captureDao(): CaptureDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Elimina datos si cambia el esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}