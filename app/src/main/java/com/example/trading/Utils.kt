package com.example.trading

import java.math.RoundingMode
import java.text.DecimalFormat

/*
* @created 15/04/23 - 4:37 AM
* @project Zoomcar
* @author Rohil
* Copyright (c) 2022 Zoomcar. All rights reserved.
*/
private val df = DecimalFormat("0.00").also {
    it.roundingMode = RoundingMode.UP
}
fun Double.roundOff(): String {
    return df.format(this)
}