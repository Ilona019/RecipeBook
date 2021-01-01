package com.example.android.recipebook.database.entities

import androidx.room.*
import com.example.android.recipebook.database.entities.Recipe
import java.io.Serializable

@Entity(tableName = "step_table", foreignKeys = [(ForeignKey(entity = Recipe::class,
    parentColumns = ["uid"],
    childColumns = ["recipe_id"],
    onDelete = ForeignKey.CASCADE))], indices = [Index(value = ["recipe_id"])])

class Step(
    @ColumnInfo(name = "recipe_id") var recipeId: Long,
    @ColumnInfo(name = "step") var descriptionStep: String?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Long = 0
}