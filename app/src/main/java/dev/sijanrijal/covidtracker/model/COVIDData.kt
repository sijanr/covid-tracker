package dev.sijanrijal.covidtracker.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import java.util.*

data class COVIDData (
    @SerializedName("dateChecked") val lastUpdatedNationalCases : Date,
    @SerializedName( "negativeIncrease") val dailyDecreaseCases : Int,
    @SerializedName( "positiveIncrease") val dailyIncreaseCases : Int,
    @SerializedName( "deathIncrease") val deathIncreaseCases : Int,
    val state : String?
    ) {
}