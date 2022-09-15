package com.example.forecastmvvm.ui.weather.Future.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forecastmvvm.R
import com.example.forecastmvvm.data.db.unitlocalized.future.UnitSpecificSimpleFutureWeatherEntry
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.item_future_weather.view.*

class GroupAdapter(private var itemList: List<UnitSpecificSimpleFutureWeatherEntry>, var context : Context):
    RecyclerView.Adapter<GroupAdapter.MyViewHolder>(){

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var date = view.textView_date
        var temperature = view.textView_temperature
        var condition = view.textView_condition
        var icon = view.imageView_condition_icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_future_weather, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var weatherEntry = itemList[position]
        holder.condition.text = weatherEntry.conditionText
        holder.temperature.text = weatherEntry.avgTemperature.toString()
        holder.date.text = weatherEntry.date.toString()
        Glide.with(context)
            .load("http:${weatherEntry.conditionIconUrl}")
            .into(holder.icon)
    }

    override fun getItemCount(): Int {
       return itemList.size
    }

}