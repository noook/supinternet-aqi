package com.supinternet.aqi.ui.screens.main.tabs.travel

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.RankingAPI
import kotlinx.android.synthetic.main.travel_tab_recycler_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val rankings = withContext(Dispatchers.IO) {
                    RankingAPI.getInstance().getRanking().await()
                }
                val sorted = rankings.cities.sortedByDescending {
                    it.station.a.toInt()
                }

                /* val id = Settings.Global.getString(
                    resources.getIdentifier(
                        "country_${city.country}_name",
                        "string",
                        requireContext().packageName
                    )
                )*/

                travel_tab_recycler_view.layoutManager = LinearLayoutManager(requireContext())
                travel_tab_recycler_view.adapter = CityAdapter(sorted, requireContext())
            } catch (e: Exception) {
                print(e)
            }
        }
    }
}
