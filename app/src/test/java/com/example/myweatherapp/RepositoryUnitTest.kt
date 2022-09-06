package com.example.myweatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.myweatherapp.model.domain.*
import com.example.myweatherapp.model.repository.WeatherRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RepositoryUnitTest {

    private lateinit var repo: WeatherRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        repo = Mockito.mock(WeatherRepository::class.java)
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
        Mockito.`when`(repo.getWeather()).thenReturn(weathers)
        val getWeather = repo.getWeather().getOrAwaitValue()
        Assert.assertNotNull(getWeather)
        Assert.assertEquals(weather, getWeather)
        Mockito.verify(repo).getWeather()
    }

    @Test
    fun getListWeather() {
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
        val listWeather = MutableLiveData<List<Weather>>()
        listWeather.value = listOf(weather1, weather)
        Mockito.`when`(repo.getCityRecent()).thenReturn(listWeather)
        val getWeathers = repo.getCityRecent().getOrAwaitValue()
        Assert.assertNotNull(getWeathers)
        Assert.assertEquals(2, getWeathers.size)
        Mockito.verify(repo).getCityRecent()
    }
}