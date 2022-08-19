package com.example.myweatherapp.model.domain

import com.google.gson.annotations.SerializedName

data class WeatherDesc(
    @SerializedName("value")
    val cdata: String
)