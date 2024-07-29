package com.example.enoughtocook

import android.net.Uri
import android.util.Log
import kotlin.random.Random
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.net.URI
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException


interface RecipeGenerationService {
    suspend fun generateRecipes(products: List<Product>, mealType: String): List<Recipe>
}

class GeminiRecipeGenerationService(private val apiKey: String, private val pixelsApiKey: String) : RecipeGenerationService {
    private val model: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 1
                topP = 1f
                maxOutputTokens = 1024
            }
        )
    }
    private val pixelsImageService = PixelsImageService(pixelsApiKey)

    override suspend fun generateRecipes(products: List<Product>, mealType: String): List<Recipe> = withContext(Dispatchers.IO) {
        val recipes = mutableListOf<Recipe>()
        val madeRecipes = mutableListOf<Recipe>()
        repeat(4) { index ->
            val recipe = generateSingleRecipe(products, mealType, index, madeRecipes)
            recipes.add(recipe)
            madeRecipes.add(recipe)
        }
        recipes
    }

    private suspend fun generateSingleRecipe(products: List<Product>, mealType: String, index: Int, madeRecipes: MutableList<Recipe>): Recipe {
        val prompt = """
        Here is the list of ingredients I have: ${products.joinToString(", ") { it.name }}.
        I need you to choose one recipe for $mealType that you can cook at home using some of these ingredients 
        (things such as salt, sugar, oil, pepper, water, flour and other basics that every person has, 
        you can include even if they are not in the list).
        
        If the list in other language, for example in russian, than provide the recipe in russian or in the language of the list provided.
        
        Don't be afraid to experiment with the ingredients you have.
        And don't be afraid to suggest some basic dishes as well.
        Try to find balance to sometimes find interesting and sometimes find simple dishes.
        
        Sometimes you can something that is not in a user list of ingredients.
        
        AND I NEED YOU TO PROVIDE REAL DISHES. To be sure that you provide real dishes, check
        on website https://www.themealdb.com/
        
        Don't include food that is not eatable
        
        And there is a list of dishes that you already made, try no to copy them: ${madeRecipes.joinToString(", ") { it.name }}.
        (This list may be empty if you haven't made before)
        
        Provide the recipe information in the following JSON format:
        {
          "name": "Recipe Name",
          "ingredients": [
            "Ingredient 1 without quantity",
            "Ingredient 2 without quantity",
            "..."
          ],
          "ingredientsQuantity": [
            "Ingredient 1 with quantity",
            "Ingredient 2 with quantity",
            "..."
          ],
          "instructions": [
            "1. Step 1",
            "2. Step 2",
            "..."
          ],
          "cooking_time": 30
        }
        
        Ensure that:
        1. The "name" is a string with the recipe name.
        2. The "ingredients" is an array of strings, each with the ingredient and it without quantity.
        3. The "ingredientsQuantity" is an array of strings, each with the ingredient and it with quantity.
        4. The "instructions" is an array of strings, each representing a step in the cooking process. 
        Before the step itself add number starting with 1.
        5. The "cooking_time" is a number representing the total cooking time in minutes.
        
        Double check if recipe is real or better just give any real recipe, not some random text
        Provide only the JSON object, with no additional text before or after. Ensure it's valid JSON that can be parsed.
    """.trimIndent()

        val response = model.generateContent(prompt).text ?: "{}"
        val cleanedResponse = cleanJsonString(response)
        return try {
            parseRecipeJson(cleanedResponse, index)
        } catch (e: JSONException) {
            Log.e("RecipeGeneration", "Failed to parse JSON: $cleanedResponse", e)
            Recipe(
                id = index.toString(),
                name = "Failed to generate recipe",
                ingredients = listOf("Error occurred"),
                ingredientsQuantity = listOf("Error occurred"),
                instructions = listOf("Please try again"),
                cookingTime = 0,
                imageUrl = "https://via.placeholder.com/400x300.png?text=Recipe+Image",
                isSaved = false
            )
        }
    }

    private fun parseRecipeJson(jsonString: String, index: Int): Recipe {
        Log.d("RecipeGeneration", "Received JSON: $jsonString")
        val json = JSONObject(jsonString)
        val recipeName = json.optString("name", "Unnamed Recipe")
        return Recipe(
            id = index.toString(),
            name = recipeName,
            ingredients = json.optJSONArray("ingredients")?.toStringList() ?: emptyList(),
            ingredientsQuantity = json.optJSONArray("ingredientsQuantity")?.toStringList() ?: emptyList(),
            instructions = json.optJSONArray("instructions")?.toStringList() ?: emptyList(),
            cookingTime = json.optInt("cooking_time", 0),
            imageUrl = generateImageUrl(recipeName),
            isSaved = false
        )
    }

    private fun JSONArray.toStringList(): List<String> {
        return (0 until length()).mapNotNull { optString(it) }
    }

    private fun generateImageUrl(recipeName: String): String {
        val imageUrl = pixelsImageService.searchImage("$recipeName food")
        return imageUrl ?: "https://via.placeholder.com/400x300.png?text=Recipe+Image"
    }

    private fun isValidImageUrl(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.scheme == "http" || uri.scheme == "https"
        } catch (e: Exception) {
            false
        }
    }
    private fun cleanJsonString(input: String): String {
        // Remove any leading or trailing non-JSON content
        val jsonStart = input.indexOf("{")
        val jsonEnd = input.lastIndexOf("}") + 1
        return if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            input.substring(jsonStart, jsonEnd)
        } else {
            input
        }
    }
}




class PixelsImageService(private val apiKey: String) {
    private val client = OkHttpClient()

    fun searchImage(query: String): String? {
        val request = Request.Builder()
            .url("https://api.pexels.com/v1/search?query=$query&per_page=1")
            .addHeader("Authorization", apiKey)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val jsonData = response.body?.string()
            val jsonObject = JSONObject(jsonData)
            val photos = jsonObject.getJSONArray("photos")
            if (photos.length() > 0) {
                val photo = photos.getJSONObject(0)
                photo.getJSONObject("src").getString("medium")
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}