package com.example.forecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.forecastmvvm.data.network.response.FutureWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedFutureWeather: LiveData<FutureWeatherResponse>
    suspend fun fetchCurrentWeather(
        location : String
    )
    suspend fun fetchFutureWeather(
        location : String
    )
}