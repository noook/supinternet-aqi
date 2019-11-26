package com.supinternet.aqi.ui.screens.main.tabs.travel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supinternet.aqi.R
import kotlinx.android.synthetic.main.travel_tab_recycler_view.*

class TravelTab : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.travel_tab_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cities = listOf<CityPollution>(
            CityPollution(name = "Paris", pollution = 54),
            CityPollution(name = "Brest", pollution = 10),
            CityPollution(name = "Amsterdam", pollution = 22),
            CityPollution(name = "New York", pollution = 112),
            CityPollution(name = "Berlin", pollution = 12)
        )

        travel_tab_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        travel_tab_recycler_view.adapter = CityAdapter(cities)
    }
}
