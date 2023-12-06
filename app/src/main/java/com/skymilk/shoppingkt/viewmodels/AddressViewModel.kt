package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.Address
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _deleteAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val deleteAddress = _deleteAddress.asStateFlow()

    fun addAddress(address: Address) {
        if (!validateInputs(address)) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Error("정보 입력 상태를 확인해주세요.")) }
            return
        }

        viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }

        fireStore.collection("user").document(auth.uid!!).collection("address").document()
            .set(address)
            .addOnSuccessListener {
                viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
            }.addOnFailureListener {
                viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
            }

    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch { _deleteAddress.emit(Resource.Loading()) }
    }

    private fun validateInputs(address: Address): Boolean {

        return address.addressTitle.isNotEmpty() &&
                address.addressMain.isNotEmpty() &&
                address.addressSub.isNotEmpty() &&
                address.name.isNotEmpty() &&
                address.phone.isNotEmpty()
    }
}