package com.bignerdranch.android.macrotracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.macrotracker.MealPreset
import java.util.*

@Dao
interface MealPresetDao {

    @Query("SELECT * FROM mealPreset") //get a complete list of meal presets from the database
    fun getMealPresets(): LiveData<List<MealPreset>>

    @Query("SELECT * FROM mealPreset WHERE id=(:id)") //retrieve a meal preset from database given its ID
    fun getMealPreset(id:UUID): LiveData<MealPreset?>

    @Update
    fun updateMealPreset(meal: MealPreset) //save changes made to a meal preset in the database

    @Insert
    fun addMealPreset(meal: MealPreset) //add a new meal preset to the database

    @Delete
    fun deleteMealPreset(meal: MealPreset) //remove a meal preset from the database
}