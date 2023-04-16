package com.example.trading.ui.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.trading.R
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trading.roundOff
import com.example.trading.viewmodel.TradingUiState
import com.example.trading.viewmodel.TradingViewModel
import com.example.trading.vo.ResponseVO.Companion.deserializeResponseVO
import com.example.trading.vo.TitleAmountVO

@Composable
fun TradingMainView(
    modifier: Modifier = Modifier,
    viewModel: TradingViewModel,
    onBackPressed: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState()

    BackHandler(
        enabled = true,
        onBack = {
            onBackPressed()
        }
    )

    TradingView(
        modifier = modifier,
        uiState = uiState.value
    )
}

@Composable
private fun TradingView(
    modifier: Modifier = Modifier,
    uiState: TradingUiState
) {
    val TAG = "TradingMainView"

    val state = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .background(colorResource(id = R.color.upstox_toolbar))
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            start = 12.dp,
                            top = 8.dp
                        ),
                    text = stringResource(id = R.string.toolbar_title) + if(uiState.isOffline)
                        " - ${stringResource(id = R.string.offline_text)}" else "",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }


            uiState.response?.data?.let {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(bottom = 8.dp),
                    state = state,
//                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(it) { index, items ->
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items.symbol?.let {
                                RowItem(
                                    title = it,
                                    titleBold = true,
                                    label = stringResource(id = R.string.ltp),
                                    amount = if(items.ltp != null)
                                        items.ltp.roundOff() else null
                                )
                            }
                            var profitLoss: String? = null
                            try {
                                val avgPrice = items.avgPrice?.toDouble()
                                if (avgPrice != null
                                    && items.ltp != null
                                    && items.quantity != null
                                ) {
                                    profitLoss =
                                        (items.ltp.minus(avgPrice) * items.quantity).roundOff()
                                }
                            } catch (e: NumberFormatException) {
                                Log.e(TAG, "number invalid")
                            }

                            items.quantity?.let {
                                RowItem(
                                    title = it.toString(),
                                    titleBold = false,
                                    label = stringResource(id = R.string.pl),
                                    amount = profitLoss
                                )
                            }

                            if (index != uiState.response.data.lastIndex) {
                                Divider(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                uiState.bottomData?.let {
                    BottomDetailsView(
                        modifier = Modifier,
                        data = uiState.bottomData
                    )
                }
            }

        }
    }

    if (uiState.showLoader) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.75f))
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = colorResource(id = R.color.upstox_toolbar)
            )
        }
    }

    if (uiState.showError){
        ErrorView()
    }
}

@Composable
private fun RowItem(
    title: String,
    titleBold: Boolean = true,
    label: String,
    amount: String? = null
){
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = title,
            color = Color.Black,
            fontWeight = if (titleBold) FontWeight.Bold else null
        )
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row {
                Text(
                    modifier = Modifier,
                    text = label,
                    color = Color.Black,
                    textAlign = TextAlign.End,
                )
                amount?.let { amount ->
                    Text(
                        modifier = Modifier
                            .padding(start = 12.dp),
                        text = stringResource(id = R.string.rs_symbol) + amount,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTradingMainView() {
    val response =
        "{\"client_id\":\"183247\",\"data\":[{\"avg_price\":\"26.00\",\"cnc_used_quantity\":0,\"collateral_qty\":0,\"collateral_type\":\"WC\",\"collateral_update_qty\":0,\"company_name\":\"Indian Railway Finance Corpora3-41\",\"haircut\":0.12,\"holdings_update_qty\":0,\"isin\":\"INE053F01010\",\"product\":\"D\",\"quantity\":575,\"symbol\":\"IRFC\",\"t1_holding_qty\":0,\"token_bse\":\"543257\",\"token_nse\":\"2029\",\"withheld_collateral_qty\":0,\"withheld_holding_qty\":0,\"ltp\":100.5,\"close\":90},{\"avg_price\":\"450.05\",\"cnc_used_quantity\":0,\"collateral_qty\":0,\"collateral_type\":\"WC\",\"collateral_update_qty\":0,\"company_name\":\"Bandhan Bank Limited\",\"haircut\":0.45,\"holdings_update_qty\":0,\"isin\":\"INE545U01014\",\"product\":\"D\",\"quantity\":3,\"symbol\":\"BANDHANBNK\",\"t1_holding_qty\":0,\"token_bse\":\"541153\",\"token_nse\":\"2263\",\"withheld_collateral_qty\":0,\"withheld_holding_qty\":0,\"ltp\":200.15,\"close\":180.5},{\"avg_price\":\"390.06\",\"cnc_used_quantity\":0,\"collateral_qty\":0,\"collateral_type\":\"WC\",\"collateral_update_qty\":0,\"company_name\":\"INDIABULLS HOUSING FINANCE LTD1-72\",\"haircut\":0.43,\"holdings_update_qty\":0,\"isin\":\"INE148I01020\",\"product\":\"D\",\"quantity\":40,\"symbol\":\"IBULHSGFIN\",\"t1_holding_qty\":0,\"token_bse\":\"535789\",\"token_nse\":\"30125\",\"withheld_collateral_qty\":0,\"withheld_holding_qty\":0,\"ltp\":400.2,\"close\":300.5},{\"avg_price\":\"63.57\",\"cnc_used_quantity\":0,\"collateral_qty\":0,\"collateral_type\":\"WC\",\"collateral_update_qty\":0,\"company_name\":\"NATIONAL ALUMINIUM CO.LTD.\",\"haircut\":0.22,\"holdings_update_qty\":0,\"isin\":\"INE139A01034\",\"product\":\"D\",\"quantity\":9,\"symbol\":\"NATIONALUM\",\"t1_holding_qty\":0,\"token_bse\":\"532234\",\"token_nse\":\"6364\",\"withheld_collateral_qty\":0,\"withheld_holding_qty\":0,\"ltp\":500.45,\"close\":550.15}],\"error\":null,\"response_type\":\"get_holdings\",\"timestamp\":1624443123714}"
    val data = deserializeResponseVO(response)

    TradingView(
        uiState = TradingUiState(
            response = data,
            bottomData = listOf(
                TitleAmountVO(
                    title = "Current Value",
                    amount = "\u20B978900.00"
                ),
                TitleAmountVO(
                    title = "Total Investment",
                    amount = "\u20B932474.68"
                ),
                TitleAmountVO(
                    title = "Today's Profit and Loss",
                    amount = "\u20B9-9637.15"
                ),
                TitleAmountVO(
                    title = "Profit and Loss",
                    amount = "\u20B946425.32"
                ),
            )
        )
    )
}