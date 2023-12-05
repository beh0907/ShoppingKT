package com.skymilk.shoppingkt.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.utils.Constants.Companion.INTRODUCTION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    companion object {
        const val SHOPPING_ACTIVITY = 1
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }

    init {
        val user = auth.currentUser
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)

        //로그인된 유저 정보가 있다면 액티비티 이동
        //자동 로그인 처리
        if (user != null) {
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) {
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        } else {
            Unit
        }
    }

    fun setButtonClick(isClick: Boolean) {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, isClick).apply()
    }

}