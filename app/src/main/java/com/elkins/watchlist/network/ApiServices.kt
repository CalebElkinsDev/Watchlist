package com.elkins.watchlist.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


private const val BASE_URL = "https://imdb-api.com/en/API/"
private const val API_KEY = "k_akzini69"

private val moshi = Moshi.Builder()
    //.add(Date::class.java, Rfc3339DateJsonAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(OkHttpClient())
    .baseUrl(BASE_URL)
    .build()

interface ApiServices {

    @GET("Title/k_akzini69/tt1375666")
    suspend fun getInception() : MovieResponse

    @GET("SearchMovie/$API_KEY/{search}")
    suspend fun searchForMovie(@Path("search") search: String) : SearchResponse

    // https://imdb-api.com/en/API/SearchMovie/k_akzini69/inception 2010
}

object ImdbApi {
    val retrofitService: ApiServices by lazy {
        retrofit.create(ApiServices::class.java)
    }
}