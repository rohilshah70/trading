package com.example.trading

import android.content.Context
import androidx.core.content.edit
import com.example.trading.vo.ResponseVO
import com.google.gson.Gson
import java.math.RoundingMode
import java.text.DecimalFormat

private val PREFERENCE_STORE = "trading_store"
private val RESPONSE_KEY = "response"

private val df = DecimalFormat("0.00").also {
    it.roundingMode = RoundingMode.UP
}

fun Double.roundOff(): String {
    return df.format(this)
}

fun saveResponseInPref(context: Context, responseVO: ResponseVO?) {
    val sharedPreferences =
        context.getSharedPreferences(PREFERENCE_STORE, 0)
    sharedPreferences.edit(commit = true) {
        putString(RESPONSE_KEY, Gson().toJson(responseVO))
    }
}
fun getResponseFromPref(context: Context) : ResponseVO? {
    val sharedPreferences =
        context.getSharedPreferences(PREFERENCE_STORE, 0)
    sharedPreferences.getString(
        RESPONSE_KEY,
        null
    )?.let {
        return Gson().fromJson(it, ResponseVO::class.java)
    }
    return null
}