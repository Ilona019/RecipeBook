package com.example.android.recipebook.database.entities

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "recipe_book_table", indices = [Index("uid")])
class Recipe(@ColumnInfo(name = "name") var name: String?) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Long = 0
}
