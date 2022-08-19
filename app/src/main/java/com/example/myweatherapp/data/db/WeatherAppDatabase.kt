package com.example.myweatherapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweatherapp.model.dao.WeatherDAO
import com.example.myweatherapp.model.domain.Weather
import com.example.myweatherapp.utils.Converters

@Database(entities = [Weather::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherAppDatabase : RoomDatabase() {

    abstract fun getWeatherDAO(): WeatherDAO

    companion object {
        @Volatile
        private var INSTANCE: WeatherAppDatabase? = null

        fun getInstance(context: Context): WeatherAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherAppDatabase::class.java,
                    "MyWeatherApp"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}