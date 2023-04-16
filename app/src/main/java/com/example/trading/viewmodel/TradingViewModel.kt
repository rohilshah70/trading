package com.example.trading.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.trading.R
import com.example.trading.getResponseFromPref
import com.example.trading.network.ApiService
import com.example.trading.roundOff
import com.example.trading.saveResponseInPref
import com.example.trading.vo.ResponseVO
import com.example.trading.vo.TitleAmountVO
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradingViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {
    private val TAG = "TradingViewModel"

    private val _uiState = MutableStateFlow(TradingUiState())
    val uiState: StateFlow<TradingUiState> = _uiState.asStateFlow()

    private var currentValue = 0.0
    private var totalInvestment = 0.0
    private var totalPandL = 0.0
    private var todayPandL = 0.0

    private val apiService by lazy {
        ApiService.create()
    }

    private val finalData = mutableListOf<TitleAmountVO>()

    private var response: ResponseVO? = null

    init {
        _uiState.update { currentState ->
            currentState.copy(
                showLoader = true,
                showError = false,
                isOffline = false
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            //TODO remove in actual usage
            delay(3000)
            response = apiService.getProducts()
            if (response != null) {
                saveResponseInPref(context = context, responseVO = response)
                println("Saved data --> ${getResponseFromPref(context)}")
                _uiState.value = TradingUiState(
                    response = response
                )
                createBottomSheetData()
            } else {
                //Check if response is saved in SharedPreference
                response = getResponseFromPref(context)

                if (response != null) {
                    _uiState.value = TradingUiState(
                        response = response,
                        isOffline = true
                    )
                    createBottomSheetData()
                } else {
                    _uiState.value = TradingUiState(
                        showError = true
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
        _uiState.update { currentState ->
            currentState.copy(
                bottomData = finalData
            )
        }
    }


    private fun calculateCurrentValue() {
        response?.data?.forEach {
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
        response?.data?.forEach {
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
        response?.data?.forEach {
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
)