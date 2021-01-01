package com.example.android.recipebook.database.daos

import androidx.room.*
import com.example.android.recipebook.database.entities.Recipe
import com.example.android.recipebook.database.entities.RecipeIngredient

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_book_table")
    fun getRecipes(): MutableList<Recipe?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipes: Recipe): Long

    @Insert
    fun insert(recipes: Iterable<Recipe>?)

    //id только что добавленной записи
    @Insert
    fun insert(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setLinksRecipeIngredients(steps: ArrayList<RecipeIngredient>?)

    @Update
    fun updateRecipe(recipe: Recipe?)

    //получить количество обновленных записей
    @Update
    fun update(recipe: ArrayList<Recipe?>?): Int

    @Delete
    fun deleteRecipe(recipe: Recipe?)

    //получить количество удалённых записей
    @Delete
    fun delete(recipes: ArrayList<Recipe?>?): Int

    @Query("SELECT name FROM recipe_book_table")
    fun getName(): List<String?>?


    @Query("SELECT * FROM recipe_book_table WHERE name = :name")
    fun getRecipe(name: String): Recipe?

}