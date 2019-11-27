package com.supinternet.aqi.ui.screens.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.HistoryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

                //val res = HistoryApi.getInstance().getHistory().await()


            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
            }
        }

    }
}