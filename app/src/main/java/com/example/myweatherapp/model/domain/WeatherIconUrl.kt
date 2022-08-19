package com.example.myweatherapp.model.domain

import com.google.gson.annotations.SerializedName

data class WeatherIconUrl(
    @SerializedName("value")
    val cdata: String
)