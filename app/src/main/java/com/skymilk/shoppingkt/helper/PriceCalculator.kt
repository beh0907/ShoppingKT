package com.skymilk.shoppingkt.helper

import java.text.DecimalFormat

val decimal = DecimalFormat("#,###")

//할인 정보가 있으면 반영, 없으면 그대로 리턴
fun Float?.getOfferPrice(price: Int): Int {
    if (this == null) return price

    return (price * (1f - (this / 100f))).toInt()
}

fun Int?.toCommaString(): String {
    return decimal.format(this)
}