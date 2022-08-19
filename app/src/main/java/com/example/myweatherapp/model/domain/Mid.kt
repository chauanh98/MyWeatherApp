package com.example.myweatherapp.model.domain

import com.google.gson.annotations.SerializedName

data class Mid(
    @SerializedName("tempC")
    val tempC: Int,
    @SerializedName("tempF")
    val tempF: Int,
    @SerializedName("weatherCode")
    val weatherCode: Int,
    @SerializedName("weatherDesc")
    val weatherDesc: List<WeatherDesc>,
    @SerializedName("weatherIconUrl")
    val weatherIconUrl: List<WeatherIconUrl>,
)