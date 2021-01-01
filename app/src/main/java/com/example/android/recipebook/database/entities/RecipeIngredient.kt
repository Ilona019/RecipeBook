package com.example.android.recipebook.database.entities

import androidx.room.*
import com.example.android.recipebook.database.entities.Recipe
import com.example.android.recipebook.database.entities.Step

@Entity(tableName = "recipe_ingredient_table", primaryKeys = ["recipe_id", "ingredient_id"], foreignKeys = [ForeignKey(entity = Recipe::class,
    parentColumns = ["uid"],
    childColumns = ["recipe_id"],
    onDelete = ForeignKey.CASCADE), ForeignKey(entity = Ingredient::class,
    parentColumns = ["uid"],
    childColumns = ["ingredient_id"],
    onDelete = ForeignKey.CASCADE)], indices = [Index(value = ["recipe_id", "ingredient_id"])])

class RecipeIngredient(
    @ColumnInfo(name = "recipe_id") var recipeId: Long,
    @ColumnInfo(name = "ingredient_id") var ingredientId: Long
)