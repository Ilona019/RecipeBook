package com.example.android.recipebook.database.daos

import androidx.room.*
import com.example.android.recipebook.database.entities.Step

@Dao
interface StepDao {
    @Query("SELECT * FROM step_table")
    fun getSteps():MutableList<Step?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStep(step: Step)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSteps(steps: List<Step>)

    @Delete
    fun deleteStep(step: Step?)

    @Update
    fun updateSteps(steps: List<Step>)

    @Update
    fun countUpdateSteps(steps: List<Step>): Int

    @Query("SELECT * FROM  step_table where recipe_id = :recipe_id" )
    fun getStepsFromRecipe(recipe_id: Long): List<Step?>?
}