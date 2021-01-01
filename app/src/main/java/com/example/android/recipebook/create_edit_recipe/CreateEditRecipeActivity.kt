package com.example.android.recipebook.create_edit_recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.recipebook.R
import com.example.android.recipebook.database.RecipeDatabase
import com.example.android.recipebook.database.entities.Recipe
import com.google.android.material.textfield.TextInputLayout


class CreateEditRecipeActivity : AppCompatActivity() {
    private var recipeDatabase: RecipeDatabase? = null
    private lateinit var viewModel: CreateEditRecipeViewModel
    private var recipe: Recipe? = null
    private lateinit var saveButton: Button

    private lateinit var textInIngredient: TextInputLayout
    private lateinit var textInStep: TextInputLayout

    var buttonAddIngredient: ImageButton? = null
    var buttonAddCookingStep: ImageButton? = null
    private var containerIngredients: LinearLayout? = null
    private var containerCookingSteps: LinearLayout? = null

    private lateinit var nameRecipe: TextInputLayout
    private var update = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        recipe = intent.getSerializableExtra("recipe") as Recipe?

        val application = requireNotNull(this).application
        recipeDatabase = RecipeDatabase.getInstance(application)

        nameRecipe = findViewById(R.id.enter_name_recipe)

        addingFieldsForIngredients()
        addingFieldsForCookingSteps()

       saveButton = findViewById(R.id.save_recipe)

        val viewModelFactory = CreateEditViewModelFactory(recipeDatabase, recipe, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateEditRecipeViewModel::class.java)

        if (savedInstanceState != null) {
            nameRecipe.editText!!.setText(savedInstanceState.getString("name"))

        val ingredients = savedInstanceState.getString("ingredients");
        val steps = savedInstanceState.getString("steps");

            if(ingredients!!.isNotEmpty()) {
                val ingredientList = ingredients.split(",")
                fillFields(containerIngredients, ingredientList,"Введите ингредиент")
            }

            if(steps!!.isNotEmpty()) {
                val stepsList = steps.split(",")
                fillFields(containerCookingSteps!!, stepsList,"Введите шаг")
            }

    }

        if ((intent.getSerializableExtra("recipe") as Recipe?) != null) {
            recipe = intent.getSerializableExtra("recipe") as Recipe?
            supportActionBar!!.title = "РЕДАКТИРОВАТЬ РЕЦЕПТ"
            update = true
            saveButton.text = "Обновить"
            nameRecipe.editText!!.setText(viewModel.getNameRecipe())
            if(savedInstanceState == null) {
                fillFields(containerIngredients, viewModel.getNamesIngredients()!!,"Введите ингредиент")
                fillFields(containerCookingSteps!!, viewModel.getNamesSteps()!!,"Введите шаг")
            }
        }

