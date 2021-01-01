package com.example.android.recipebook.helper_mode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.recipebook.database.daos.RecipeIngredientDao
import com.example.android.recipebook.database.daos.StepDao
import com.example.android.recipebook.database.entities.Recipe


class HelperModeViewModel (
    private val daoRecipeIngredient: RecipeIngredientDao,
    private val daoStep: StepDao,
    val recipe: Recipe,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var stepsList: MutableList<String>

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    private val _stepCounter = MutableLiveData<Int>()
    private val stepCounter: LiveData<Int>
        get() = _stepCounter

    private val _stepFinish = MutableLiveData<Boolean>()
    val stepFinish: LiveData<Boolean>
        get() = _stepFinish

    private val _lastStep = MutableLiveData<Boolean>()
    val lastStep: LiveData<Boolean>
        get() = _lastStep


    init {
        _title.value = "Ингредиенты для блюда ${recipe.name}:"
        _stepCounter.value = 0
        resetList()
        changeCurrentStep()
    }

    private fun createListIngredients(): String{
        var strIngredients = ""

        for(item in daoRecipeIngredient.getIngredientsFromRecipe(recipe.uid)!!) {
            strIngredients += "* ${item!!.nameIngredient}\n\n"
        }
        return strIngredients
    }

    private fun resetList() {
        stepsList = emptyList<String>().toMutableList()

        stepsList.add(createListIngredients())

        for(step in daoStep.getStepsFromRecipe(recipe.uid)!!){
            step!!.descriptionStep?.let { stepsList.add(it) }
        }
    }

    private fun changeCurrentStep() {
        if(_stepCounter.value == stepsList.size - 1) {
            _lastStep.value = true
        }

        if ((_stepCounter.value!! >= stepsList.size) || (_stepCounter.value!! < 0)){
            _stepFinish.value = true
        } else {

                if(_stepCounter.value == 0) {
                    _title.value = "Ингредиенты для блюда ${recipe.name}:"
                }
                else
                    _title.value = "Шаг № ${stepCounter.value}"

                _description.value = _stepCounter.value?.let { stepsList[it] }
        }
}

    fun onNext() {
        _stepCounter.value = (_stepCounter.value)?.plus(1)
        changeCurrentStep()
    }

    fun onBack() {
        _stepCounter.value = (_stepCounter.value)?.minus(1)
        changeCurrentStep()
    }

    fun onFinishComplete() {
        _stepFinish.value = false
        _lastStep.value = false
    }

}
