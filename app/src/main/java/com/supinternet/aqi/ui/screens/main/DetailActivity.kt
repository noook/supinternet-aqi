package com.supinternet.aqi.ui.screens.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.RankingAPI
import com.supinternet.aqi.data.network.model.history.History
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Bubble(val value: Double, val unit: String, val indice: String)

class DetailActivity : AppCompatActivity() {
    var history: List<History> = listOf()
    var chartView: AnyChartView? = null
    var id: String? = null
    var favorites: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.detail_activity)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")
        this.id = id
        val airQuality = intent.getStringExtra("air_quality")
        val quality = findViewById<TextView>(R.id.air_quality)
        quality.text = getString(R.string.air_quality, airQuality)

        this.chartView = detail_activity_column_chart
        val infos = findViewById<TextView>(R.id.infos)
        infos.text = "$name $id"
        val self = this
        detail_Activity_button_aqi.tag = "aqi"
        detail_Activity_button_pm2_5.tag = "pm2_5"
        detail_Activity_button_pm10.tag = "pm10"
        detail_Activity_button_no2.tag = "no2"

        val buttons: List<TextView> = listOf(
            detail_Activity_button_aqi,
            detail_Activity_button_pm2_5,
            detail_Activity_button_pm10,
            detail_Activity_button_no2
        )

        buttons.forEach {
            val button = it
            it.setOnClickListener {
                buttons.forEach {
                    it.setTextColor(resources.getColor(R.color.button_background))
                    it.background = getDrawable(R.drawable.border)
                }
                button.setTextColor(resources.getColor(R.color.white))
                button.background = getDrawable(R.color.button_background)
                initChartWithTag(button.tag as String)
            }
        }
        this.loadFavorites()


        detail_activity_star_empty.setOnClickListener {
            this.addFavorite(id)
        }

        detail_activity_star_filled.setOnClickListener {
            this.removeFavorite(id)
        }

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
                initChart(Dataset.USAQI)
                detail_activity_buttons_container.visibility = View.VISIBLE
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
            bubblesViews[index].findViewById<TextView>(R.id.bubble_value).text =
                bubble.value.toString()
            bubblesViews[index].findViewById<TextView>(R.id.bubble_unit).text = bubble.unit
        }
    }

    enum class Dataset {
        PM10, PM2_5, NO2, USAQI
    }

    fun loadFavorites() {
        val sharedPref = this.getSharedPreferences(getString(R.string.saved_favorites), MODE_PRIVATE) ?: return
        val favorites = sharedPref.getStringSet(getString(R.string.saved_favorites), setOf())
        this.favorites = favorites!!
        this.displayStarButton()
    }

    fun saveFavorites() {
        val sharedPref = this.getSharedPreferences(getString(R.string.saved_favorites), MODE_PRIVATE) ?: return
        val self = this
        with(sharedPref.edit()) {
            putStringSet(getString(R.string.saved_favorites), self.favorites)
            commit()
        }
        this.displayStarButton()
    }

    fun displayStarButton() {
        val isFavorite = this.favorites.contains(this.id)
        detail_activity_star_filled.visibility = when (isFavorite) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        detail_activity_star_empty.visibility = when (isFavorite) {
            false -> View.VISIBLE
            true -> View.GONE
        }
    }

    fun addFavorite(id: String) {
        val set = this.favorites.toMutableSet()
        set.add(id)
        this.favorites = set
        this.saveFavorites()
    }

    fun removeFavorite(id: String) {
        val set = this.favorites.toMutableSet()
        set.remove(id)
        this.favorites = set
        this.saveFavorites()
    }

    fun initChartWithTag(tag: String) {
        val dataset: Dataset = when (tag) {
            "aqi" -> Dataset.USAQI
            "pm2_5" -> Dataset.PM2_5
            "pm10" -> Dataset.PM10
            "no2" -> Dataset.NO2
            else -> Dataset.USAQI
        }
        this.initChart(dataset, true)
    }

    fun getChartDataset(set: List<History>, field: Dataset): List<Number> {
        return when (field) {
            Dataset.NO2 -> set.map {
                it.data.no2.v
            }
            Dataset.PM10 -> set.map {
                it.data.pm10.v
            }
            Dataset.PM2_5 -> set.map {
                it.data.pm25.v
            }
            Dataset.USAQI -> set.map {
                it.data.pm25.v // p field crashes sometimes, don't forget to change the field
            }
        }
    }

    fun initChart(withField: Dataset, recreate: Boolean = false) {
        if (recreate == true) {
            this.chartView!!.clear()
        }
        val chart: Cartesian = AnyChart.column()
        // chart.xScale(DateTime.instantiate());
        var iterator = 0
        val data: List<DataEntry> = this.getChartDataset(this.history.takeLast(20), withField).map {
            iterator++
            ValueDataEntry(iterator, it)
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
        this.chartView!!.setChart(chart)
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