        nameRecipe.editText!!.addTextChangedListener(editTextWatcher);
        textInIngredient.editText!!.addTextChangedListener(editTextWatcher);
        textInStep.editText!!.addTextChangedListener(editTextWatcher);
        saveButton.setOnClickListener {
            if (validateNameRecipe() && validateInputIngredients() && validateInputSteps()) {
                if (update) {
                    viewModel.updateRecipe(
                        nameRecipe.editText!!.text.toString(),
                        containerIngredients,
                        containerCookingSteps
                    )
                    setResult(recipe, 2)
                } else {
                    viewModel.createRecipe(
                        nameRecipe.editText!!,
                        containerIngredients,
                        containerCookingSteps
                    )
                    viewModel.flag.observe(this, Observer { newFag ->
                        recipe = viewModel.recipe
                        setResult(recipe, newFag)
                    })
                }
            }
        }
    }

    private val editTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            saveButton.isEnabled = validateNameRecipe() && validateInputIngredients() && validateInputSteps()
        }

        override fun afterTextChanged(s: Editable) {
            saveButton.isEnabled = validateNameRecipe() && validateInputIngredients() && validateInputSteps()
        }
    }

    private fun isEmptyContainer(container: LinearLayout?): Boolean{
        var thisChild: View?
        var textView: EditText?

        for(item in 0 until container!!.childCount) {
            thisChild = container.getChildAt(item)
            textView = thisChild.findViewById<View>(R.id.text_out) as EditText
            if(textView.text.isNotEmpty()) {
                return false
            }
        }
        return true
    }

    private fun validateInputIngredients(): Boolean {
        if(isEmptyContainer(containerIngredients)){
            textInIngredient.error = "Добавьте хотя бы один ингредиент"
            return false
        }
        textInIngredient.error = null
        return true
    }

    private fun validateInputSteps(): Boolean {
        if(isEmptyContainer(containerCookingSteps)){
            textInStep.error = "Добавьте хотя бы один шаг"
            return false
        }
        textInStep.error = null
        return true
    }

    private fun validateNameRecipe(): Boolean {
        if(nameRecipe.editText!!.text.isEmpty()){
            nameRecipe.error = "Поле не может быть пустым"
            return false
        }
        nameRecipe.error = null
        return true
    }

    override fun onSaveInstanceState(savedData: Bundle) {
        var thisChild: View?
        var childTextView: EditText?
        var ingredients = ""
        var steps = ""

        for(i in 0 until containerIngredients!!.childCount) {
            thisChild = containerIngredients!!.getChildAt(i)
            childTextView = thisChild.findViewById<View>(R.id.text_out) as EditText
            ingredients += childTextView.text.toString()
            ingredients+=","
        }

        for(i in 0 until containerCookingSteps!!.childCount) {
            thisChild = containerCookingSteps!!.getChildAt(i)
            childTextView = thisChild.findViewById<View>(R.id.text_out) as EditText
            steps += childTextView.text.toString()
            steps+=","
        }

        savedData.putString("name", nameRecipe.editText!!.text.toString())
        savedData.putString("ingredients", ingredients.dropLast(1))
        savedData.putString("steps", steps.dropLast(1))
        super.onSaveInstanceState(savedData)
    }

    private fun fillFields(
        container: LinearLayout?, list: List<String>,
        strHint: String) {
        for(name in list) {
            addFieldOnPlus(container!!, name, strHint)
        }
    }

    private fun setResult(recipe: Recipe?, flag: Int) {
        setResult(flag, Intent().putExtra("recipe", recipe))
        finish()
    }


    private fun initializeFieldEnterIngredient() {
        textInIngredient = findViewById(R.id.text_ingredient)

        buttonAddIngredient = findViewById(R.id.add_ingredient)
        containerIngredients = findViewById(R.id.container_ingredients)
    }


    private fun addButtonDeleteField(addViewCurrentStep: View ){
        val buttonRemove =
            addViewCurrentStep.findViewById<View>(R.id.remove) as ImageButton

        val thisListener: View.OnClickListener =
            View.OnClickListener {
                (addViewCurrentStep.parent as LinearLayout).removeView(addViewCurrentStep)
            }

        buttonRemove.setOnClickListener(thisListener)
    }

    private fun addFieldOnPlus(container: LinearLayout, textFiles: String, hint: String){
        val layoutInflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val addFieldView: View =
            layoutInflater.inflate(R.layout.add_row_with_button, null)
        val textOut =
            addFieldView.findViewById<View>(R.id.text_out) as EditText
        textOut.setText(textFiles)
        textOut.hint = hint

        addButtonDeleteField(addFieldView)
        container.addView(addFieldView)
    }

    private fun addingFieldsForIngredients() {
        initializeFieldEnterIngredient()
        buttonAddIngredient!!.setOnClickListener {
            addFieldOnPlus(containerIngredients!!, textInIngredient.editText!!.text.toString(), "Введите ингредиент")
            textInIngredient.editText!!.text = null
        }
    }

    private fun initializeFieldEnterStep(){
        textInStep = findViewById(R.id.text_step)

        buttonAddCookingStep = findViewById(R.id.add_step)
        containerCookingSteps = findViewById(R.id.container_cooking_steps)
    }

    private fun addingFieldsForCookingSteps() {
        initializeFieldEnterStep()

        buttonAddCookingStep!!.setOnClickListener {
            addFieldOnPlus(containerCookingSteps!!, textInStep.editText!!.text.toString(), "Введите шаг")
            textInStep.editText!!.text = null
        }
    }
}