package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Filter
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
class SearchViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search = _search.asStateFlow()

    fun getSearchByKeyword(keyword: String) {
        viewModelScope.launch { _search.emit(Resource.Loading()) }

        fireStore.collection("Products")
//            .where(
//                Filter.or(
//                    Filter.arrayContains("name", keyword),// 상품명 키워드 체크
//                    Filter.arrayContains("description", keyword), // 상품 설명 키워드 체크
//                )
//            )
            .orderBy("name")
            .get()
            .addOnSuccessListener {

                val result = it.toObjects(Product::class.java)
                println("result : $result")
                viewModelScope.launch { _search.emit(Resource.Success(result)) }
            }.addOnFailureListener {

                println("result : ${it.message}")
                viewModelScope.launch { _search.emit(Resource.Error(it.message)) }
            }
    }
}