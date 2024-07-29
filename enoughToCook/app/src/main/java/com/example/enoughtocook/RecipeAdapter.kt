package com.example.enoughtocook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.enoughtocook.databinding.ItemRecipeBinding
import com.bumptech.glide.Glide

class RecipeAdapter(private var recipes: List<Recipe> = listOf(),
                    private val onItemClick: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipeName.text = recipe.name
            binding.ingredientsList.text = recipe.ingredients.joinToString(", ")
            binding.cookingTime.text = "${recipe.cookingTime} min"
            Glide.with(binding.root.context)
                .load(recipe.imageUrl)
                .into(binding.recipeImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    fun submitList(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}