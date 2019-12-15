package com.supinternet.aqi.ui.screens.main.tabs.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.model.station.Data
import com.supinternet.aqi.ui.screens.main.tabs.favs.Fav
import com.supinternet.aqi.ui.screens.main.tabs.favs.ListAdapter
import com.supinternet.aqi.ui.screens.splashscreen.SplashScreenActivity
import kotlinx.android.synthetic.main.fragment_favs.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsTab : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleApiClient = GoogleApiClient
            .Builder(this.context!!)
            .enableAutoManage(this.activity!!, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()

        sign_out_button.setOnClickListener {
            Auth.GoogleSignInApi.signOut(googleApiClient)
            googleApiClient.disconnect()
            val intent = Intent(this.context, SplashScreenActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}