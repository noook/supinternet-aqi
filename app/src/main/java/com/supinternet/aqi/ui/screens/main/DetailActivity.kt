package com.supinternet.aqi.ui.screens.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.scales.DateTime
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.RankingAPI
import com.supinternet.aqi.data.network.model.history.History
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailActivity : AppCompatActivity() {
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
        // chart.xScale(DateTime.instantiate());
        var iterator = 0
        val data: List<DataEntry> = this.history.takeLast(20).map {
            iterator++
            //ValueDataEntry(it.time.toString(), it.data.pm10.v)
            ValueDataEntry(iterator, it.data.pm10.v)
        }
        val column: Column = chart.column(data)

        val function = "function () {\n" +
                "  var val = this.value;\n" +
                "  switch (true) {\n" +
                "    case val <= 50: return '${getColumnColor(45)}'\n" +
                "    case val <= 100: return '${getColumnColor(95)}'\n" +
                "    case val <= 150: return '${getColumnColor(145)}'\n" +
                "    case val <= 200: return '${getColumnColor(195)}'\n" +
                "    case val <= 300: return '${getColumnColor(295)}'\n" +
                "    default: return '${getColumnColor(305)}'\n" +
                "  }\n" +
                "}"

        column.fill(function)

        column.stroke("rgba(0, 0, 0, 0")

        chart.animation(true)
        chartView.setChart(chart)
    }

    fun getColumnColor(value: Int): String {
        val id = when (value) {
            in 0..50 -> "good"
            in 51..100 -> "moderate"
            in 101..150 -> "unhealthy_sensitive"
            in 151..200 -> "unhealthy"
            in 201..300 -> "very_unhealthy"
            else -> "hazardous"
        }
        val color = getString(
            resources.getIdentifier(
                "aqi_${id}",
                "color",
                this.applicationContext.packageName
            )
        )
        return "#${color.takeLast(6)}"

    }
}