package com.example.trading.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trading.R

/*
* @created 15/04/23 - 9:01 PM
* @project Zoomcar
* @author Rohil
* Copyright (c) 2022 Zoomcar. All rights reserved.
*/

@Composable
fun ErrorView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ){
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            ) {
                Image(
                    modifier = Modifier,
                    painter = painterResource(R.drawable.ic_error_no_network),
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier,
                text = "Something Went wrong, \nPlease try again later"
            )
        }
    }
}


@Preview
@Composable
private fun PreviewErrorView(){
    ErrorView()
}