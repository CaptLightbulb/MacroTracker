package com.bignerdranch.android.macrotracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.macrotracker.MealPreset


@Database(entities = [MealPreset::class], version = 1)
@TypeConverters(TypeConversion::class)
abstract class MealPresetDB: RoomDatabase() { //class to hold the database of meal presets
    abstract fun mealDao(): MealPresetDao
}