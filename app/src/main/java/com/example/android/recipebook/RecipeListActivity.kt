package com.example.android.recipebook

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.recipebook.adapter.RecipesAdapter
import com.example.android.recipebook.create_edit_recipe.CreateEditRecipeActivity
import com.example.android.recipebook.database.RecipeDatabase
import com.example.android.recipebook.database.entities.Recipe
import com.example.android.recipebook.helper_mode.HelperModeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.ref.WeakReference
import java.util.*


class RecipeListActivity : AppCompatActivity(), RecipesAdapter.OnRecipeItemClick
{

    private var textViewMsg: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var recipeDatabase: RecipeDatabase? = null
    private var recipes: MutableList<Recipe>? = null
    private var recipesAdapter: RecipesAdapter? = null
    private lateinit var emptyTextViewSearch: View
    private var searchView: SearchView? = null
    private var pos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recipeDatabase = RecipeDatabase.getInstance(this)

        initializeVies()
        displayList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView!!.maxWidth = Int.MAX_VALUE
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                recipesAdapter!!.filter!!.filter(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                recipesAdapter!!.filter!!.filter(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun displayList() {
        RetrieveTask(this).execute()
    }

    private class RetrieveTask internal constructor(context: RecipeListActivity?) :
        AsyncTask<Void?, Void?, List<Recipe?>?>() {

        private val activityReference: WeakReference<RecipeListActivity?> = WeakReference(context)

        override fun doInBackground(vararg params: Void?): List<Recipe?>? {
            return if (activityReference.get() != null) activityReference.get()!!.recipeDatabase!!.getRecipeDao()
                ?.getRecipes() else null
        }

        override fun onPostExecute(recipes: List<Recipe?>?) {
            if (recipes != null && recipes.isNotEmpty()) {
                activityReference.get()!!.recipes!!.clear()
                activityReference.get()!!.recipes!!.addAll(recipes.filterIsInstance<Recipe>())
                // hides empty text view
                activityReference.get()!!.textViewMsg!!.visibility = View.GONE
                activityReference.get()!!.recipesAdapter!!.notifyDataSetChanged()
            }
        }

    }

    private fun initializeVies() {
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        emptyTextViewSearch = findViewById(R.id.emptyTextViewSearch)
        textViewMsg = findViewById(R.id.database__empty)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener(listener)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recipes = ArrayList()

        recipesAdapter = RecipesAdapter(recipes as ArrayList<Recipe>, this, recipeDatabase!!, emptyTextViewSearch)
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = recipesAdapter
    }

    private val listener =
        View.OnClickListener {
            startActivityForResult(
                Intent(this, CreateEditRecipeActivity::class.java),
                100
            )
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode > 0) {
            if (resultCode == 1) {
                recipes!!.add(data!!.getSerializableExtra("recipe") as Recipe)
            } else if (resultCode == 2) {
                recipes!![pos] = data!!.getSerializableExtra("recipe") as Recipe
            }
            listVisibility()
        }
    }

    private fun createDeleteDialog(posDeleteItem: Int) {
        val builder =
            AlertDialog.Builder(this)
        builder.setMessage(recipes!![posDeleteItem].name!!.capitalize())
            .setTitle("Вы действительно хотите удалить этот рецепт?");

        builder.setPositiveButton("OK") { _, _ ->
            recipeDatabase!!.getRecipeDao()?.deleteRecipe(recipes!![posDeleteItem])
            recipes!!.removeAt(posDeleteItem)
            listVisibility()
        }

        builder.setNegativeButton("Отмена") {_, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRecipeClick(pos: Int) {
        AlertDialog.Builder(this)
            .setTitle("Выберите действие")
            .setItems(
                arrayOf("Удалить", "Редактировать", "Режим помощника")
            ) { _, i ->
                when (i) {
                    0 -> {
                        createDeleteDialog(pos)
                    }
                    1 -> {
                        this.pos = pos
                        startActivityForResult(
                            Intent(
                                this,
                                CreateEditRecipeActivity::class.java
                            ).putExtra("recipe", recipes!![pos]),
                            100
                        )
                    }
                    2 -> {
                        this.pos = pos
                        startActivityForResult(
                            Intent(
                                this,
                                HelperModeActivity::class.java
                            ).putExtra("recipe", recipes!![pos]),
                            100
                        )
                    }
                }
            }.show()
    }

    private fun listVisibility() {
        var emptyMsgVisibility = View.GONE
        if (recipes!!.size == 0) {//no recipes in the dataBase
            if (textViewMsg!!.visibility == View.GONE) emptyMsgVisibility =
                View.VISIBLE
        }
        textViewMsg!!.visibility = emptyMsgVisibility
        recipesAdapter!!.notifyDataSetChanged()
    }


    override fun onDestroy() {
        recipeDatabase!!.cleanUp()
        super.onDestroy()
    }

}
