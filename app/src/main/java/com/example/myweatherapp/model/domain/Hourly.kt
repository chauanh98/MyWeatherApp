package com.example.myweatherapp.model.domain

import com.google.gson.annotations.SerializedName

data class Hourly(
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("mid")
    val mid: List<Mid>,
    @SerializedName("time")
    val time: Int,
)