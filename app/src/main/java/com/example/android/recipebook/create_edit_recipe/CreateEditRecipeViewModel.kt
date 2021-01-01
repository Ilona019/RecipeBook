package com.example.android.recipebook.create_edit_recipe

import android.app.Application
import android.os.AsyncTask
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.recipebook.R
import com.example.android.recipebook.database.RecipeDatabase
import com.example.android.recipebook.database.entities.Ingredient
import com.example.android.recipebook.database.entities.Recipe
import com.example.android.recipebook.database.entities.RecipeIngredient
import com.example.android.recipebook.database.entities.Step
import java.lang.ref.WeakReference

class CreateEditRecipeViewModel(
    private var recipeDatabase: RecipeDatabase?,
    var recipe: Recipe?,
    application: Application
) : AndroidViewModel(application) {

    private var _flag = MutableLiveData<Int>()
    val flag: LiveData<Int>
        get() = _flag

    fun getNamesIngredients():ArrayList<String>? {
        val ingredients = arrayListOf<String>()
        for(ingredient in recipeDatabase!!.getRecipeIngredientDao()!!.getIngredientsFromRecipe(recipe!!.uid)!!){
            ingredients.add(ingredient!!.nameIngredient!!)
        }
        return ingredients
    }

    fun getNamesSteps():ArrayList<String>? {
        val steps = arrayListOf<String>()
        for(step in recipeDatabase!!.getStepDao()!!.getStepsFromRecipe(recipe!!.uid)!!){
            steps.add(step!!.descriptionStep!!)
        }
        return steps
    }

    fun getNameRecipe(): String?{
        return recipe!!.name
    }

    fun createRecipe(nameRecipe: EditText,  containerIngredients: LinearLayout?, containerCookingSteps: LinearLayout?) {
        recipe = Recipe(
            nameRecipe.text.toString()
        )
        InsertTask(
            this,
            recipe!!,
            containerCookingSteps!!,
            containerIngredients!!
        ).execute()

    }


    private class InsertTask internal constructor(context: CreateEditRecipeViewModel, recipe: Recipe, containerCookingSteps: LinearLayout, containerIngredients: LinearLayout) :
        AsyncTask<Void?, Void?, Boolean>() {
        private val activityReference: WeakReference<CreateEditRecipeViewModel> = WeakReference(context)
        private val recipe: Recipe = recipe
        private var steps: List<Step>? = null
        private val containerCookingSteps: LinearLayout = containerCookingSteps
        private var containerIngredients: LinearLayout = containerIngredients

        override fun doInBackground(vararg params: Void?): Boolean? {
            // retrieve auto incremented recipe id

            val newId =  activityReference.get()!!.recipeDatabase!!.getRecipeDao()!!.insertRecipe(recipe)
            recipe.uid = newId
            makeListIngredients(newId)
            makeListSteps(newId)
            activityReference.get()!!.recipeDatabase!!.getStepDao()!!.insertSteps(steps!!)
            return true
        }

        private fun makeListIngredients(newIdRecipe: Long) {
            val childCount = containerIngredients.childCount
            var newIngredient: Ingredient?
            var formatNameIngredient: String
            var newIdIngredient: Long
            var existIngredient: Ingredient?

            for (i in 0 until childCount) {
                val thisChild = containerIngredients.getChildAt(i)
                val childTextView =
                    thisChild.findViewById<View>(R.id.text_out) as EditText

                formatNameIngredient = childTextView.text.toString().toLowerCase().trim()

                existIngredient = activityReference.get()!!.recipeDatabase!!.getIngredientDao()?.getIngredientByName(formatNameIngredient)
                if(existIngredient == null) {
                    newIngredient = Ingredient(formatNameIngredient)
                    newIdIngredient =
                        activityReference.get()!!.recipeDatabase!!.getIngredientDao()!!
                            .insertIngredient(newIngredient)
                } else {
                    newIdIngredient = existIngredient.uid
                }

                activityReference.get()!!.recipeDatabase!!.getRecipeIngredientDao()!!.insertRecipeIngredient(
                    RecipeIngredient(newIdRecipe, newIdIngredient)
                )
            }
        }

        // onPostExecute runs on main thread
        override fun onPostExecute(bool: Boolean) {
            if (bool) {
                activityReference.get()!!.setResult(recipe, 1)
            }
        }

        private fun makeListSteps(newId: Long) {
            val childCount = containerCookingSteps.childCount
            steps = ArrayList()
            for (i in 0 until childCount) {
                val thisChild = containerCookingSteps.getChildAt(i)
                val childTextView =
                    thisChild.findViewById<View>(R.id.text_out) as EditText
                (steps as ArrayList<Step>).add(
                    Step(
                        newId,
                        childTextView.text.toString()
                    )
                )
            }
        }

    }

    private fun setResult(newRecipe: Recipe, i: Int) {
        _flag.value = i
        recipe = newRecipe
    }

    private fun updateListIngredients(containerIngredients: LinearLayout?) {
        val containerCount = containerIngredients!!.childCount
        var ingredients: List<Ingredient?> =  recipeDatabase!!.getRecipeIngredientDao()?.getIngredientsFromRecipe(recipe!!.uid) as List<Ingredient>
        var thisChild: View?
        var childTextView: EditText?
        var newIngredient: Ingredient?
        var newIdIngredient: Long
        var name: String
        val enteredIngredients: ArrayList<String>? = ArrayList()

        //Формирую список строк из новых ингредиентов.
        for(i in 0 until containerCount) {
            thisChild = containerIngredients.getChildAt(i)
            childTextView = thisChild.findViewById<View>(R.id.text_out) as EditText
            enteredIngredients!!.add(childTextView.text.toString().toLowerCase().trim())
        }

        //Удаляю из RecipeIngredient ингредиенты, которых нет в списке новых ингредиентов.
        for(currentIngredient in ingredients) {
            name = currentIngredient!!.nameIngredient!!

            if(enteredIngredients!!.contains(name)) {
                enteredIngredients.remove(name)
            }
            else {
                recipeDatabase!!.getRecipeIngredientDao()?.deleteRecipeIngredient(RecipeIngredient(recipe!!.uid, currentIngredient.uid))
                //Если в бд RecipeIngredient нет рецептов, где используется ингредиент, удалить из бд Ingredient.
                if(recipeDatabase!!.getRecipeIngredientDao()!!.getCountStrokeIngredientById(currentIngredient.uid) == 0){
                    recipeDatabase!!.getIngredientDao()!!.deleteIngredient(currentIngredient)
                }
            }
        }

        //Добавляю новые ингредиенты в таблицы Ingredient и RecipeIngredient
        if(enteredIngredients!!.isNotEmpty()) {
            for(j in 0 until enteredIngredients.size) {
                newIngredient = recipeDatabase!!.getIngredientDao()?.getIngredientByName(enteredIngredients[j])

                if(newIngredient!=null) {
                    recipeDatabase!!.getRecipeIngredientDao()
                        ?.insertRecipeIngredient(RecipeIngredient((recipe!!.uid), newIngredient.uid))
                }
                else {
                    newIngredient = Ingredient(enteredIngredients[j])
                    newIdIngredient =
                        recipeDatabase!!.getIngredientDao()!!.insertIngredient(newIngredient)

                    recipeDatabase!!.getRecipeIngredientDao()
                        ?.insertRecipeIngredient(RecipeIngredient((recipe!!.uid), newIdIngredient))
                }
            }
        }
    }

    private fun updateListSteps(newId: Long, containerCookingSteps: LinearLayout?): List<Step?> {
        val childCount = containerCookingSteps!!.childCount
        var steps: List<Step?> = recipeDatabase!!.getStepDao()!!.getStepsFromRecipe(recipe!!.uid)!!
        val countStepsDatabase = steps.size
        var thisChild: View?
        var childTextView: EditText?
        var newStep: Step?

        if(childCount <= countStepsDatabase) {
            steps = updateDescriptionList(steps, childCount, containerCookingSteps)
            deleteEndList(childCount, steps)
        }

        if(childCount > countStepsDatabase)
            steps = updateDescriptionList(steps, countStepsDatabase, containerCookingSteps)

        for (j in  0 until childCount-countStepsDatabase) {

            thisChild = containerCookingSteps.getChildAt(countStepsDatabase + j)
            childTextView = thisChild.findViewById<View>(R.id.text_out) as EditText
            newStep =
                Step(
                    newId,
                    childTextView.text.toString()
                )
            recipeDatabase!!.getStepDao()!!.insertStep(newStep)//вставить новый шаг в таблицу
            (steps as ArrayList<Step?>).add(newStep)
        }

        return steps
    }

    private fun updateDescriptionList(steps: List<Step?>, indexEnd: Int, containerCookingSteps: LinearLayout?):List<Step?> {
        var thisChild: View?
        var childTextView: EditText?

        for(i in 0 until indexEnd){
            thisChild = containerCookingSteps!!.getChildAt(i)
            childTextView =
                thisChild.findViewById<View>(R.id.text_out) as EditText
            steps[i]!!.descriptionStep =  childTextView.text.toString()
        }
        return steps
    }

    private fun deleteEndList(indexStart: Int, list: List<Step?>) {
        for (i in indexStart until list.size) {
            recipeDatabase!!.getStepDao()!!.deleteStep(list[i])
        }
    }

    fun updateRecipe(nameRecipe: String, containerIngredients: LinearLayout?, containerCookingSteps: LinearLayout?) {
        recipe!!.name = nameRecipe
        recipeDatabase!!.getRecipeDao()?.updateRecipe(recipe)

        updateListIngredients(containerIngredients)
        recipeDatabase!!.getStepDao()?.updateSteps(updateListSteps(recipe!!.uid, containerCookingSteps) as List<Step>)
    }

}