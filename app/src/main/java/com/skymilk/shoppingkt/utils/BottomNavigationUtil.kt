package com.skymilk.shoppingkt.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.activities.ShoppingActivity


fun showBottomNavigation(activity: Activity) {
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility = RecyclerView.VISIBLE
}

fun hideBottomNavigation(activity: Activity) {
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility = RecyclerView.GONE
}