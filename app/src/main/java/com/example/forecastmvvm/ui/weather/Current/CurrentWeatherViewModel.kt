package com.example.forecastmvvm.ui.weather.Current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.Internal.UnitSystem
import com.example.forecastmvvm.Internal.lazyDeferred
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.data.repository.ForecastRepository
import com.example.forecastmvvm.ui.base.WeatherViewModel

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred{
        forecastRepository.getcurrentWeather(super.isMetricUnit)
    }

}