package com.example.android.recipebook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.recipebook.database.daos.IngredientDao
import com.example.android.recipebook.database.daos.RecipeDao
import com.example.android.recipebook.database.daos.RecipeIngredientDao
import com.example.android.recipebook.database.daos.StepDao
import com.example.android.recipebook.database.entities.*

@Database(entities =  [(Recipe::class), (Step::class), (RecipeIngredient::class), (Ingredient::class)], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun getRecipeDao(): RecipeDao?
    abstract fun getStepDao(): StepDao?
    abstract fun getIngredientDao(): IngredientDao?
    abstract fun getRecipeIngredientDao(): RecipeIngredientDao?

    fun cleanUp() {
        recipeDB = null
    }

    companion object {
        private var recipeDB: RecipeDatabase? = null

        fun  getInstance(context: Context): RecipeDatabase? {
            if (null == recipeDB) {
                recipeDB =
                    buildDatabaseInstance(context)
            }
            return recipeDB
        }

        private fun buildDatabaseInstance(context: Context): RecipeDatabase {
            return Room.databaseBuilder(
                context,
                RecipeDatabase::class.java,
                "RecipeBook"
            ).allowMainThreadQueries().build()
        }
    }
}