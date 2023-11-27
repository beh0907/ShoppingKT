package com.skymilk.shoppingkt.models

data class User(
    val name: String,
    val email: String,
    var imagePath: String = "" // 이미지는 없을 수도 있다
) {
    constructor() : this("", "", "")

}
