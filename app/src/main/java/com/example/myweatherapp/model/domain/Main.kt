package com.example.myweatherapp.model.domain

import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("date")
    val date: String,
    @SerializedName("hourly")
    val hourly: List<Hourly>,
)

