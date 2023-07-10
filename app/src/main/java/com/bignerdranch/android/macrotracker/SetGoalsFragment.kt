package com.bignerdranch.android.macrotracker

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

const val ALL_DAYS = -1

class  SetGoalsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private  lateinit var  calorieGoal : EditText //objects to store GUI elements
    private  lateinit var  fatGoal : EditText
    private  lateinit var  carbGoal : EditText
    private  lateinit var  proteinGoal : EditText
    private  lateinit var setGoalsButton: Button
    private lateinit var parentFragment: MacroTotalsFragment
    private lateinit var dayDropDown: Spinner

    private var weekDay: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate the layout for this fragment
        val view = inflater.inflate(R.layout.set_goals_page, container, false)

        calorieGoal = view.findViewById(R.id.setGoalCalorieInput) //link GUI objects to XML elements
        fatGoal = view.findViewById(R.id.setGoalFatsInput)
        carbGoal = view.findViewById(R.id.setGoalCarbsInput)
        proteinGoal = view.findViewById(R.id.setGoalProteinInput)
        setGoalsButton = view.findViewById(R.id.buttonSetGoals)
        dayDropDown = view.findViewById(R.id.dayDropDown)
        ArrayAdapter.createFromResource(requireContext(), R.array.weekdays, android.R.layout.simple_spinner_item)
            .also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dayDropDown.adapter = arrayAdapter
            }
        dayDropDown.onItemSelectedListener = this

        initInputFields() //initialize the values of the input fields to match the existing values

        return view
    }

    override fun onStart() {
        super.onStart()

        setGoalsButton.setOnClickListener{ //send the input goal info back to the macrototals fragment if the input is valid
            try {
                val calories : Int = getIntFromInput(calorieGoal.text.toString())
                val fats : Int = getIntFromInput(fatGoal.text.toString())
                val carbs : Int = getIntFromInput(carbGoal.text.toString())
                val protein : Int = getIntFromInput(proteinGoal.text.toString())
                if(weekDay > -1) parentFragment.setGoals(calories, fats, carbs, protein, weekDay)
                else parentFragment.setGoals(calories, fats, carbs, protein)
            } catch (e: Exception){
                val warning = AlertDialog.Builder(requireContext()).apply {
                    setMessage("Error: Please only enter integer values")
                }
                warning.show()
            }
        }



    }

    private fun getIntFromInput(input: String) : Int{ //return an integer value based on the given string or return 0 if the conversion failed
        try{
            return input.toInt();
        } catch (ex : Exception){
            return 0
        }
    }

    private fun initInputFields(){ //initialize the values of the input fields to match the existing values
        if(weekDay > -1){
            calorieGoal.setText(parentFragment.calorieGoals[weekDay].toString())
            fatGoal.setText(parentFragment.fatGoals[weekDay].toString())
            carbGoal.setText(parentFragment.carbGoals[weekDay].toString())
            proteinGoal.setText(parentFragment.proteinGoals[weekDay].toString())
        }else if (allDaysMatch()){
            calorieGoal.setText(parentFragment.calorieGoals[0].toString())
            fatGoal.setText(parentFragment.fatGoals[0].toString())
            carbGoal.setText(parentFragment.carbGoals[0].toString())
            proteinGoal.setText(parentFragment.proteinGoals[0].toString())
        }
    }
    
    private fun allDaysMatch(): Boolean{
        for(pos in 0..5){
            if(parentFragment.calorieGoals[pos] != parentFragment.calorieGoals[pos+1] ||
                parentFragment.fatGoals[pos] != parentFragment.fatGoals[pos+1] ||
                parentFragment.carbGoals[pos] != parentFragment.carbGoals[pos+1] ||
                parentFragment.proteinGoals[pos] != parentFragment.proteinGoals[pos+1]) return false
        }
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        weekDay = position - 1
        initInputFields()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    companion object{ //return and instance of this fragment with the correct conditions
        fun newInstance(macroTotalsFragment: MacroTotalsFragment): SetGoalsFragment{
            return SetGoalsFragment().apply {
                parentFragment = macroTotalsFragment
            }
        }
    }
}