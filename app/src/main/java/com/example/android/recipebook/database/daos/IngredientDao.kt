package com.example.android.recipebook.database.daos

import androidx.room.*
import com.example.android.recipebook.database.entities.Ingredient

@Dao
interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredient(ingredient: Ingredient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredients(ingredients: List<Ingredient>)

    @Delete
    fun deleteIngredient(ingredient: Ingredient?)

    @Update
    fun updateIngredients(ingredients: List<Ingredient>)

    @Query("SELECT * FROM ingredient_table WHERE name_ingredient = :name")
    fun getIngredientByName(name: String): Ingredient?

}