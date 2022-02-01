@file:Suppress("SpellCheckingInspection")

package com.elkins.watchlist.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private const val CONNECT_TIMEOUT: Long = 15
private const val READ_TIMEOUT: Long = 15
private const val BASE_URL = "https://imdb-api.com/en/API/"
private const val API_KEY = "k_akzini69"

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient().newBuilder()
    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface ApiServices {

    @GET("https://imdb-api.com/API/AdvancedSearch/$API_KEY")
    suspend fun searchForMovie(@Query("title") search: String,
                               @Query("title_type") titleType: String) : SearchResponse

    @GET("Title/$API_KEY/{id}")
    suspend fun getMovieFromId(@Path("id") id: String) : MovieResponse
}

object ImdbApi {
    val retrofitService: ApiServices by lazy {
        retrofit.create(ApiServices::class.java)
    }

    // Cancel all running and queried network calls
    fun cancelRequests() {
        for(call in client.dispatcher().queuedCalls()) {
            call.cancel()
        }

        for(call in client.dispatcher().runningCalls()) {
            call.cancel()
        }
    }
}