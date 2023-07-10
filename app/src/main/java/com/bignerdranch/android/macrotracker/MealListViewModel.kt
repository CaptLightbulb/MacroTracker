package com.bignerdranch.android.macrotracker

import androidx.lifecycle.ViewModel

class MealListViewModel: ViewModel() {

    private val mealRepo = MealPresetRepo.fetch() //retrieve the existing instance of the meal preset database repository
    val mealListLiveData = mealRepo.getMealPresets()

    fun addMeal(meal: MealPreset){ //add a new meal preset to database
        mealRepo.addMealPreset(meal)
    }

    fun deleteMeal(meal: MealPreset){ //delete a meal from the database
        mealRepo.deleteMealPreset(meal)
    }
}