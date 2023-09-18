package com.example.trading.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.trading.R
import com.example.trading.getResponseFromPref
import com.example.trading.mvi.MviEffect
import com.example.trading.mvi.MviEvent
import com.example.trading.mvi.MviState
import com.example.trading.mvi.MviViewModel
import com.example.trading.network.ApiService
import com.example.trading.roundOff
import com.example.trading.saveResponseInPref
import com.example.trading.vo.ResponseVO
import com.example.trading.vo.TitleAmountVO
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradingViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : MviViewModel<TradingUiState, TradingEvent, TradingEffect>(TradingUiState()) {
    private val TAG = TradingViewModel::class.java.simpleName

    private var currentValue = 0.0
    private var totalInvestment = 0.0
    private var totalPandL = 0.0
    private var todayPandL = 0.0

    private val apiService by lazy {
        ApiService.create()
    }

    private val finalData = mutableListOf<TitleAmountVO>()

    private var apiResponse: ResponseVO? = null

    init {
        //Show loader initially
        setEffect(TradingEffect.ShowLoader)

        setEffect(TradingEffect.FetchDataFromApi)
    }


    override suspend fun handleEvents(event: TradingEvent) {
        // No event handled here
    }

    override suspend fun handleEffects(effect: TradingEffect) {
        when (effect) {
            TradingEffect.ShowLoader -> {
                updateState {
                    copy(
                        showLoader = true,
                        showError = false,
                        isOffline = false
                    )
                }
            }

            TradingEffect.FetchDataFromApi -> {
                viewModelScope.launch(Dispatchers.IO) {
                    //Make the api call
                    apiResponse = apiService.getProducts()

                    if (apiResponse != null) {
                        saveResponseInPref(context = context, responseVO = apiResponse)
                        println("Saved data --> ${getResponseFromPref(context)}")
                        setEffect(TradingEffect.UpdateMainData())
                        createBottomSheetData()
                    } else {
                        //Check if response is saved in SharedPreference
                        apiResponse = getResponseFromPref(context)

                        if (apiResponse != null) {
                            //Response found in shared pref, show a msg that it is local data
                            setEffect(TradingEffect.UpdateMainData(true))
                            createBottomSheetData()
                        } else {
                            //Data not found in shared pref, show error
                            setEffect(TradingEffect.ShowError)
                        }
                    }
                }
            }

            TradingEffect.ShowError -> {
                updateState {
                    TradingUiState(
                        showError = true
                    )
                }
            }

            TradingEffect.UpdateBottomSheetData -> {
                updateState {
                    copy(
                        bottomData = finalData
                    )
                }
            }

            is TradingEffect.UpdateMainData -> {
                updateState {
                    TradingUiState(
                        response = apiResponse,
                        isOffline = effect.isOffline
                    )
                }
            }
        }
    }

    private fun createBottomSheetData() {
        calculateCurrentValue()
        calculateTotalInvestment()
        calculateTodayPandL()
        calculateTotalPandL()
        setEffect(TradingEffect.UpdateBottomSheetData)
    }


    private fun calculateCurrentValue() {
        apiResponse?.data?.forEach {
            if (it.quantity != null && it.ltp != null) {
                currentValue += it.quantity.times(it.ltp)
            }
        }
        finalData.add(
            TitleAmountVO(
                context.getString(R.string.current_value),
                context.getString(R.string.rs_symbol) + currentValue.roundOff()
            )
        )
    }

    private fun calculateTotalInvestment() {
        apiResponse?.data?.forEach {
            if (it.quantity != null && it.avgPrice != null) {
                try {
                    totalInvestment += it.quantity.times(it.avgPrice.toDouble())
                } catch (e: NumberFormatException) {
                    Log.e(TAG, "number invalid")
                }
            }
        }
        finalData.add(
            TitleAmountVO(
                context.getString(R.string.total_investment),
                context.getString(R.string.rs_symbol) + totalInvestment.roundOff()
            )
        )
    }

    private fun calculateTotalPandL() {
        totalPandL = currentValue - totalInvestment
        finalData.add(
            TitleAmountVO(
                context.getString(R.string.total_profit_loss),
                context.getString(R.string.rs_symbol) + totalPandL.roundOff()
            )
        )
    }

    private fun calculateTodayPandL() {
        apiResponse?.data?.forEach {
            if (it.quantity != null && it.ltp != null && it.close != null) {
                todayPandL += (it.close.minus(it.ltp)).times(it.quantity)
            }
        }
        finalData.add(
            TitleAmountVO(
                context.getString(R.string.today_profit_loss),
                context.getString(R.string.rs_symbol) + todayPandL.roundOff()
            )
        )
    }
}

data class TradingUiState(
    val response: ResponseVO? = null,
    val isOffline: Boolean = false,
    val bottomData: List<TitleAmountVO>? = null,
    val showLoader: Boolean = false,
    val showError: Boolean = false
) : MviState

sealed class TradingEffect : MviEffect {
    object ShowLoader : TradingEffect()
    object ShowError : TradingEffect()
    object FetchDataFromApi : TradingEffect()
    object UpdateBottomSheetData : TradingEffect()
    data class UpdateMainData(val isOffline: Boolean = false) : TradingEffect()
}

sealed class TradingEvent : MviEvent {
    object BackPressed : TradingEvent()
}