package com.example.android.recipebook.helper_mode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.recipebook.R
import com.example.android.recipebook.RecipeListActivity
import com.example.android.recipebook.database.RecipeDatabase
import com.example.android.recipebook.database.entities.Recipe
import com.example.android.recipebook.databinding.ActivityHelperModeBinding


class HelperModeActivity : AppCompatActivity() {
    private lateinit var viewModel: HelperModeViewModel
    private lateinit var binding: ActivityHelperModeBinding
    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_helper_mode)

         val toolbar: Toolbar = findViewById(R.id.toolbar)
         setSupportActionBar(toolbar)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)


        recipe = intent.getSerializableExtra("recipe") as Recipe?

        val application = requireNotNull(this).application
        val daoRecipeIngredient = RecipeDatabase.getInstance(application)!!.getRecipeIngredientDao()
        val daoStep = RecipeDatabase.getInstance(application)!!.getStepDao()
        val viewModelFactory = HelperModeViewModelFactory(daoRecipeIngredient!!, daoStep!!, recipe!!, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(HelperModeViewModel::class.java)


        binding.buttonNext.setOnClickListener {
            viewModel.onNext()
        }

        binding.buttonBack.setOnClickListener {
            viewModel.onBack()
        }

        viewModel.title.observe(this, Observer { title ->
            binding.title.text = title
        })

        viewModel.description.observe(this, Observer { newStep ->
            binding.description.text = newStep
        })

        viewModel.lastStep.observe(this, Observer { lastStep ->
            if (lastStep) {
                binding.buttonNext.text = "Финиш"
            }
        })

        viewModel.stepFinish.observe(this, Observer { isFinished ->
            if (isFinished) {
                val intent = Intent(this, RecipeListActivity::class.java)
                startActivity(intent)
                viewModel.onFinishComplete()
            }
        })

    }

}

