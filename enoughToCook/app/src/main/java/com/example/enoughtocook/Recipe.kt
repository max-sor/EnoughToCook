package com.example.enoughtocook

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val ingredientsQuantity: List<String>,
    val instructions: List<String>,
    val cookingTime: Int,
    val imageUrl: String,
    val isSaved: Boolean = false
) : Parcelable