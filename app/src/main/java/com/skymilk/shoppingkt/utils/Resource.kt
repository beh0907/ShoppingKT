package com.skymilk.shoppingkt.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : Resource<T>(data) // 성공 데이터 리턴
    class Error<T>(message: String?) : Resource<T>(message = message) // 에러 메시지 리턴
    class Loading<T> : Resource<T>() // 시도 중
    class Unspecified<T> : Resource<T>() //기본 상태
}
