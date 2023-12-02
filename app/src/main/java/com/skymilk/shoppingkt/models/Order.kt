package com.skymilk.shoppingkt.models

data class Order(
    val orderStatus:OrderStatus,
    val totalPrice:Int,
    val products:List<CartProduct>,
    val address: Address
)
