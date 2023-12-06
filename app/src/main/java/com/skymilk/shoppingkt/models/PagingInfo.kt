package com.skymilk.shoppingkt.models

import com.google.firebase.firestore.DocumentSnapshot


//리스트 페이징 처리를 위한 페이징 클래스
data class PagingInfo(
    var page:Int = 1,
    var amount: Long = 20, //페이지 당 데이터 수
    var isPagingEnd: Boolean = false,
    var lastSnapshot: DocumentSnapshot? = null
)
