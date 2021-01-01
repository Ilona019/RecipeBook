package com.example.android.recipebook.helper_mode

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.recipebook.database.daos.RecipeIngredientDao
import com.example.android.recipebook.database.daos.StepDao
import com.example.android.recipebook.database.entities.Recipe

class HelperModeViewModelFactory(private val daoRecipeIngredientDao: RecipeIngredientDao,
                                 private val daoStep: StepDao,
                                 private val recipe: Recipe?,
                                 private val application: Application
) : ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HelperModeViewModel::class.java)) {
            return HelperModeViewModel(daoRecipeIngredientDao, daoStep, recipe!!, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}