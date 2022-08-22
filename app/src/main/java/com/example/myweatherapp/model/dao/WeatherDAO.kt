package com.example.myweatherapp.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweatherapp.model.domain.Weather

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weather")
    fun getWeather(): LiveData<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Query("SELECT * FROM weather ORDER BY idWeather DESC LIMIT 10")
    fun getCityRecent(): LiveData<Weather>
}