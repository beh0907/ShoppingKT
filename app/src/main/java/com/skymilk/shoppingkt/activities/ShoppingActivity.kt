package com.skymilk.shoppingkt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {

    lateinit var binding: ActivityShoppingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentShoppingHost)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}