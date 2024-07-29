package com.example.enoughtocook // Replace with your actual package name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.enoughtocook.databinding.DialogRecipeDetailsBinding // Make sure this import matches your package structure

class RecipeDetailsDialogFragment : DialogFragment() {
    private lateinit var binding: DialogRecipeDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogRecipeDetailsBinding.inflate(inflater, container, false)
        getDialog()?.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent))
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<Recipe>("recipe")
        recipe?.let { displayRecipeDetails(it) }
    }

    private fun displayRecipeDetails(recipe: Recipe) {
        binding.recipeName.text = recipe.name
        binding.cookingTime.text = "${recipe.cookingTime} min"
        binding.ingredientsList.text = recipe.ingredientsQuantity.joinToString("\n")
        binding.instructionsList.text = recipe.instructions.joinToString("\n\n")

        Glide.with(requireContext())
            .load(recipe.imageUrl)
            .into(binding.recipeImage)
    }

    companion object {
        fun newInstance(recipe: Recipe): RecipeDetailsDialogFragment {
            val fragment = RecipeDetailsDialogFragment()
            val args = Bundle()
            args.putParcelable("recipe", recipe)
            fragment.arguments = args
            return fragment
        }
    }
}