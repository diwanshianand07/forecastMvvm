package com.example.forecastmvvm.data.provider

import com.example.forecastmvvm.Internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}