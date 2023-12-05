package com.skymilk.shoppingkt.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String = "",
    val totalPrice: Int = 0,
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val dateTime: String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(Date()), //현재 시간을 자동으로 설정
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
) : Parcelable
