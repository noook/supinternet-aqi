package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.AQIAPI
import com.supinternet.aqi.data.network.model.map.MapSearch
import com.supinternet.aqi.data.network.model.search.TextSearch
import com.supinternet.aqi.data.network.model.station.Data
import kotlinx.android.synthetic.main.fragment_favs.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_maps_station_card.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

data class Fav(val stationName : String,  val stationId: Int)

class FavsTab : Fragment() {

    private lateinit var map: GoogleMap
    private var ignoreNextMove = false
    private val markers = mutableMapOf<Marker, Any>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val table = mutableListOf<Data>()
        super.onViewCreated(view, savedInstanceState)

        var data = arrayOf(Fav("Paris",5722), Fav("Lyon",3028), Fav("Toulouse",3836))

        GlobalScope.launch(Dispatchers.Default) {


            for(station in data) {

                val res =
                    com.supinternet.aqi.data.network.AQIAPI.getInstance().currentStationData(
                        station.stationId,
                        "7ed2ade1e4f2bcf13b203614959658c2d944f131"
                    ).await()

                table.add(res.data)
            }


            withContext(Dispatchers.Main) {
                Log.v("HELLO",table.toString())
                lists.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = ListAdapter(table)
                    determinateBar?.visibility = View.GONE
                }
            }
        }
    }
}