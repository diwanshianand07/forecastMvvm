package com.example.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.FutureWeatherDao
import com.example.forecastmvvm.data.db.WeatherLocationDao
import com.example.forecastmvvm.data.db.entity.WeatherLocation
import com.example.forecastmvvm.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.example.forecastmvvm.data.db.unitlocalized.future.UnitSpecificSimpleFutureWeatherEntry
import com.example.forecastmvvm.data.network.FORECAST_DAYS_COUNT
import com.example.forecastmvvm.data.network.WeatherNetworkDataSource
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.forecastmvvm.data.network.response.FutureWeatherResponse
import com.example.forecastmvvm.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao : WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val futureWeatherDao: FutureWeatherDao,
    private val locationProvider : LocationProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }
    override suspend fun getcurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
       return withContext(Dispatchers.IO) {
           initWeatherData()
           return@withContext if(metric) currentWeatherDao.getWeatherMetric()
           else currentWeatherDao.getWeatherImperial()
       }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {

        fun deleteOldForecastData() {
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
            futureWeatherDao.insert(futureWeatherList)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
            else futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.current)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }
    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if(lastWeatherLocation == null
            || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            return
        }
       if(isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)) {
           fetchCurrentWeather()
       }
    }
    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString()
        )
    }

    private suspend fun fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString()
        )
    }


    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean{
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }
}

//class ForecastRepositoryImpl(
//    private val currentWeatherDao: CurrentWeatherDao,
//    private val futureWeatherDao: FutureWeatherDao,
//    private val weatherLocationDao: WeatherLocationDao,
//    private val weatherNetworkDataSource: WeatherNetworkDataSource,
//    private val locationProvider: LocationProvider
//) : ForecastRepository {
//
//    init {
//        weatherNetworkDataSource.apply {
//            downloadedCurrentWeather.observeForever { newCurrentWeather ->
//                persistFetchedCurrentWeather(newCurrentWeather)
//            }
//            downloadedFutureWeather.observeForever { newFutureWeather ->
//                persistFetchedFutureWeather(newFutureWeather)
//            }
//        }
//    }
//
//    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
//        return withContext(Dispatchers.IO) {
//            initWeatherData()
//            return@withContext if (metric) currentWeatherDao.getWeatherMetric()
//            else currentWeatherDao.getWeatherImperial()
//        }
//    }
//
//    override suspend fun getFutureWeatherList(
//        startDate: LocalDate,
//        metric: Boolean
//    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
//        return withContext(Dispatchers.IO) {
//            initWeatherData()
//            return@withContext if (metric) futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
//            else futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
//        }
//    }
//
//    override suspend fun getFutureWeatherByDate(
//        date: LocalDate,
//        metric: Boolean
//    ): LiveData<out UnitSpecificDetailFutureWeatherEntry> {
//        return withContext(Dispatchers.IO) {
//            initWeatherData()
//            return@withContext if (metric) futureWeatherDao.getDetailedWeatherByDateMetric(date)
//            else futureWeatherDao.getDetailedWeatherByDateImperial(date)
//        }
//    }
//
//    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
//        return withContext(Dispatchers.IO) {
//            return@withContext weatherLocationDao.getLocation()
//        }
//    }
//
//    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
//        GlobalScope.launch(Dispatchers.IO) {
//            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
//            weatherLocationDao.upsert(fetchedWeather.location)
//        }
//    }
//
//    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {
//
//        fun deleteOldForecastData() {
//            val today = LocalDate.now()
//            futureWeatherDao.deleteOldEntries(today)
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//            deleteOldForecastData()
//            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
//            futureWeatherDao.insert(futureWeatherList)
//            weatherLocationDao.upsert(fetchedWeather.location)
//        }
//    }
//
//    private suspend fun initWeatherData() {
//        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
//
//        if (lastWeatherLocation == null
//            || locationProvider.hasLocationChanged(lastWeatherLocation)) {
//            fetchCurrentWeather()
//            fetchFutureWeather()
//            return
//        }
//
//        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
//            fetchCurrentWeather()
//
//        if (isFetchFutureNeeded())
//            fetchFutureWeather()
//    }
//
//    private suspend fun fetchCurrentWeather() {
//        weatherNetworkDataSource.fetchCurrentWeather(
//            locationProvider.getPreferredLocationString(),
//            Locale.getDefault().language
//        )
//    }
//
//    private suspend fun fetchFutureWeather() {
//        weatherNetworkDataSource.fetchFutureWeather(
//            locationProvider.getPreferredLocationString(),
//            Locale.getDefault().language
//        )
//    }
//
//    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
//        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
//        return lastFetchTime.isBefore(thirtyMinutesAgo)
//    }
//
//    private fun isFetchFutureNeeded(): Boolean {
//        val today = LocalDate.now()
//        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
//        return futureWeatherCount < FORECAST_DAYS_COUNT
//    }
//}

