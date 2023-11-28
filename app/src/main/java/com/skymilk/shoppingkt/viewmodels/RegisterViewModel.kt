package com.skymilk.shoppingkt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.Constants
import com.skymilk.shoppingkt.utils.RegisterFieldState
import com.skymilk.shoppingkt.utils.RegisterValidation
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.utils.validateEmail
import com.skymilk.shoppingkt.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    //파이어베이스 연동 회원가입
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {

            //로딩 상태 저장
            viewModelScope.launch { _register.emit(Resource.Loading()) }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { // 계정 생성 성공
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }.addOnFailureListener { // 계정 생성 실패
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            //검증 상태 저장
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password)
            )

            viewModelScope.launch { _validation.send(registerFieldState) }
        }
    }

    //회원가입 성공 시 유저정보를 저장한다
    private fun saveUserInfo(uid: String, user: User) {
        //파이어스토어에 유저 정보를 저장한다
        db.collection(Constants.USER_COLLECTION).document(uid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }

    }

    //계정 정보를 검증
    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email) // 이메일 검증
        val passwordValidation = validatePassword(password) // 비밀번호 검증

        //두 검증 모두 통과 여부
        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }
}