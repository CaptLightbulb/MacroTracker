package com.bignerdranch.android.macrotracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class AddMacrosFragment : Fragment() {

    private  lateinit var calorieInput : EditText //create references to GUI elements
    private  lateinit var fatInput : EditText
    private  lateinit var carbInput : EditText
    private  lateinit var  proteinInput : EditText
    private lateinit var addButton : Button
    private lateinit var buttonViewPresets: Button

    private lateinit var  parentFragment: MacroTotalsFragment //reference to the macroTotalsFragment

    private var callbacks: Callbacks? = null

    interface Callbacks{ //used to communicate with the presiding activity
        fun viewMealPresetList(addMacrosFragment: AddMacrosFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflated the GUI for this fragment onto the screen
        val view = inflater.inflate(R.layout.add_macros_page, container, false)

        calorieInput = view.findViewById(R.id.addCalorieInput) //link GUI objects to the corresponding XML elements
        fatInput = view.findViewById(R.id.addFatsInput)
        carbInput = view.findViewById(R.id.addCarbsInput)
        proteinInput = view.findViewById(R.id.addProteinInput)
        addButton = view.findViewById(R.id.buttonAddMacros)
        buttonViewPresets = view.findViewById(R.id.buttonViewPresets)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks? //initialize callbacks with the current context
    }

    override fun onStart() {
        super.onStart()

        addButton.setOnClickListener{
            val calories : Int = getIntFromInput(calorieInput.text.toString()) //collect numeric inputs from the entry fields
            val fats : Int = getIntFromInput(fatInput.text.toString())
            val carbs : Int = getIntFromInput(carbInput.text.toString())
            val protein : Int = getIntFromInput(proteinInput.text.toString())
            addToTotals(calories, fats, carbs, protein) //add numeric inputs to total daily values
        }
        buttonViewPresets.setOnClickListener {
            callbacks?.viewMealPresetList(this) //tell the presiding activity to open the meal preset list fragment
        }
    }

    private fun getIntFromInput(input: String) : Int{ //return an integer value from the given string, or return 0 if the conversion fails
        return try{
            input.toInt();
        } catch (ex : Exception){
            0
        }
    }

    fun addToTotals(calories: Int, fat: Int, carbs: Int, protein: Int){ //add given macros to the daily totals and return to the previous screen
        parentFragment.addToMacros(calories,fat,carbs,protein)
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object{ //used to return an instance of this class with the appropriate conditions applied
        fun newInstance(macroTotalsFragment: MacroTotalsFragment): AddMacrosFragment{
            return AddMacrosFragment().apply {
                parentFragment = macroTotalsFragment
            }
        }
    }
}