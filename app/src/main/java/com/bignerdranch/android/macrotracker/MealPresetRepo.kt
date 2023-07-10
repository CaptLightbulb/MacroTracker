package com.bignerdranch.android.macrotracker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.macrotracker.database.MealPresetDB
import java.util.*
import java.util.concurrent.Executors

class MealPresetRepo private constructor(context: Context){

    private val mealPresetDB: MealPresetDB = //setup a reference to the meal preset database
        Room.databaseBuilder(context.applicationContext, MealPresetDB::class.java, "mealPresetDB").build()

    private val dao = mealPresetDB.mealDao() //store reference to the database's DAO
    private val thread = Executors.newSingleThreadExecutor() //thread object to handle database interactions

    fun getMealPresets(): LiveData<List<MealPreset>> = dao.getMealPresets() //retrieve list of meal presets from database
    fun getMealPreset(id: UUID) : LiveData<MealPreset?> = dao.getMealPreset(id) //retrieve meal preset from database using given ID

    fun updateMealPreset(meal: MealPreset){ //commit changes to a meal preset in the database
        thread.execute{
            dao.updateMealPreset(meal)
        }
    }

    fun addMealPreset(meal: MealPreset){ //add meal preset to database
        thread.execute{
            dao.addMealPreset(meal)
        }
    }

    fun deleteMealPreset(meal : MealPreset){ //delete meal preset form database
        thread.execute {
            dao.deleteMealPreset(meal)
        }
    }

    companion object{ //used to manage a singleton instance of this repository
        private var repoInstance: MealPresetRepo? = null

        fun build(context: Context){
            if(repoInstance == null){
                repoInstance = MealPresetRepo(context)
            }
        }

        fun fetch(): MealPresetRepo{
            return repoInstance ?: throw Exception("Repo has not been built yet")
        }
    }
}