package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.model.station.Data
import kotlinx.android.synthetic.main.fragment_favs.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Fav(val stationName: String, val stationId: Int)

class FavsTab : Fragment() {

    var favorites = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favs, container, false)
    }

    fun loadFavorites() {
        val sharedPref = this.activity!!.getSharedPreferences(
            getString(R.string.saved_favorites),
            AppCompatActivity.MODE_PRIVATE
        ) ?: return
        val favorites = sharedPref.getStringSet(getString(R.string.saved_favorites), setOf())
        this.favorites = favorites!!.toList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val arrrayResult = mutableListOf<Data>()
        super.onViewCreated(view, savedInstanceState)

        loadFavorites()
        var data = this.favorites.map {
            Fav("", it.toInt())
        }.toTypedArray()

        GlobalScope.launch(Dispatchers.Default) {


            for (station in data) {

                val res =
                    com.supinternet.aqi.data.network.AQIAPI.getInstance().currentStationData(
                        station.stationId,
                        "7ed2ade1e4f2bcf13b203614959658c2d944f131"
                    ).await()

                arrrayResult.add(res.data)
            }


            withContext(Dispatchers.Main) {
                lists.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = ListAdapter(arrrayResult)
                    waitingBar?.visibility = View.GONE
                }
            }
        }
    }
}