package com.example.forecastmvvm.ui.weather.Current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.Internal.UnitSystem
import com.example.forecastmvvm.Internal.lazyDeferred
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.data.repository.ForecastRepository

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {
    private val unitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.IMPERIAL
    val weather by lazyDeferred{
        forecastRepository.getcurrentWeather(isMetric)
    }
}