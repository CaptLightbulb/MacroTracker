package com.bignerdranch.android.macrotracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class MealEditViewModel: ViewModel() {
    private val mealRepo = MealPresetRepo.fetch() //retrieve the existing instance of the meal preset database repository
    private val mealIdLiveData = MutableLiveData<UUID>()

    var mealLiveData: LiveData<MealPreset?> = Transformations.switchMap(mealIdLiveData){mealId-> //retrieve a livedata of the meal in the database that has the given id
        mealRepo.getMealPreset(mealId)
    }

    fun loadMeal(mealId: UUID){ //set the value of the meal preset livedata based on the given id
        mealIdLiveData.value = mealId
    }

    fun saveMeal(meal: MealPreset){ //commit changes to the given meal preset in the database
        mealRepo.updateMealPreset(meal)
    }

    fun deleteMeal(meal: MealPreset){ //delete the given meal preset from the database
        mealRepo.deleteMealPreset(meal)
    }
}