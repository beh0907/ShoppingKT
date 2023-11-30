package com.skymilk.shoppingkt.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.adapters.BestProductsAdapter
import com.skymilk.shoppingkt.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding

    protected val offersAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOffersRecyclerView()
        initBestProductsRecyclerView()

        setScroll()
    }

    private fun setScroll() {
        binding.scrollViewBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { scrollView, _, scrollY, _, _ ->
            if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollY) {
                onBestProductsPagingRequest()
            }
        })
    }

    fun showOfferLoading() {
        binding.progressBarOffer.visibility = View.VISIBLE
    }

    fun hideOfferLoading() {
        binding.progressBarOffer.visibility = View.GONE
    }

    fun showBestProductsLoading() {
        binding.progressBarBestProducts.visibility = View.VISIBLE
    }

    fun hideBestProductsLoading() {
        binding.progressBarBestProducts.visibility = View.GONE
    }

    private fun initOffersRecyclerView() {
        binding.recyclerOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offersAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    //canScrollHorizontally -1 = 최좌단 / 1 = 최우단
                    //canScrollVertically -1 = 최상단 / 1 = 최하단
                    //해당 방향으로 스크롤이 불가능 = 끝까지 닿은 경우 다음 페이지 데이터 호출
                    if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                        onOfferPagingRequest()
                    }
                }
            })
        }

    }

    private fun initBestProductsRecyclerView() {
        binding.recyclerBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    open fun onOfferPagingRequest() {}
    open fun onBestProductsPagingRequest() {}
}