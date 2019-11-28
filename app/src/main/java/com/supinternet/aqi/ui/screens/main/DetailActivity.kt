package com.supinternet.aqi.ui.screens.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import android.util.Log
import com.supinternet.aqi.data.network.RankingAPI
import android.view.View
import kotlinx.android.synthetic.main.station_details_bubble.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Bubble(val value: Double, val unit: String, val indice: String)

class  DetailActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.detail_activity)


        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")
        val airQuality = intent.getStringExtra("air_quality")
        val quality = findViewById<TextView>(R.id.air_quality)
        quality.text = getString(R.string.air_quality, airQuality)


        val infos = findViewById<TextView>(R.id.infos)
        infos.text = "$name $id"

        GlobalScope.launch {
            try {
                // Ici ce sont les data que nous retourne l'Api,
                // il faut les utiliser pour le graph  pour plus de détails regarder
                // History.kt dans les models ou demandez moi (c'est Nico)

                val res = RankingAPI.Factory.HistoryApi.getInstance().getHistory().await()
                Log.v("yoo",res.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
            }
        }

        val bubblesData = listOf<Bubble>(
            Bubble(
                18.19,
                "ug/m3",
                "PM2.5"
            ),
            Bubble(
                170.75,
                "ppb",
                "CO"
            ),
            Bubble(
                17.36,
                "ppb",
                "NO2"
            ),
            Bubble(
                0.25,
                "ppb",
                "SO2"
            ),
            Bubble(
                16.48,
                "ppb",
                "O3"
            ),
            Bubble(
                28.08,
                "ug/m3",
                "PM10"
            )
        )

        var bubblesViews = listOf<View>(
            findViewById(R.id.b1),
            findViewById(R.id.b2),
            findViewById(R.id.b3),
            findViewById(R.id.b4),
            findViewById(R.id.b5),
            findViewById(R.id.b6)
        )

        for ((index, bubble) in bubblesData.withIndex()) {
            bubblesViews[index].findViewById<TextView>(R.id.bubble_indice).text = bubble.indice
            bubblesViews[index].findViewById<TextView>(R.id.bubble_value).text = bubble.value.toString()
            bubblesViews[index].findViewById<TextView>(R.id.bubble_unit).text = bubble.unit
        }
    }
}