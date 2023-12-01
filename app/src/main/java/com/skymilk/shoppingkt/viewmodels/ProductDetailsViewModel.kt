package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.firebase.FirebaseCommon
import com.skymilk.shoppingkt.models.CartProduct
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addCart = _addCart.asStateFlow()

    //장바구니에 상품을 추가 혹은 수량 증가
    fun addUpdateProduct(cartProduct: CartProduct) {
        viewModelScope.launch { _addCart.emit(Resource.Loading()) }

        fireStore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {

                it.documents.let {
                    if (it.isEmpty()) {
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)

                        if (product == cartProduct) { // 같은 상품이 이미 있다면 수량을 늘린다
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else { //없다면 상품을 추가한다
                            addNewProduct(cartProduct)
                        }
                    }
                }

            }.addOnFailureListener {
                viewModelScope.launch { _addCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    //장바구니에 상품 추가
    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductCart(cartProduct) { addedCartProduct, exception ->
            viewModelScope.launch {
                if (exception == null) _addCart.emit(Resource.Success(addedCartProduct))
                else _addCart.emit(Resource.Error(exception.message.toString()))
            }
        }
    }

    //장바구니 내 상품 수량 증가
    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { addedId, exception ->
            viewModelScope.launch {
                if (exception == null) _addCart.emit(Resource.Success(cartProduct))
                else _addCart.emit(Resource.Error(exception.message.toString()))
            }
        }
    }
}