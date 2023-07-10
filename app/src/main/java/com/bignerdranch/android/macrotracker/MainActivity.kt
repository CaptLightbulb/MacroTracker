package com.bignerdranch.android.macrotracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class MainActivity : AppCompatActivity(), MacroTotalsFragment.Callbacks, AddMacrosFragment.Callbacks, MealListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) { //open up the macro totals fragment upon first starting up
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(currentFragment == null){
            val fragment = MacroTotalsFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        }
    }

    override fun addMacros(macroTotalsFragment: MacroTotalsFragment) { //open up the addmacros fragment upon request
        val fragment = AddMacrosFragment.newInstance(macroTotalsFragment)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun setGoals(macroTotalsFragment: MacroTotalsFragment) { //open up the setgoals fragment upon request
        val fragment = SetGoalsFragment.newInstance(macroTotalsFragment)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun viewMealPresetList(addMacrosFragment: AddMacrosFragment) { //open up the meal preset list screen upon request
        val fragment = MealListFragment.newInstance(addMacrosFragment)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun onMealSelected(mealId: UUID, mealListFragment: MealListFragment) { //open up the edit page for a meal with one is selected from the list
        val fragment = MealEditFragment.newInstance(mealId, mealListFragment)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
}