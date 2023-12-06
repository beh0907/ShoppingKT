package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.firebase.FirebaseCommon
import com.skymilk.shoppingkt.helper.getOfferPrice
import com.skymilk.shoppingkt.models.CartProduct
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {
    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    //cartProducts의 목록이 변동될 때마다 합계값을 다시 계산한다
    val productsTotalPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> sumTotalPrice(it.data!!)
            else -> 0
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private fun sumTotalPrice(data: List<CartProduct>): Int {
        return data.sumOf { cartProduct ->
            //할인율이 반영된 가격과 수량을 곱하여 총합을 구한다
            cartProduct.product.offerPercentage.getOfferPrice(cartProduct.product.price) * cartProduct.quantity
        }
    }

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    init {
        getCartProducts()
    }

    fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }

        //addSnapshotListener는 실시간으로 값이 변동될 때 가져온다
        fireStore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    fun deleteCartProducts(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            fireStore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }

    //장바구니에 상품 추가
    fun addProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductCart(cartProduct) { addedCartProduct, exception ->
        }
    }

    fun changeQuantity(cartProduct: CartProduct, state: FirebaseCommon.QuantityChangeState) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        //상품 정보를 가지고 있는지 확인한다
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id


            when (state) {
                FirebaseCommon.QuantityChangeState.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }

                FirebaseCommon.QuantityChangeState.DECREASE -> {
                    if (cartProduct.quantity == 1) { //1개 남았을 때 감소시키면 삭제 다이얼로그 팝업
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }

                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->

            if (exception != null) viewModelScope.launch {
                _cartProducts.emit(
                    Resource.Error(
                        exception.message.toString()
                    )
                )
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if (exception != null) viewModelScope.launch {
                _cartProducts.emit(
                    Resource.Error(
                        exception.message.toString()
                    )
                )
            }
        }

    }
}