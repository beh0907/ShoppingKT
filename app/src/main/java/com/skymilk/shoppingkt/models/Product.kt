package com.skymilk.shoppingkt.models

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
) {
    //null 비허용 변수의 기본값을 설정해줘야 한다
    constructor() : this("0", "", "", 0, images = emptyList())
}