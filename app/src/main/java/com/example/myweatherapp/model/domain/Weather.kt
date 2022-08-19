package com.example.myweatherapp.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Weather(
    @PrimaryKey
    val idWeather: Long = 1,
    @SerializedName("weather")
    val weather: List<Main>
)