package com.bignerdranch.android.macrotracker

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MealListFragment : Fragment() {

    interface Callbacks{ //used to communicate with the presiding activity
        fun onMealSelected(mealId: UUID, mealListFragment: MealListFragment)
    }

    private var callbacks: Callbacks? = null

    private lateinit var mealRecyclerView: RecyclerView //objects to store GUI elements
    private lateinit var emptyAddButton: Button
    private lateinit var emptyListLayout: ConstraintLayout
    private lateinit var addMacrosFragment: AddMacrosFragment

    private var adapter: MealPresetAdapter? = MealPresetAdapter(emptyList()) //adapter for recyclerview

    private val thisFragment: MealListFragment = this //a usable reference to this fragment to use in nested classes

    private val mealListViewModel: MealListViewModel by lazy { //retrieve and instance of this fragment's viewmodel
        ViewModelProviders.of(this).get(MealListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks? //initialize callbacks reference using current context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate layout for this fragment
        val view = inflater.inflate(R.layout.meal_list_page, container, false)

        mealRecyclerView = view.findViewById(R.id.meal_recycler_view) //link GUI objects to XML elements
        emptyListLayout = view.findViewById(R.id.emptyListLayout)
        emptyAddButton = view.findViewById(R.id.emptyAddMeal)

        mealRecyclerView.adapter = adapter //set up adapter for recycler view
        mealRecyclerView.layoutManager = LinearLayoutManager(context)

        emptyAddButton.setOnClickListener { //create a new meal preset when button is used
            val meal = MealPreset()
            mealListViewModel.addMeal(meal)
            callbacks?.onMealSelected(meal.id, this)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mealListViewModel.mealListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { meals ->
                meals?.let { //update on-screen info to reflect changes in the app's data
                    updateUI(meals)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_meal_list, menu) //create the options menu on-screen
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_meal_preset->{ //create a new meal preset when the button is selected in the options menu
                val meal = MealPreset()
                mealListViewModel.addMeal(meal)
                callbacks?.onMealSelected(meal.id, this)
                true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(meals: List<MealPreset>){ //keep on-screen info up to date with app data
        emptyListLayout.visibility = View.INVISIBLE
        adapter = MealPresetAdapter(meals)
        mealRecyclerView.adapter = adapter
        if(meals.isEmpty()){
            emptyListLayout.visibility = View.VISIBLE
        }
    }

    fun addMealPresetToTotals(meal: MealPreset){ //add the given meals macronutrients to daily totals
        addMacrosFragment.addToTotals(meal.calories, meal.gramsFat, meal.gramsCarbs, meal.gramsProtein)
        activity?.supportFragmentManager?.popBackStack()
    }

    private inner class MealPresetHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{ //holder for recycler view

        private lateinit var meal : MealPreset

        private val nameLabel: TextView = itemView.findViewById(R.id.mealName) //objects for GUI elements
        private val calorieLabel: TextView = itemView.findViewById(R.id.mealCalorieLabel)
        private val fatLabel: TextView = itemView.findViewById(R.id.mealFatLabel)
        private val carbLabel: TextView = itemView.findViewById(R.id.mealCarbLabel)
        private val proteinLabel: TextView = itemView.findViewById(R.id.mealProteinLabel)
        private val addButton: ImageButton = itemView.findViewById(R.id.buttonMealPresetAdd)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonMealPresetDelete)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(meal: MealPreset){
            this.meal = meal //bind the meal's data to the on-screen GUI elements
            nameLabel.text = this.meal.name
            calorieLabel.text = getString(R.string.mealCalLabel, this.meal.calories)
            fatLabel.text = getString(R.string.mealFatLabel, this.meal.gramsFat)
            carbLabel.text = getString(R.string.mealCarbLabel, this.meal.gramsCarbs)
            proteinLabel.text = getString(R.string.mealProteinLabel, this.meal.gramsProtein)

            addButton.setOnClickListener { //add the meal's nutrients to daily totals when button is used
                addMealPresetToTotals(meal)
            }
            deleteButton.setOnClickListener { //delete meal from database when button is used
                val warning = AlertDialog.Builder(requireContext()).apply { //alert dialog to verify this decision
                    setMessage("Are you sure you want to delete this recipe?")
                    setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
                        mealListViewModel.deleteMeal(meal)
                        activity?.supportFragmentManager?.popBackStack()
                    })
                    setNegativeButton("No"){_, _ ->
                    }
                }
                warning.show()
            }
        }

        override fun onClick(v: View?) { //open up the edit page when a meal is selected from the list
            callbacks?.onMealSelected(meal.id, thisFragment)
        }
    }

    private inner class MealPresetAdapter(var meals: List<MealPreset>): RecyclerView.Adapter<MealPresetHolder>(){ //adapter for recycler view
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPresetHolder {
            val view = layoutInflater.inflate(R.layout.meal_list_item, parent, false) //inflate list item layout and send to the hodler
            return MealPresetHolder(view)
        }

        override fun getItemCount(): Int = meals.size

        override fun onBindViewHolder(holder: MealPresetHolder, position: Int) { //bind each meal to the corresponding holder to display the list
            val meal = meals[position]
            holder.bind(meal)
        }
    }

    companion object{ //return an instance of this fragment with the correct conditions
        fun newInstance(addMacrosFragment: AddMacrosFragment): MealListFragment{
            val fragment = MealListFragment()
            fragment.addMacrosFragment = addMacrosFragment
            return fragment
        }
    }
}