package com.skymilk.shoppingkt.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun showKeyboard(editText: EditText, activity: Activity){
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, 0)
}

fun hideKeyboard(activity: Activity){
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
}