package com.supinternet.aqi.ui.screens.splashscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.supinternet.aqi.ui.screens.intro.IntroActivityIntent
import com.supinternet.aqi.ui.screens.main.MainActivityIntent

class SplashScreenActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleApiClient = GoogleApiClient
            .Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()

        if (isUserConnected(googleApiClient)) {
            startActivity(MainActivityIntent())
        } else {
            startActivity(IntroActivityIntent())
        }

        finish()
    }

    private fun isUserConnected(client: GoogleApiClient): Boolean {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount != null) {
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("REQ_CODE", requestCode.toString())
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}