package com.bignerdranch.android.macrotracker.database

import androidx.room.TypeConverter
import java.util.*

class TypeConversion {

    @TypeConverter //convert string to UUID for use in the application
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter //convert UUID to string for storage in the database
    fun fromUUID(uuid: UUID?): String?{
        return uuid?.toString()
    }
}