package com.skymilk.shoppingkt.models

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import com.skymilk.shoppingkt.R

sealed class OrderStatus(val status: String) {
    data object Ordered : OrderStatus("Ordered") //주문 상태
    data object Canceled : OrderStatus("Canceled") //주문 취소 상태
    data object Confirmed : OrderStatus("Confirmed")//주문 확인 및 배송 준비
    data object Shipped : OrderStatus("Shipped")//배송 중
    data object Delivered : OrderStatus("Delivered")//상품 도착
    data object Returned : OrderStatus("Returned")//상품 환불
    data object Unspecified : OrderStatus("")//기본 상태
}

fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> OrderStatus.Ordered
        "Confirmed" -> OrderStatus.Confirmed
        "Shipped" -> OrderStatus.Shipped
        "Delivered" -> OrderStatus.Delivered
        "Canceled" -> OrderStatus.Canceled
        "Returned" -> OrderStatus.Returned
        else -> OrderStatus.Unspecified
    }
}

fun getOrderStatusColor(status: OrderStatus, resources: Resources): ColorDrawable {
    return when (status) {
        is OrderStatus.Ordered -> ColorDrawable(resources.getColor(R.color.g_orange_yellow))
        is OrderStatus.Confirmed -> ColorDrawable(resources.getColor(R.color.g_green))
        is OrderStatus.Shipped -> ColorDrawable(resources.getColor(R.color.g_green))
        is OrderStatus.Delivered -> ColorDrawable(resources.getColor(R.color.g_green))
        is OrderStatus.Canceled -> ColorDrawable(resources.getColor(R.color.g_red))
        is OrderStatus.Returned -> ColorDrawable(resources.getColor(R.color.g_red))
        else -> ColorDrawable(resources.getColor(R.color.g_gray500))
    }
}

//정상 주문 상태 4단계 인덱스 가져오기
fun getOrderStep(status: OrderStatus): Int {
    return when (status) {
        is OrderStatus.Ordered -> 0
        is OrderStatus.Confirmed -> 1
        is OrderStatus.Shipped -> 2
        is OrderStatus.Delivered -> 3
        else -> 0
    }

}