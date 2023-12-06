package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.Order
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    //주문 신청
    fun placeOrder(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }

        //복수의 쓰기 작업을 일괄로 수행하기 위한 runBatch
        //주문을 위한 동시 처리를 위해 배치 설정
        fireStore.runBatch { batch ->
            //사용자 개인의 주문 목록 DB 저장
            fireStore
                .collection("user")
                .document(auth.uid!!)
                .collection("orders")
                .document()
                .set(order)

            //전체 주문 DB 저장
            fireStore.collection("orders").document().set(order)

            //상품이 주문됐다면 장바구니를 비운다
            fireStore
                .collection("user")
                .document(auth.uid!!)
                .collection("cart")
                .get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        //모든 장바구니를 삭제한다
                        //주문할 상품과 매칭하여 선택되지 않은 상품은 보류할 수도 있다
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch { _order.emit(Resource.Success(order)) }
        }.addOnFailureListener {
            viewModelScope.launch { _order.emit(Resource.Error(it.message.toString())) }
        }

    }

}