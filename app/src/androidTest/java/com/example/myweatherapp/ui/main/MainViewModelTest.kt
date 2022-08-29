package com.example.myweatherapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myweatherapp.getOrAwaitValue
import com.example.myweatherapp.model.domain.*
import com.google.common.truth.Truth
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelTest : TestCase() {
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    public override fun setUp() {
        super.setUp()
        mainViewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun getWeather(): Unit = runBlocking {
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
        mainViewModel.insertWeather(weather)
        val result = mainViewModel.mWeather.getOrAwaitValue()

        Truth.assertThat(result.cityName).isEqualTo(weather.cityName)
    }

    @Test
    fun getCitiesList(): Unit = runBlocking {
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

        mainViewModel.insertWeather(weather)

        val result = mainViewModel.mCity.getOrAwaitValue().find {
            it.cityName == "Moscow" && it.idWeather.toInt() == 1
        }
        Truth.assertThat(result).isEqualTo(weather)
    }
}