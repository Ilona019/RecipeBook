package com.example.android.recipebook.create_edit_recipe

    import android.app.Application
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.example.android.recipebook.database.RecipeDatabase
    import com.example.android.recipebook.database.entities.Recipe

    class CreateEditViewModelFactory(private var recipeDatabase: RecipeDatabase?,
                                     private val recipe: Recipe?,
                                     private val application: Application
    ) : ViewModelProvider.Factory  {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateEditRecipeViewModel::class.java)) {
                return CreateEditRecipeViewModel( recipeDatabase, recipe, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
