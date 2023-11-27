package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val register: Flow<Resource<FirebaseUser>> = _register

    fun createAccountWithEmailAndPassword(user: User, password: String) {

        runBlocking {
            _register.emit(Resource.Loading())
        }

        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener { // 계정 생성 성공
                it.user?.let {
                    _register.value = Resource.Success(it)
                }
            }.addOnFailureListener { // 계정 생성 실패
                _register.value = Resource.Error(it.message.toString())
            }
    }
}