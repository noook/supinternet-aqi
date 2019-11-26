package com.supinternet.aqi.ui.screens.main.tabs.travel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.supinternet.aqi.R
import kotlinx.android.synthetic.main.travel_tab_item_cell.view.*

class CityAdapter(val cities: List<CityPollution>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CityItemCell(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.travel_tab_item_cell, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CityItemCell).bindCity(cities[position])
    }
}

class CityItemCell(v: View) : RecyclerView.ViewHolder(v) {
    //private val flag: ImageView = v.country_flag
    private val name: TextView = v.city_name
    private val flag: ImageView = v.city_flag
    private val pollution: TextView = v.pollution_value

    fun bindCity(city: CityPollution) {
        name.text = city.name
        flag.setImageResource(R.drawable.ic_flag_country_in)
        pollution.text = city.pollution.toString()
    }
}