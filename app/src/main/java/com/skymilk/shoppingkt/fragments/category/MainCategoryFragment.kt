package com.skymilk.shoppingkt.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.adapters.BestDealsAdapter
import com.skymilk.shoppingkt.adapters.BestProductsAdapter
import com.skymilk.shoppingkt.adapters.SpecialProductsAdapter
import com.skymilk.shoppingkt.databinding.FragmentMainCategoryBinding
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {
    private lateinit var binding: FragmentMainCategoryBinding
    private val viewModel: MainCategoryViewModel by viewModels()

    private val specialProductsAdapter: SpecialProductsAdapter by lazy { SpecialProductsAdapter() }
    private val bestDealsAdapter: BestDealsAdapter by lazy { BestDealsAdapter() }
    private val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpecialProductsRecyclerView()
        initBestDealsRecyclerView()
        initBestProductsRecyclerView()

        setScroll()
        setObserve()
    }

    private fun initSpecialProductsRecyclerView() {
        specialProductsAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        binding.recyclerSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    private fun initBestDealsRecyclerView() {
        bestDealsAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        binding.recyclerBestDeals.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun initBestProductsRecyclerView() {
        bestProductsAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        binding.recyclerBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setScroll() {
        binding.scrollViewMain.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { scrollView, _, scrollY, _, _ ->
            if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollY) {
                viewModel.getBestProducts()
            }

        })
    }

    private fun setObserve() {
        lifecycleScope.launch {
            //collectLatest는 갱신이 처리보다 빠를 경우 마지막 처리만 반영한다
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launch {
            //collectLatest는 갱신이 처리보다 빠를 경우 마지막 처리만 반영한다
            viewModel.bestDeals.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)

                        hideLoading()

                    }

                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()

                        hideLoading()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launch {
            //collectLatest는 갱신이 처리보다 빠를 경우 마지막 처리만 반영한다
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.progressBarBestProducts.visibility = View.GONE
                    }

                    is Resource.Loading -> {
                        binding.progressBarBestProducts.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {
                        binding.progressBarBestProducts.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBarMain.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBarMain.visibility = View.GONE
    }

}