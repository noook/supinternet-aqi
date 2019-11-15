package com.supinternet.aqi.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.R
import com.supinternet.aqi.ui.screens.main.tabs.aroundme.MapsTab
import com.supinternet.aqi.ui.screens.main.tabs.favs.FavsTab
import com.supinternet.aqi.ui.screens.main.tabs.settings.SettingsTab
import com.supinternet.aqi.ui.screens.main.tabs.travel.TravelTab
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

@Suppress("FunctionName")
fun Context.MainActivityIntent(): Intent {
    return Intent(this, MainActivity::class.java)
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO Afficher le bon fragment lorsqu'on clique sur la bottom bar
    }

}