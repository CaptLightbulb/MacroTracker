package com.bignerdranch.android.macrotracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class MealPreset(@PrimaryKey val id:UUID = UUID.randomUUID(), //create definition for the meal preset objects
                      var name: String = "",
                      var calories: Int = 0,
                      var gramsFat: Int = 0,
                      var gramsCarbs: Int = 0,
                      var gramsProtein: Int = 0) {
}