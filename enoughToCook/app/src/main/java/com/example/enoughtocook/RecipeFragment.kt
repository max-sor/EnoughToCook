package com.example.enoughtocook

import com.example.enoughtocook.databinding.FragmentRecipeBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch




class RecipeFragment : Fragment() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var binding: FragmentRecipeBinding
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupMealTypeSelection()
        setupGenerateButton()
        observeGeneratedRecipes()
    }

    private fun setupViewModel() {
        val productsViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)
        val recipeGenerationService = GeminiRecipeGenerationService(
            apiKey = "AIzaSyABGMDc7285foKKadwjREEk58-hICPp2jc",
            pixelsApiKey = "AgENJbVdeb7qh1yNb6uMM9BXVeulKaOd3soTiQHL1eU1UEu4F0567591"
        )
        viewModel = ViewModelProvider(this, RecipeViewModelFactory(productsViewModel, recipeGenerationService))
            .get(RecipeViewModel::class.java)
    }

    private fun observeGeneratedRecipes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.generatedRecipes.collect { recipes ->
                recipeAdapter.submitList(recipes)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.recipesRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(onItemClick = { recipe ->
            showRecipeDetails(recipe)
        })
        binding.recipesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = recipeAdapter
        }
    }
    private fun showRecipeDetails(recipe: Recipe) {
        val dialog = RecipeDetailsDialogFragment.newInstance(recipe)
        dialog.show(parentFragmentManager, "RecipeDetailsDialog")
    }


    private fun setupMealTypeSelection() {
        binding.mealTypeChipGroup.isSingleSelection = true
        binding.mealTypeChipGroup.setOnCheckedChangeListener { group: ChipGroup, checkedId: Int ->
            val mealType = when (checkedId) {
                R.id.breakfastChip -> "Breakfast"
                R.id.lunchChip -> "Lunch"
                R.id.dinnerChip -> "Dinner"
                R.id.snackChip -> "Snack"
                else -> "Lunch"
            }

            viewModel.setMealType(mealType)
        }
    }


    //Gives context for internet connection check
    private fun setupGenerateButton() {
        binding.generateRecipeButton.setOnClickListener {
            context?.let { ctx ->
                viewModel.generateRecipes(ctx)
            }
        }
    }


}