package com.example.forecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecastmvvm.Internal.NoConnectivityException
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse

class WeatherNetworkDataSourceImpl(
    private val apixuWeatherApiService: ApixuWeatherApiService
) : WeatherNetworkDataSource {
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather
    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    override suspend fun fetchCurrentWeather(location: String) {
        try{
            val fetchedCurrentWeather =  apixuWeatherApiService
                .getCurrentWeather(location)
                .await()
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        }
        catch(e: NoConnectivityException){
            Log.e("Connectivity", "No Internet Connection")
        }
    }
}