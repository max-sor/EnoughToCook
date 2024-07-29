package com.example.enoughtocook

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class ProductsViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()
    private val gson = Gson()

    init {
        migrateOldData()
        loadListFromPreferencesAsync()
    }

    private fun migrateOldData() {
        val oldJson = sharedPreferences.getString("productList", null)
        if (oldJson != null) {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                val oldList: List<String> = gson.fromJson(oldJson, type)
                val newList = oldList.map { Product(it, 1.0, "units") }
                _productList.value = newList
                saveListToPreferences()
            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Error migrating old data: ${e.message}")
            }
        }
    }

    private fun loadListFromPreferencesAsync() = viewModelScope.launch {
        _productList.value = loadListFromPreferences()
    }

    fun addProduct(product: Product) {
        val currentList = _productList.value.toMutableList()
        if (!currentList.any { it.name == product.name }) {
            currentList.add(product)
            _productList.value = currentList
            saveListToPreferences()
        }
    }

    fun removeProduct(product: Product) {
        val currentList = _productList.value.toMutableList()
        if (currentList.remove(product)) {
            _productList.value = currentList
            saveListToPreferences()
        }
    }

    fun updateProductList(newList: List<Product>) {
        _productList.value = newList
        saveListToPreferences()
    }

    private suspend fun loadListFromPreferences(): List<Product> {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString("productList", null)
            if (json != null) {
                try {
                    val type = object : TypeToken<List<Product>>() {}.type
                    gson.fromJson(json, type) ?: emptyList()
                } catch (e: Exception) {
                    Log.e("ProductsViewModel", "Error loading list: ${e.message}")
                    migrateOldData() // Try to migrate old data if parsing fails
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    fun loadList() = viewModelScope.launch {
        _productList.value = loadListFromPreferences()
    }

    private fun isValidJson(jsonStr: String): Boolean {
        return try {
            gson.fromJson(jsonStr, Any::class.java)
            true
        } catch (e: JsonSyntaxException) {
            false
        }
    }

    private fun saveListToPreferences() = viewModelScope.launch(Dispatchers.IO) {
        val json = gson.toJson(_productList.value)
        if (isValidJson(json)) {
            try {
                sharedPreferences.edit().putString("productList", json).apply()
            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Failed to save list to preferences: ${e.message}")
            }
        }
    }
    fun getCurrentProducts(): List<Product> = productList.value
}