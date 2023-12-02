package com.skymilk.shoppingkt.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.CartProduct

class FirebaseCommon(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val cartCollection =
        fireStore.collection("user").document(auth.uid!!).collection("cart")

    //카트에 상품 추가
    fun addProductCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }.addOnFailureListener {
                onResult(null, it)
            }
    }

    //수량 증가
    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        fireStore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    //수량 감소
    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        fireStore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChangeState {
        INCREASE, DECREASE
    }
}