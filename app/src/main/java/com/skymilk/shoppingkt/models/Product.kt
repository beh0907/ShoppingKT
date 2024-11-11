package com.skymilk.shoppingkt.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>,
    val regDateTime: String,
    val updateDateTime: String
) : Parcelable {
    //null 비허용 변수의 기본값을 설정해줘야 한다
    constructor() : this(
        "0",
        "",
        "",
        0,
        images = emptyList(),
        regDateTime = "",
        updateDateTime = ""
    )
}