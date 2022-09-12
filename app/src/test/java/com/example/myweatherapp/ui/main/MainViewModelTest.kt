package com.example.myweatherapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.myweatherapp.getOrAwaitValue
import com.example.myweatherapp.model.domain.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Mock
    private lateinit var viewModel: MainViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
        val isViewLoading = MutableLiveData<Boolean>()
        val anErrorOccurred = MutableLiveData<Boolean>()

        weathers.value = weather
        isViewLoading.value = false
        anErrorOccurred.value = false

        Mockito.`when`(viewModel.mWeather).thenReturn(weathers)
        Mockito.`when`(viewModel.shouldLoading).thenReturn(isViewLoading)
        Mockito.`when`(viewModel.shouldError).thenReturn(anErrorOccurred)

        val getWeather = viewModel.mWeather.getOrAwaitValue()
        val getLoading = viewModel.shouldLoading.getOrAwaitValue()
        val getError = viewModel.shouldError.getOrAwaitValue()

        Assert.assertNotNull(getWeather)
        Assert.assertEquals(weather, getWeather)
        Assert.assertEquals(false, getLoading)
        Assert.assertEquals(false, getError)

        Mockito.verify(viewModel).mWeather
        Mockito.verify(viewModel).shouldLoading
        Mockito.verify(viewModel).shouldError
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