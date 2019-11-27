package com.supinternet.aqi.ui.screens.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.R
import android.util.Log
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.supinternet.aqi.data.network.RankingAPI
import com.supinternet.aqi.data.network.model.history.History
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.coroutines.*


class  DetailActivity: AppCompatActivity(){
    var history: List<History> = listOf()

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
        val self = this

        GlobalScope.launch {
            try {
                // Ici ce sont les data que nous retourne l'Api,
                // il faut les utiliser pour le graph  pour plus de détails regarder
                // History.kt dans les models ou demandez moi (c'est Nico)

                val history = withContext(Dispatchers.Main) {
                    RankingAPI.Factory.HistoryApi.getInstance().getHistory().await()
                }
                self.history = history
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                initChart()
            }
        }


    }

    fun initChart() {
        val chartView: AnyChartView = detail_activity_column_chart
        val chart: Cartesian = AnyChart.column()
        var iterator = 0
        val data: List<DataEntry> = this.history.map {it
            iterator++
            ValueDataEntry(iterator, it.data.pm10.v)
        }
        val column: Column = chart.column(data)
        chart.animation(true)
        chartView.setChart(chart)
    }
}