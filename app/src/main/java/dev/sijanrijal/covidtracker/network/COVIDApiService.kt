package dev.sijanrijal.covidtracker.network

import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import dev.sijanrijal.covidtracker.model.COVIDData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl("https://api.covidtracking.com/v1/")
    .build()

interface COVIDApi{

    @GET("us/daily.json")
    suspend fun getAllUSData() : List<COVIDData>

    @GET("states/daily.json")
    suspend fun getAllStatesData() : List<COVIDData>
}

object COVIDApiService {
    val retrofitService = retrofit.create(COVIDApi::class.java)
}

