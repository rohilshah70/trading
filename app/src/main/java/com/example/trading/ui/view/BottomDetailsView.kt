package com.example.trading.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trading.vo.TitleAmountVO
import androidx.compose.material.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BottomDetailsView(
    modifier: Modifier = Modifier,
    data: List<TitleAmountVO>? = null
) {
    val state = rememberLazyListState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        data?.let {
            LazyColumn(
                modifier = Modifier.padding(vertical = 14.dp),
                state = state,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(it) { index, item ->
                    Column {
                        if (index == data.lastIndex) {
                            Spacer(modifier = Modifier.padding(top = 14.dp))
                        }
                        item.title?.let { title ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = title,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp),
                                    text = item.amount ?: "",
                                    color = Color.Black,
                                    textAlign = TextAlign.End,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBottomSheet() {
    BottomDetailsView(
        data = listOf(
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
}