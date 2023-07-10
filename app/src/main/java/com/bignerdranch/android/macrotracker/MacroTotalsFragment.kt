package com.bignerdranch.android.macrotracker

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

const val CALORIE_KEY = "calories"
const val FAT_KEY = "fat"
const val CARB_KEY = "carbs"
const val PROTEIN_KEY = "protein"
const val CALORIE_GOAL_KEY = "calorie goal"
const val FAT_GOAL_KEY = "fat goal"
const val CARB_GOAL_KEY = "carb goal"
const val PROTEIN_GOAL_KEY = "protein goal"
const val LOGGED_DATE_KEY = "loggedDate"

class MacroTotalsFragment : Fragment() {

    interface Callbacks{ //used to communicate with the presiding activity
        fun addMacros(macroTotalsFragment: MacroTotalsFragment)
        fun setGoals(macroTotalsFragment: MacroTotalsFragment)
    }
    private var callbacks : Callbacks? = null

    private  lateinit var buttonAdd : Button //objects to store GUI elements
    private lateinit var buttonReset : Button
    private lateinit var buttonSetGoals : Button
    private  lateinit var calorieMeter : ProgressBar
    private  lateinit var fatMeter : ProgressBar
    private  lateinit var carbMeter : ProgressBar
    private  lateinit var proteinMeter : ProgressBar
    private lateinit var calorieLabel : TextView
    private lateinit var fatLabel : TextView
    private lateinit var carbLabel : TextView
    private lateinit var proteinLabel : TextView

    var calories : Int = 0 //instance variables to store macronutrient values and goals
    var gramsFat : Int = 0
    var gramsCarbs : Int = 0
    var gramsProtein : Int = 0
    var calorieGoals : MutableList<Int> = mutableListOf<Int>(0,0,0,0,0,0,0)
    var fatGoals : MutableList<Int> = mutableListOf<Int>(0,0,0,0,0,0,0)
    var carbGoals : MutableList<Int> = mutableListOf<Int>(0,0,0,0,0,0,0)
    var proteinGoals : MutableList<Int> = mutableListOf<Int>(0,0,0,0,0,0,0)

    private lateinit var sharedPref : SharedPreferences //sharedpref used to give persistent storage to macronutrient values

    private val dateFormatter = SimpleDateFormat.getDateInstance() //date tracker object for in-app date tracking
    private lateinit var loggedDate : Date

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate this fragment's XML layout onto the screen
        val view = inflater.inflate(R.layout.macro_totals_page, container, false)

        buttonAdd = view.findViewById(R.id.buttonAdd) //link GUI objects to the corresponding XML elements
        buttonReset = view.findViewById(R.id.buttonResetTotals)
        buttonSetGoals = view.findViewById(R.id.buttonSetGoal)
        calorieMeter = view.findViewById(R.id.calorieMeter)
        fatMeter = view.findViewById(R.id.fatMeter)
        carbMeter = view.findViewById(R.id.carbMeter)
        proteinMeter = view.findViewById(R.id.proteinMeter)
        calorieLabel = view.findViewById(R.id.calorieLabel)
        fatLabel = view.findViewById(R.id.fatLabel)
        carbLabel = view.findViewById(R.id.carbLabel)
        proteinLabel = view.findViewById(R.id.proteinLabel)

