package com.example.android.recipebook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.recipebook.R
import com.example.android.recipebook.database.RecipeDatabase
import com.example.android.recipebook.database.entities.Recipe


class RecipesAdapter(
    list: MutableList<Recipe>,
    context: Context,
    recipeDatabase: RecipeDatabase,
    emptyTextViewSearch: View
):RecyclerView.Adapter<RecipesAdapter.ViewHolder>(), Filterable {
    private val list: MutableList<Recipe> = list
    private var listFiltered: List<Recipe>? = list
    private val context: Context = context
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val onRecipeItemClick: OnRecipeItemClick
    private val recipeDatabase: RecipeDatabase = recipeDatabase
    private var emptyTextViewSearch: View = emptyTextViewSearch

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = layoutInflater.inflate(R.layout.recipe_list_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textViewTitle: TextView
        var textViewContent: TextView

        override fun onClick(view: View) {
            onRecipeItemClick.onRecipeClick(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
            textViewTitle = itemView.findViewById(R.id.recipe_title)
            textViewContent = itemView.findViewById(R.id.ingredient_text)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text =  listFiltered!![position].name
        var strIngredients = ""
        val ingredients = recipeDatabase.getRecipeIngredientDao()!!.getIngredientsFromRecipe(listFiltered!![position].uid)
                for(item in ingredients!!) {
            strIngredients += item!!.nameIngredient
            strIngredients += ", "
        }
       holder.textViewContent.text = strIngredients.dropLast(2)
    }

    override fun getItemCount(): Int {
        return listFiltered!!.size
    }

    interface OnRecipeItemClick {
        fun onRecipeClick(pos: Int)
    }

    init {
        onRecipeItemClick = context as OnRecipeItemClick
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val query = charSequence.toString().toLowerCase().trim()
                val filtered: ArrayList<Recipe?>

                filtered = if(query.isEmpty()) {
                    list as ArrayList<Recipe?>
                } else {
                    recipeDatabase.getRecipeIngredientDao()!!.getRecipesBySubStringIngredient("$query%")!! as ArrayList<Recipe?>
                }

                val results = FilterResults()
                results.count = filtered.size
                results.values = filtered
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(
                charSequence: CharSequence?,
                results: FilterResults
            ) {
                listFiltered = results.values as List<Recipe>?

                when {
                    list.isEmpty() && listFiltered!!.isEmpty() -> {
                        emptyTextViewSearch.visibility = View.GONE
                    }
                    listFiltered!!.isEmpty() -> emptyTextViewSearch.visibility = View.VISIBLE
                    else -> emptyTextViewSearch.visibility = View.GONE
                }
                notifyDataSetChanged()
            }

        }
    }

}