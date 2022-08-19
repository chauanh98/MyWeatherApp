package com.example.myweatherapp.utils

import androidx.room.TypeConverter
import com.example.myweatherapp.model.domain.Main
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun listToJSON(value: List<Main>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Main>::class.java).toList()
}