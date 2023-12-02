package com.skymilk.shoppingkt.models

sealed class OrderStatus(val status: String) {
    data object Ordered : OrderStatus("Ordered") //주문 상태
    data object Canceled : OrderStatus("Canceled") //주문 취소 상태
    data object Confirmed : OrderStatus("Confirmed")//주문 확인 및 배송 준비
    data object Shipped : OrderStatus("Shipped")//배송 중
    data object Delivered : OrderStatus("Delivered")//상품 도착
    data object Returned : OrderStatus("Returned")//상품 환불
}
