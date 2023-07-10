package com.bignerdranch.android.macrotracker

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*

private const val ARG_MEAL_ID = "mealId"

class MealEditFragment: Fragment() {

    private lateinit var meal: MealPreset

    private lateinit var nameField: EditText//objects to store GUI elements
    private lateinit var calField: EditText
    private lateinit var fatField: EditText
    private lateinit var carbField: EditText
    private lateinit var proteinField: EditText
    private lateinit var addButton: Button
    private lateinit var deleteButton: Button

    private lateinit var mealListFragment: MealListFragment //reference to the fragment for the list of meal presets

    private val mealEditViewModel: MealEditViewModel by lazy { //initialize an instance of this fragment's viewmodel
        ViewModelProviders.of(this).get(MealEditViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) { //link the viewModel to the given meal
        super.onCreate(savedInstanceState)
        meal = MealPreset()
        val mealId: UUID = arguments?.getSerializable(ARG_MEAL_ID) as UUID
        mealEditViewModel.loadMeal(mealId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate the layout for this fragment
        val view = inflater.inflate(R.layout.meal_edit_page, container, false)

        nameField = view.findViewById(R.id.editNameField) //link GUI objects to their XML elements
        calField = view.findViewById(R.id.editCalorieField)
        fatField = view.findViewById(R.id.editFatField)
        carbField = view.findViewById(R.id.editCarbField)
        proteinField = view.findViewById(R.id.editProteinField)
        addButton = view.findViewById(R.id.buttonAddMealToTotals)
        deleteButton = view.findViewById(R.id.buttonDeleteMeal)

        return view
    }

    override fun onStart() {
        super.onStart()

        nameField.addTextChangedListener { //textChangedListeners to automatically update the stored values when the user changes them
            meal.name = it.toString()
        }
        calField.addTextChangedListener {
            try {
                meal.calories = it.toString().toInt()
            } catch (e: Exception){}
        }
        fatField.addTextChangedListener {
            try {
                meal.gramsFat = it.toString().toInt()
            } catch (e: Exception){}
        }
        carbField.addTextChangedListener {
            try {
                meal.gramsCarbs = it.toString().toInt()
            } catch (e: Exception){}
        }
        proteinField.addTextChangedListener {
            try {
                meal.gramsProtein = it.toString().toInt()
            } catch (e: Exception){}
        }

        addButton.setOnClickListener { //add a new meal preset to database when the button is used
            addMealPresetToTotals(this.meal)
        }

        deleteButton.setOnClickListener { //delete the chosen meal preset from the database when the button is used
            val warning = AlertDialog.Builder(requireContext()).apply { //alert dialog to prompt user to verify this decision
                setMessage("Are you sure you want to delete this recipe?")
                setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
                    mealEditViewModel.deleteMeal(meal)
                    activity?.supportFragmentManager?.popBackStack()
                })
                setNegativeButton("No"){_, _ ->
                }
            }
            warning.show()
        }
    }

    override fun onStop() { //save the meal when the user exits this screen
        super.onStop()
        mealEditViewModel.saveMeal(meal)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mealEditViewModel.mealLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { meal ->
                meal?.let { //update on-screen information in real time based on the state of the meal preset object
                    this.meal = meal
                    updateUI()
                }
            }
        )
    }

    private fun updateUI(){ //keep on-screen info up to date with in-app info
        nameField.setText(meal.name)
        calField.setText(meal.calories.toString())
        fatField.setText(meal.gramsFat.toString())
        carbField.setText(meal.gramsCarbs.toString())
        proteinField.setText(meal.gramsProtein.toString())
    }

    private fun addMealPresetToTotals(meal: MealPreset){ //add the info for this meal preset to the current daily macronutrient totals
        mealListFragment.addMealPresetToTotals(meal)
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object{ //used to return and instance of this fragment with the correct conditions
        fun newInstance(mealId: UUID, mealListFragment: MealListFragment): MealEditFragment{
            val args = Bundle().apply {
                putSerializable(ARG_MEAL_ID, mealId)
            }
            return MealEditFragment().apply {
                arguments = args
                this.mealListFragment = mealListFragment
            }
        }
    }
}