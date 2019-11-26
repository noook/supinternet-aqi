package com.supinternet.aqi.ui.screens.main.tabs.aroundme

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.AQIAPI
import com.supinternet.aqi.data.network.model.map.MapSearchData
import com.supinternet.aqi.data.network.model.search.TextSearchData
import com.supinternet.aqi.ui.screens.main.DetailActivity
import com.supinternet.aqi.ui.utils.GoogleMapUtils
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_maps_search_card.*
import kotlinx.android.synthetic.main.fragment_maps_station_card.*
import kotlinx.android.synthetic.main.fragment_maps_station_card.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class MapsTab : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var ignoreNextMove = false
    private val markers = mutableMapOf<Marker, Any>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.maps_tab_gmap) as SupportMapFragment).getMapAsync(
            this
        )

        maps_tab_search_zone_button.setOnClickListener {
            showLoading()

            GlobalScope.launch(Dispatchers.Main) {

                val location = map.projection.visibleRegion.latLngBounds

                try {
                    val res = withContext(Dispatchers.IO) {
                        AQIAPI.getInstance().searchInMapAsync(
                            "${location.northeast.latitude},${location.northeast.longitude},${location.southwest.latitude},${location.southwest.longitude}",
                            "7ed2ade1e4f2bcf13b203614959658c2d944f131"
                        ).await()
                    }

                    addMarkers(res.data)

                } catch (e: Exception) {
                    e.printStackTrace()

                    AlertDialog.Builder(requireActivity())
                        .setTitle(getString(R.string.maps_tab_zone_error_title))
                        .setMessage(getString(R.string.maps_tab_zone_error_message))
                        .setPositiveButton("Ok", null)
                        .show()
                }

                hideLoading()
            }
        }

        maps_tab_search_field_arrow.setOnClickListener {

            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            maps_tab_search_field_arrow.requestFocus()

            val text = maps_tab_search_field.editableText.toString()

            if (text.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading()

                    try {
                        val res = withContext(Dispatchers.IO) {
                            AQIAPI.getInstance().textSearchAsync(
                                text,
                                "7ed2ade1e4f2bcf13b203614959658c2d944f131"
                            ).await()
                        }

                        addMarkersFromText(res.data)
                    } catch (e: Exception) {
                        e.printStackTrace()

                        AlertDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.maps_tab_search_error_title))
                            .setMessage(getString(R.string.maps_tab_search_error_message))
                            .setPositiveButton("Ok", null)
                            .show()
                    }

                    hideLoading()
                }
            }
        }

        maps_tab_search_field.setOnKeyListener { _, keyCode, event ->
            if ((event?.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                maps_tab_search_field_arrow.performClick()
            }

            true
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        maps_tab_search_zone_button.visibility = View.INVISIBLE
        maps_tab_loading_overlay.requestFocus()

        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(), R.raw.map_style
            )
        )

        googleMap.setOnMarkerClickListener { marker ->
            val data = markers[marker]

            val location: LatLng?

            if (data is MapSearchData) {
                showDetailsCard(
                    stationId = data.uid,
                    title = data.station.name,
                    aqi = try {
                        data.aqi.toInt()
                    } catch (e: java.lang.Exception) {
                        null
                    },
                    date = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssX",
                        Locale.ENGLISH
                    ).parse(data.station.time).time
                )

                location = LatLng(data.lat, data.lon)
            } else if (data is TextSearchData) {
                showDetailsCard(
                    stationId = data.uid,
                    title = data.station.name,
                    aqi = data.aqi.toInt(),
                    date = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.ENGLISH
                    ).parse(data.time.stime).time
                )

                location = LatLng(data.station.geo[0], data.station.geo[1])
            } else {
                location = null
            }

            if (location != null) {
                ignoreNextMove = true
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
            }

            true

        }

        googleMap.setOnCameraMoveStartedListener {
            if (!ignoreNextMove)
                maps_tab_search_zone_button.visibility = View.VISIBLE
        }
    }


    private fun showLoading() {
        maps_tab_search_zone_button.visibility = View.INVISIBLE
        maps_tab_search_field.isClickable = false
        maps_tab_search_field_arrow.isClickable = false
        maps_tab_search_field_progress.visibility = View.VISIBLE
        maps_tab_loading_overlay.visibility = View.VISIBLE
        maps_tab_loading_overlay.requestFocus()
        maps_tab_station.visibility = View.GONE
        hideDetailsCard()
    }

    private fun hideLoading() {
        maps_tab_search_zone_button.visibility = View.VISIBLE
        maps_tab_search_field.isClickable = true
        maps_tab_search_field_arrow.isClickable = true
        maps_tab_search_field_progress.visibility = View.INVISIBLE
        maps_tab_loading_overlay.visibility = View.GONE
    }

    private fun showDetailsCard(stationId : Int, title: String, date: Long, aqi: Int?) {
        maps_tab_station.visibility = View.VISIBLE
        val intent = Intent(context, DetailActivity::class.java)

        intent.putExtra("name", title)
        intent.putExtra("id", stationId)

        maps_tab_station.setOnClickListener{ startActivity(intent)}
        maps_tab_station_name.text = title
        maps_tab_station_aqi_value.text = aqi?.toString() ?: "?"

        maps_tab_station_aqi_value.setTextColor(
            ContextCompat.getColor(
                requireContext(), when (aqi) {
                    in 0..50 -> R.color.aqi_good
                    in 51..100 -> R.color.aqi_moderate
                    in 101..150 -> R.color.aqi_unhealthy_sensitive
                    in 151..200 -> R.color.aqi_unhealthy
                    in 201..300 -> R.color.aqi_very_unhealthy
                    else -> R.color.aqi_hazardous
                }
            )
        )

        maps_tab_station_date_value.text = DateUtils.getRelativeTimeSpanString(date)
        map.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.maps_station_card_height))
    }

    private fun hideDetailsCard() {
        maps_tab_station.visibility = View.GONE
        map.setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.maps_station_card_height), 0)
    }

    private fun addMarkers(markers: List<MapSearchData>) {
        map.clear()

        val stations = if (markers.size > 20) {
            markers.subList(0, 20)
        } else {
            markers
        }

        if (stations.isEmpty()) {
            AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.maps_tab_zone_no_station_title))
                .setMessage(getString(R.string.maps_tab_zone_no_station_message))
                .setPositiveButton("Ok", null)
                .show()
        } else {
            for (data in stations) {
                val marker = MarkerOptions().position(
                    LatLng(
                        data.lat,
                        data.lon
                    )
                ).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        GoogleMapUtils.getBitmap(
                            requireContext(),
                            R.drawable.ic_map_marker
                        )
                    )
                )

                this.markers[map.addMarker(marker)] = data
            }
        }
    }

    private fun addMarkersFromText(markers: List<TextSearchData>) {
        map.clear()

        val stations = if (markers.size > 20) {
            markers.subList(0, 20)
        } else {
            markers
        }

        if (stations.isEmpty()) {
            AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.maps_tab_search_no_station_title))
                .setMessage(getString(R.string.maps_tab_search_no_station_message))
                .setPositiveButton("Ok", null)
                .show()
        } else {
            val builder = LatLngBounds.Builder()

            for (data in stations) {
                val location = LatLng(
                    data.station.geo[0],
                    data.station.geo[1]
                )

                val marker = MarkerOptions().position(
                    location
                ).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        GoogleMapUtils.getBitmap(
                            requireContext(),
                            R.drawable.ic_map_marker
                        )
                    )
                )

                builder.include(location)

                this.markers[map.addMarker(marker)] = data
            }

            ignoreNextMove = true
            maps_tab_search_zone_button.visibility = View.INVISIBLE
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        }
    }

}