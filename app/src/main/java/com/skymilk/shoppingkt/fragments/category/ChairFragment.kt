package com.skymilk.shoppingkt.fragments.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.shoppingkt.databinding.FragmentBaseCategoryBinding
import com.skymilk.shoppingkt.models.Category
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.BaseCategoryViewModel
import com.skymilk.shoppingkt.viewmodels.factory.BaseCategoryViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {
    @Inject
    lateinit var fireStore: FirebaseFirestore

    private val viewModel: BaseCategoryViewModel by viewModels {
        BaseCategoryViewModelFactory(fireStore, Category.Chair)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserve()
    }

    private fun setObserve() {
        lifecycleScope.launch {
            viewModel.offerProducts.collect {
                when (it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                    }

                    is Resource.Success -> {
                        offersAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideOfferLoading()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.bestProducts.collect {
                when (it) {
                    is Resource.Loading -> {
                        showBestProductsLoading()
                    }

                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideBestProductsLoading()
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onOfferPagingRequest() {

    }

    override fun onBestProductsPagingRequest() {

    }
}