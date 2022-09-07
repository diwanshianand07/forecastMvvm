package com.example.forecastmvvm.ui.weather.Current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.Internal.UnitSystem
import com.example.forecastmvvm.Internal.lazyDeferred
import com.example.forecastmvvm.data.repository.ForecastRepository

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    private val unitSystem = UnitSystem.METRIC//get from settings later
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC
    val weather by lazyDeferred{
        forecastRepository.getcurrentWeather(isMetric)
    }
}