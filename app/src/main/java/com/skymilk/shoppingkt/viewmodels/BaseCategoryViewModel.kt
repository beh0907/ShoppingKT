package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.Category
import com.skymilk.shoppingkt.models.Product
import com.skymilk.shoppingkt.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaseCategoryViewModel constructor(
    private val fireStore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    init {
        getOfferProducts()
        getBestProducts()
    }

    fun getOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        println("category : $category")

        fireStore.collection("Products")
            .limit(20)
            .whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).get()//offerPercentage의 값이 있는 상품만
            .addOnSuccessListener {

                val result = it.toObjects(Product::class.java)
                println("result : $result")

                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(result))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun getBestProducts() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        fireStore.collection("Products").whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).get()//offerPercentage의 값이 있는 상품만
            .addOnSuccessListener {

                val result = it.toObjects(Product::class.java)

                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(result))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}