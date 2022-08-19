package com.example.myweatherapp.model.repository

import androidx.lifecycle.LiveData
import com.example.myweatherapp.model.dao.WeatherDAO
import com.example.myweatherapp.model.domain.Weather

class WeatherRepository private constructor(private val _weatherDAO: WeatherDAO) {

    fun getWeather(): LiveData<Weather> = _weatherDAO.getWeather()

    suspend fun insertWeather(weather: Weather) {
        _weatherDAO.insertWeather(weather)
    }

    companion object {
        fun create(weatherDAO: WeatherDAO): WeatherRepository {
            return WeatherRepository(weatherDAO)
        }
    }
}