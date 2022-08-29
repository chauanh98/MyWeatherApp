package com.example.myweatherapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myweatherapp.data.db.WeatherAppDatabase
import com.example.myweatherapp.getOrAwaitValue
import com.example.myweatherapp.model.domain.*
import com.example.myweatherapp.model.repository.WeatherRepository
import com.google.common.truth.Truth
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest : TestCase() {
    private lateinit var repository: WeatherRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    public override fun setUp() {
        super.setUp()
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherAppDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = WeatherRepository(db.getWeatherDAO())
    }

    @Test
    fun getWeather() : Unit = runBlocking {
        val weather = Weather(
            1, "Moscow", listOf(
                Main(
                    "1-1-2022", listOf(
                        Hourly(
                            12, listOf(
                                Mid(
                                    12, 12, 12,
                                    listOf(WeatherDesc("cdata")),
                                    listOf(WeatherIconUrl("cdata"))
                                )
                            ), 100
                        )
                    )
                )
            )
        )
        repository.insertWeather(weather)
        val result = repository.getWeather().getOrAwaitValue()
        Truth.assertThat(result).isEqualTo(weather)
    }
}