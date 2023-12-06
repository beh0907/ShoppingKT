package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.PagingInfo
import com.skymilk.shoppingkt.models.Product
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts = _specialProducts.asStateFlow()

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals = _bestDeals.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val pagingInfo = PagingInfo()

    init { // 첫 시작시 기본 셋팅
        getSpecialProducts()
        getBestDeals()
        getBestProducts()
    }

    //파이어스토어에서 스페셜 상품 목록을 가져온다
    fun getSpecialProducts() {
        viewModelScope.launch { _specialProducts.emit(Resource.Loading()) }

        fireStore.collection("Products").whereEqualTo("category", "Special Products")
            .get().addOnSuccessListener {
                val result = it.toObjects(Product::class.java)

                viewModelScope.launch { _specialProducts.emit(Resource.Success(result)) }
            }.addOnFailureListener { error ->
                viewModelScope.launch { _specialProducts.emit(Resource.Error(error.message)) }
            }
    }

    //파이어스토어에서 핫딜 상품 목록을 가져온다
    fun getBestDeals() {
        viewModelScope.launch { _bestDeals.emit(Resource.Loading()) }

        fireStore.collection("Products").whereEqualTo("category", "Best Deals")
            .get().addOnSuccessListener {

                val result = it.toObjects(Product::class.java)

                viewModelScope.launch { _bestDeals.emit(Resource.Success(result)) }
            }.addOnFailureListener { error ->
                viewModelScope.launch { _bestDeals.emit(Resource.Error(error.message)) }
            }
    }

    //파이어스토어에서 베스트 상품 목록을 가져온다
    fun getBestProducts() {
        if (pagingInfo.isPagingEnd) return

        viewModelScope.launch { _bestProducts.emit(Resource.Loading()) }

        val ref = fireStore.collection("Products")
        val query = if (pagingInfo.lastSnapshot != null)
            ref.orderBy("name").startAfter(pagingInfo.lastSnapshot).limit(pagingInfo.amount)
        else
            ref.orderBy("name").limit(pagingInfo.amount)

        query
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty)
                    pagingInfo.lastSnapshot = it.last() // 가져온 데이터가 있다면 마지막 스냅샷을 저장하여 페이징처리에 활용한다

                if (it.size() != pagingInfo.amount.toInt() || it.isEmpty)
                    pagingInfo.isPagingEnd = true // 가져온 데이터가 없거나 페이지 당 개수보다 적다면 끝낸다

                val result = it.toObjects(Product::class.java)
                println("result : $result")

                viewModelScope.launch { _bestProducts.emit(Resource.Success(result)) }
            }.addOnFailureListener {
                println("error : ${it.message}")
                viewModelScope.launch { _bestProducts.emit(Resource.Error(it.message)) }
            }
    }
}

