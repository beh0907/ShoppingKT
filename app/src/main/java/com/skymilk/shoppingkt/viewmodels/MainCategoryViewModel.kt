package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
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
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        fireStore.collection("Products").whereEqualTo("category", "Special Products")
            .get().addOnSuccessListener { result ->

                val resultList = result.toObjects(Product::class.java)

                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(resultList))
                }
            }.addOnFailureListener { error ->
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(error.message))
                }
            }
    }

    //파이어스토어에서 핫딜 상품 목록을 가져온다
    fun getBestDeals() {
        viewModelScope.launch {
            _bestDeals.emit(Resource.Loading())
        }

        fireStore.collection("Products").whereEqualTo("category", "Best Deals")
            .get().addOnSuccessListener { result ->

                val resultList = result.toObjects(Product::class.java)

                viewModelScope.launch {
                    _bestDeals.emit(Resource.Success(resultList))
                }
            }.addOnFailureListener { error ->
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Error(error.message))
                }
            }
    }

    //파이어스토어에서 베스트 상품 목록을 가져온다
    fun getBestProducts() {
        //페이징이 완료되었다면 이후 작업을 수행하지 않는다
        if (pagingInfo.isPagingEnd) return

        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        fireStore.collection("Products").limit(pagingInfo.amount * pagingInfo.amount)
            .get().addOnSuccessListener { result ->

                val resultList = result.toObjects(Product::class.java)

                //이전 목록과 새로 가져온 목록의 정보가 같다면 완료 되었음을 확인한다
                pagingInfo.isPagingEnd = pagingInfo.oldList == resultList
                pagingInfo.oldList = resultList

                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(resultList))
                }
            }.addOnFailureListener { error ->
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(error.message))
                }
            }
    }
}

//리스트 페이징 처리를 위한 페이징 클래스
internal data class PagingInfo(
    var page: Long = 1, //페이지 정보
    var amount: Long = 10, //페이지 당 데이터 수
    var oldList: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)