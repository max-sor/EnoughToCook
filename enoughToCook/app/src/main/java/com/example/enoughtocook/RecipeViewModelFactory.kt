package com.example.enoughtocook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecipeViewModelFactory(
    private val productsViewModel: ProductsViewModel,
    private val recipeGenerationService: RecipeGenerationService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(productsViewModel, recipeGenerationService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}