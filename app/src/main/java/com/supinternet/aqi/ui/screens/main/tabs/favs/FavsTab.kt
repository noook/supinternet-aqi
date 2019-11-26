package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
        val table = mutableListOf<String>()
        super.onViewCreated(view, savedInstanceState)

        // Fausse données
        var data = arrayOf(Fav("Paris",5722), Fav("Lyon",3028), Fav("Toulouse",3836))

        GlobalScope.launch(Dispatchers.Default) {


            for(station in data) {

                val res =
                    com.supinternet.aqi.data.network.AQIAPI.getInstance().textSearchAsync(
                        ,
                        "7ed2ade1e4f2bcf13b203614959658c2d944f131"
                    ).await()

                table.add("res")
            }


            withContext(Dispatchers.Main) {
                //textview.text := res.data
            Log.v("HELLO",res.data.toString())

            }

        }



        //Log.v("abcde",res.data.toString())


    }


}