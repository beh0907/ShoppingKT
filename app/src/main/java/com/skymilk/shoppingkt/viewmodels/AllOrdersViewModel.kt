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
class AllOrdersViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrders()
    }

    //로그인한 사용자의 모든 주문 목록을 가져온다
    fun getAllOrders() {
        viewModelScope.launch { _allOrders.emit(Resource.Loading()) }

        fireStore
            .collection("user")
            .document(auth.uid!!)
            .collection("orders")
            .get()
            .addOnSuccessListener {
                val result = it.toObjects(Order::class.java)
                viewModelScope.launch { _allOrders.emit(Resource.Success(result)) }
            }.addOnFailureListener {
                viewModelScope.launch { _allOrders.emit(Resource.Error(it.message)) }
            }
    }

}