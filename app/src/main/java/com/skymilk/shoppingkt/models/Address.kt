package com.skymilk.shoppingkt.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val name: String,
    val addressMain: String,
    val addressSub: String,
    val phone: String,
) : Parcelable {
    constructor(): this("", "", "", "", "")
}
