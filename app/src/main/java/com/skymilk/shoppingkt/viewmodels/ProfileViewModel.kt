package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        fireStore.collection("user")
            .document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _user.emit(Resource.Error(error?.message.toString())) }
                } else {
                    val user = value.toObject(User::class.java)
                    viewModelScope.launch { _user.emit(Resource.Success(user)) }
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}