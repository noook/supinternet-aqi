package com.supinternet.aqi.ui.screens.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.R


class  DetailActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")

       val infos = findViewById<TextView>(R.id.infos)
        infos.text = "$name $id"







    }


}