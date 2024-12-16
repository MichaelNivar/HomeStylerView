package com.app.homestyle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.homestyle.model.Category
import com.app.homestyle.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    fun insertCategory(category: Category) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun getAllCategories(onResult: (List<Category>) -> Unit) {
        viewModelScope.launch {
            val categories = repository.getAllCategories()
            onResult(categories)
        }
    }

    fun getCategoryById(id: String, onResult: (Category?) -> Unit) {
        viewModelScope.launch {
            val category = repository.getCategoryById(id)
            onResult(category)
        }
    }
}

class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}