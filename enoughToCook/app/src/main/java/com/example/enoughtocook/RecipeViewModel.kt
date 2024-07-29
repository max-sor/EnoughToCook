package com.example.enoughtocook

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class RecipeViewModel(
    private val productsViewModel: ProductsViewModel,
    private val recipeGenerationService: RecipeGenerationService
) : ViewModel() {
    private val _generatedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val generatedRecipes: StateFlow<List<Recipe>> = _generatedRecipes.asStateFlow()

    private val _selectedMealType = MutableStateFlow("Lunch")
    val selectedMealType: StateFlow<String> = _selectedMealType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setMealType(mealType: String) {
        _selectedMealType.value = mealType
    }

    fun generateRecipes(context: Context) = viewModelScope.launch {
        if (!isOnline(context)) {
            _error.value = "No internet connection. Please check your network and try again."
            return@launch
        }

        _isLoading.value = true
        _error.value = null
        _generatedRecipes.value = emptyList() // Clear the previous list
        try {
            val products = productsViewModel.getCurrentProducts()
            val mealType = selectedMealType.value
            _generatedRecipes.value = recipeGenerationService.generateRecipes(products, mealType)
        } catch (e: Exception) {
            _error.value = "Failed to generate recipes: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}