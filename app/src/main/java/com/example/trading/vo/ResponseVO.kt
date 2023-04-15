package com.example.trading.vo

import com.example.trading.network.ApiService.Companion.deserializeData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseVO(
    @SerialName("client_id")
    val clientId: String? = null,

    @SerialName("data")
    val data: List<ItemDataVO>? = null,

    @SerialName("error")
    val error: String? = null,

    @SerialName("response_type")
    val responseType: String? = null,

    @SerialName("timestamp")
    val timestamp: Long? = null
){
    companion object {

        fun deserializeResponseVO(data: String): ResponseVO? {
            try {
                return deserializeData(data)
            } catch (e: Exception) {
                println(e)
            }
            return null
        }
    }
}

@Serializable
data class ItemDataVO(
    @SerialName("avg_price")
    val avgPrice: String? = null,

    @SerialName("cnc_used_quantity")
    val cncUsedQuantity: Int? = null,

    @SerialName("collateral_qty")
    val collateralQty: Int? = null,

    @SerialName("collateral_type")
    val collateralType: String? = null,

    @SerialName("collateral_update_qty")
    val collateralUpdateQty: Int? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    @SerialName("haircut")
    val haircut: Double? = null,

    @SerialName("holdings_update_qty")
    val holdingsUpdateQty: Int? = null,

    @SerialName("isin")
    val isin: String? = null,

    @SerialName("product")
    val product: String? = null,

    @SerialName("quantity")
    val quantity: Int? = null,

    @SerialName("symbol")
    val symbol: String? = null,

    @SerialName("t1_holding_qty")
    val t1HoldingQty: Int? = null,

    @SerialName("token_bse")
    val tokenBse: String? = null,

    @SerialName("token_nse")
    val tokenNse: String? = null,

    @SerialName("withheld_collateral_qty")
    val withheldCollateralQty: Int? = null,

    @SerialName("withheld_holding_qty")
    val withheldHoldingQty: Int? = null,

    @SerialName("ltp")
    val ltp: Double? = null,

    @SerialName("close")
    val close: Double? = null
)