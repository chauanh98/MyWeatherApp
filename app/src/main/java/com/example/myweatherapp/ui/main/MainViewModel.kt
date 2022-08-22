package com.example.myweatherapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myweatherapp.data.db.WeatherAppDatabase
import com.example.myweatherapp.model.domain.Weather
import com.example.myweatherapp.model.repository.WeatherRepository
import com.example.myweatherapp.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _weatherRepository: WeatherRepository
    var mWeather: LiveData<Weather>
    var mCity: LiveData<Weather>
    var isViewLoading = MutableLiveData<Boolean>()
    var anErrorOccurred = MutableLiveData<Boolean>()

    init {
        val weatherDB = WeatherAppDatabase.getInstance(application)
        _weatherRepository = WeatherRepository.create(weatherDB.getWeatherDAO())
        mWeather = _weatherRepository.getWeather()
        mCity = _weatherRepository.getCityRecent()
    }

    fun getWeather(lat: Double, lng: Double, cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isViewLoading.postValue(true)
            val params: HashMap<String?, String?> = HashMap()
            params["key"] = Constants.API.API_KEY
            params["q"] = "$lat,$lng"
            params["format"] = "json"

            val jsonStringHolder = performGetCall(Constants.API.BASE_URL, params)

            val obj = JSONObject(jsonStringHolder.toString())
            val array = obj.getJSONObject("data")

            val obj1 = JSONObject(array.toString())
            val array1 = obj1.getJSONArray("weather")

            val weatherObject = JSONObject()
            weatherObject.put("weather", array1)

            val response =
                Gson().fromJson(weatherObject.toString(), Weather::class.java)

            Log.i("chau", response.toString())

            response?.let {
                isViewLoading.postValue(false)
                anErrorOccurred.postValue(false)
                it.cityName = cityName
                insertWeather(it)
            } ?: isViewLoading.postValue(true)
        }
    }

    private fun insertWeather(weather: Weather) {
        viewModelScope.launch {
            _weatherRepository.insertWeather(weather)
            mWeather = _weatherRepository.getWeather()
        }
    }

    private fun performGetCall(
        requestURL: String?,
        postDataParams: HashMap<String?, String?>?
    ): String? {
        val url: URL
        var response: String? = ""
        try {
            url = URL(requestURL)
            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.doOutput = true
            val os: OutputStream = conn.outputStream
            val writer = BufferedWriter(
                OutputStreamWriter(os, "UTF-8")
            )
            writer.write(getParamDataString(postDataParams))
            writer.flush()
            writer.close()
            os.close()
            val responseCode = conn.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                var line: String?
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                while (br.readLine().also { line = it } != null) {
                    response += line
                }
            } else {
                response = ""
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
        return response
    }

    private fun getParamDataString(params: HashMap<String?, String?>?): String {
        val result = java.lang.StringBuilder()
        var first = true
        for (map in params!!.entries) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(map.key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(map.value, "UTF-8"))
        }
        return result.toString()
    }
}