        updateUI() //make sure the screen is updated to reflect the app's values

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks? //initialize callbacks reference using the current context
    }

    override fun onStart() {
        super.onStart()

        buttonAdd.setOnClickListener{ //instruct activity to open the addmacrosfragment
            callbacks?.addMacros(this)
        }
        buttonSetGoals.setOnClickListener { //instruct activity to open the setgoalsfragment
            callbacks?.setGoals(this)
        }
        buttonReset.setOnClickListener { //reset the daily macronutrient values back to 0
            val warning = AlertDialog.Builder(requireContext()).apply { //display a dialog prompting user to verify this decision
                setMessage("Are you sure you want to reset your daily totals?")
                setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
                    resetMacros()
                })
                setNegativeButton("No"){_, _ ->
                }
            }
            warning.show()
        }

        view?.viewTreeObserver?.addOnGlobalLayoutListener { //update UI every time this fragment's layout is rendered
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) //initialize sharedpref to retrieve stored values
        loadTrackers() //load any values stored in sharedpref
        if(isNewDay()) resetMacros() //reset the daily counters if the current date is later than the last logged in date
    }

    fun addToMacros(calories : Int, fat : Int, carbs : Int, protein : Int){ //add given macronutrient values to daily totals
        this.calories += calories
        gramsFat += fat
        gramsCarbs += carbs
        gramsProtein += protein
        saveTrackers()
    }

    fun setGoals(calories : Int, fat : Int, carbs : Int, protein : Int){ //change macro goal values based on given inputs
        for(pos in 0..6){
            calorieGoals[pos] = calories
            fatGoals[pos] = fat
            carbGoals[pos] = carbs
            proteinGoals[pos] = protein
        }
        saveTrackers()
    }

    fun setGoals(calories : Int, fat : Int, carbs : Int, protein : Int, day: Int){ //change macro goal values based on given inputs
        calorieGoals[day]  = calories
        fatGoals[day] = fat
        carbGoals[day] = carbs
        proteinGoals[day] = protein
        saveTrackers()
    }

    private fun updateUI(){ //adjust on-screen values to reflect values stored in the app
        calorieMeter.max = calorieGoals[getWeekDay()]
        fatMeter.max = fatGoals[getWeekDay()]
        carbMeter.max = carbGoals[getWeekDay()]
        proteinMeter.max = proteinGoals[getWeekDay()]
        calorieMeter.progress = calories
        fatMeter.progress = gramsFat
        carbMeter.progress = gramsCarbs
        proteinMeter.progress = gramsProtein
        calorieLabel.text = getString(R.string.calorieLabel, calories, calorieGoals[getWeekDay()])
        fatLabel.text = getString(R.string.fatLabel, gramsFat, fatGoals[getWeekDay()])
        carbLabel.text = getString(R.string.carbLabel, gramsCarbs, carbGoals[getWeekDay()])
        proteinLabel.text = getString(R.string.proteinLabel, gramsProtein, proteinGoals[getWeekDay()])
    }

    private fun loadTrackers(){ //retrieve stored values from sharedpref and use them to initialize app values
        calories = sharedPref.getInt(CALORIE_KEY, 0)
        gramsFat = sharedPref.getInt(FAT_KEY, 0) 
        gramsCarbs = sharedPref.getInt(CARB_KEY, 0) 
        gramsProtein = sharedPref.getInt(PROTEIN_KEY, 0) 
        try{
            calorieGoals = goalsToList(sharedPref.getString(CALORIE_GOAL_KEY, String()) ?: String())
            fatGoals = goalsToList(sharedPref.getString(FAT_GOAL_KEY, String()) ?: String())
            carbGoals = goalsToList(sharedPref.getString(CARB_GOAL_KEY, String()) ?: String())
            proteinGoals = goalsToList(sharedPref.getString(PROTEIN_GOAL_KEY, String()) ?: String())
        } catch (err: ClassCastException){ }
        loggedDate = dateFormatter.parse(sharedPref.getString(LOGGED_DATE_KEY, dateFormatter.format(getLocalDate()))!!) ?: getLocalDate()
    }

    private fun goalsToList(goals: String): MutableList<Int>{
        return if(goals.isNotEmpty()){
            val goalStr = goals.trimEnd()
            val stringList = goalStr.split(' ')
            val intList = mutableListOf<Int>()
            for(goal in stringList){
                intList += goal.toInt()
            }
            intList
        } else mutableListOf<Int>(0,0,0,0,0,0,0)
    }

    private fun saveTrackers(){ //store the current app values in sharedpref for persistent storage
        sharedPref.edit()?.putInt(CALORIE_KEY, calories)?.apply()
        sharedPref.edit()?.putInt(FAT_KEY, gramsFat)?.apply()
        sharedPref.edit()?.putInt(CARB_KEY, gramsCarbs)?.apply()
        sharedPref.edit()?.putInt(PROTEIN_KEY, gramsProtein)?.apply()
        sharedPref.edit()?.putString(CALORIE_GOAL_KEY, goalsToString(calorieGoals))?.apply()
        sharedPref.edit()?.putString(FAT_GOAL_KEY, goalsToString(fatGoals))?.apply()
        sharedPref.edit()?.putString(CARB_GOAL_KEY, goalsToString(carbGoals))?.apply()
        sharedPref.edit()?.putString(PROTEIN_GOAL_KEY, goalsToString(proteinGoals))?.apply()
        sharedPref.edit()?.putString(LOGGED_DATE_KEY, dateFormatter.format(getLocalDate()))?.apply()
    }
    
    private fun goalsToString(goals: MutableList<Int>): String{
        var goalString = ""
        for(goal in goals){
            goalString += "$goal "
        }
        return goalString
    } 

    private fun isNewDay() : Boolean{ //determine if the day has changed since the last login time
        return getLocalDate() > loggedDate
    }

    private fun getWeekDay(): Int{
        val cal = Calendar.getInstance()
        cal.time = getLocalDate()
        return cal.get(Calendar.DAY_OF_WEEK) - 1
    }

    private fun getLocalDate() : Date{ //retrieve the date for the devices local time
        return dateFormatter.parse(dateFormatter.format(Calendar.getInstance().time))!!
    }
    
    private fun resetMacros(){ //reset daily trackers to 0
        calories = 0
        gramsFat = 0
        gramsCarbs = 0
        gramsProtein = 0
    }

    companion object{ //used to return an instance of this fragment with the proper conditions applied
        fun newInstance(): MacroTotalsFragment{
            return MacroTotalsFragment()
        }
    }
}