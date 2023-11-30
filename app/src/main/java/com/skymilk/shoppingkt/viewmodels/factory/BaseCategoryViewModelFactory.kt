package com.skymilk.shoppingkt.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.models.Category
import com.skymilk.shoppingkt.viewmodels.BaseCategoryViewModel

class BaseCategoryViewModelFactory(
    private val fireStore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseCategoryViewModel(fireStore, category) as T
    }

}