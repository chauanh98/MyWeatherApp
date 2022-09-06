package com.example.myweatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.myweatherapp.model.domain.*
import com.example.myweatherapp.ui.main.MainViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainViewModelUnitTest {

    private lateinit var viewModel: MainViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = Mockito.mock(MainViewModel::class.java)
    }

    @Test
    fun getWeather() {
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
        val weathers = MutableLiveData<Weather>()
        weathers.value = weather
        Mockito.`when`(viewModel.mWeather).thenReturn(weathers)
        val getWeather = viewModel.mWeather.getOrAwaitValue()
        Assert.assertNotNull(getWeather)
        Assert.assertEquals(weather, getWeather)
        Mockito.verify(viewModel).mWeather
    }

    @Test
    fun getListCity() {
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
        val weather1 = Weather(
            2, "Paris", listOf(
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
        val listCities = MutableLiveData<List<Weather>>()
        listCities.value = listOf(weather1, weather)
        Mockito.`when`(viewModel.mCity).thenReturn(listCities)
        val getWeathers = viewModel.mCity.getOrAwaitValue()
        Assert.assertNotNull(getWeathers)
        Assert.assertEquals(2, getWeathers.size)
        Mockito.verify(viewModel).mCity
    }
}