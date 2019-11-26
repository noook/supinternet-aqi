package com.supinternet.aqi.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supinternet.aqi.data.network.model.map.MapSearch
import com.supinternet.aqi.data.network.model.ranking.Ranking
import com.supinternet.aqi.data.network.model.search.TextSearch
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AQIAPI {

    @GET("map/bounds/")
    fun searchInMapAsync(@Query("latlng", encoded = false) latLng: String, @Query("token") token: String): Deferred<MapSearch>

    @GET("search/")
    fun textSearchAsync(@Query("keyword") search: String, @Query("token") token: String): Deferred<TextSearch>

    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        private const val BASE_URL: String = "https://api.waqi.info/"

        @Synchronized
        fun getInstance(): AQIAPI {
            retrofit = retrofit ?: buildRetrofit()
            return retrofit!!.create(AQIAPI::class.java)
        }

        private fun buildRetrofit() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

}

interface RankingAPI {

    @GET("ranking/index.json")
    fun getRanking(): Deferred<Ranking>

    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        private const val BASE_URL: String = "https://waqi.info/rtdata/"

        @Synchronized
        fun getInstance(): RankingAPI {
            retrofit = retrofit ?: buildRetrofit()
            return retrofit!!.create(RankingAPI::class.java)
        }

        private fun buildRetrofit() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

}