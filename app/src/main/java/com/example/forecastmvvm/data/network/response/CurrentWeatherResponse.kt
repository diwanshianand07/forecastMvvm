package com.example.forecastmvvm.data.network.response

import com.example.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.example.forecastmvvm.data.db.entity.Location

data class CurrentWeatherResponse(
    val current: CurrentWeatherEntry,
    val location: Location
)