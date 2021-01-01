package com.example.android.recipebook.database.entities


import androidx.room.*
import com.example.android.recipebook.database.entities.Recipe
import java.io.Serializable

@Entity(tableName = "ingredient_table", indices = [Index("uid")])

class Ingredient(@ColumnInfo(name = "name_ingredient") var nameIngredient: String?) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Long = 0
}