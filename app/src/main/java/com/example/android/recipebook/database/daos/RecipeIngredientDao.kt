package com.example.android.recipebook.database.daos

import androidx.room.*
import com.example.android.recipebook.database.entities.Ingredient
import com.example.android.recipebook.database.entities.Recipe
import com.example.android.recipebook.database.entities.RecipeIngredient

@Dao
interface RecipeIngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Delete
    fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient?)

    @Query("SELECT * FROM  ingredient_table INNER JOIN  recipe_ingredient_table ON  ingredient_table.uid = recipe_ingredient_table.ingredient_id where recipe_id = :recipeId" )
    fun getIngredientsFromRecipe(recipeId: Long): MutableList<Ingredient?>?

    @Query("SELECT * FROM  recipe_book_table INNER JOIN  recipe_ingredient_table ON  recipe_book_table.uid = recipe_ingredient_table.recipe_id where ingredient_id = :ingredientId" )
    fun getRecipesByIngredient(ingredientId: Long):MutableList<Recipe?>?

    @Query("SELECT recipe_book_table.uid, recipe_book_table.name FROM recipe_book_table INNER JOIN recipe_ingredient_table ON recipe_ingredient_table.recipe_id  = recipe_book_table.uid INNER JOIN ingredient_table ON  ingredient_table.uid = recipe_ingredient_table.ingredient_id WHERE ingredient_table.name_ingredient LIKE :subStr GROUP BY recipe_id" )
    fun getRecipesBySubStringIngredient(subStr: String):MutableList<Recipe?>?

    @Query("SELECT COUNT(*) FROM  recipe_ingredient_table WHERE ingredient_id = :id_ingredient")
    fun getCountStrokeIngredientById(id_ingredient: Long):Int?

}