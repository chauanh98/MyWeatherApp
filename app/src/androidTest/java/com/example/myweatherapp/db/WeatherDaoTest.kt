package com.example.myweatherapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myweatherapp.data.db.WeatherAppDatabase
import com.example.myweatherapp.getOrAwaitValue
import com.example.myweatherapp.model.dao.WeatherDAO
import com.example.myweatherapp.model.domain.*
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dao: WeatherDAO
    private lateinit var database: WeatherAppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherAppDatabase::class.java
        ).build()
        dao = database.getWeatherDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveWeather(): Unit = runBlocking {
        val weather = Weather(
            1, "Moscow", listOf(
                Main(
                    "1-1-2022", listOf(
                        Hourly(
                            12, listOf(
                                Mid(
                                    12, 12, 12, listOf(WeatherDesc("cdata")),
                                    listOf(WeatherIconUrl("cdata"))
                                )
                            ), 100
                        )
                    )
                )
            )
        )
        dao.insertWeather(weather)

        val getWeather = dao.getWeather().getOrAwaitValue()
        Truth.assertThat(getWeather).isEqualTo(weather)
    }

    @Test
    fun saveWeathersList(): Unit = runBlocking {
        val weather = Weather(
            2, "Paris", listOf(
                Main(
                    "1-1-2022", listOf(
                        Hourly(
                            12, listOf(
                                Mid(
                                    12, 12, 12, listOf(WeatherDesc("cdata")),
                                    listOf(WeatherIconUrl("cdata"))
                                )
                            ), 100
                        )
                    )
                )
            )
        )
        val weather1 = Weather(
            1, "Moscow", listOf(
                Main(
                    "1-1-2022", listOf(
                        Hourly(
                            12, listOf(
                                Mid(
                                    12, 12, 12, listOf(WeatherDesc("cdata")),
                                    listOf(WeatherIconUrl("cdata"))
                                )
                            ), 100
                        )
                    )
                )
            )
        )
        dao.insertWeather(weather)
        dao.insertWeather(weather1)

        val getWeather = dao.getCityRecent().getOrAwaitValue()
        Truth.assertThat(getWeather).isEqualTo(listOf(weather, weather1))
    }
}