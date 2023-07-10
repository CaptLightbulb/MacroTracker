package com.bignerdranch.android.macrotracker

import android.app.Application

class MacroTrackerApp: Application() {
    override fun onCreate() { //create a singleton instance of the meal preset database repository
        super.onCreate()
        MealPresetRepo.build(this)
    }
}