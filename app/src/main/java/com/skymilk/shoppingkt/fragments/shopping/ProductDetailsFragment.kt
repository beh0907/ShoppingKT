package com.skymilk.shoppingkt.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.adapters.ColorsAdapter
import com.skymilk.shoppingkt.adapters.ImagesViewPagerAdapter
import com.skymilk.shoppingkt.adapters.SizesAdapter
import com.skymilk.shoppingkt.databinding.FragmentProductDetailsBinding
import com.skymilk.shoppingkt.models.CartProduct
import com.skymilk.shoppingkt.models.Product
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.utils.hideBottomNavigation
import com.skymilk.shoppingkt.utils.showBottomNavigation
import com.skymilk.shoppingkt.viewmodels.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()

    private lateinit var binding: FragmentProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    private val imageSViewPagerAdapter: ImagesViewPagerAdapter by lazy { ImagesViewPagerAdapter() }
    private val colorsAdapter: ColorsAdapter by lazy { ColorsAdapter() }
    private val sizesAdapter: SizesAdapter by lazy { SizesAdapter() }

    private lateinit var currentProduct: Product
    private var selectedColor: Int? = null
    private var selectedSize: String? = null

    val decimal = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //하단 탭을 숨긴다
        hideBottomNavigation(requireActivity())
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //숨긴 하단 탭을 다시 표시한다
        showBottomNavigation(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initImagesViewPager()
        initColorsRecyclerView()
        initSizesRecyclerView()

        setArguments()
        setClick()
        setObserve()
    }

    private fun setArguments() {
        currentProduct = args.product

        binding.apply {
            txtProductName.text = currentProduct.name
            txtProductDescription.text = currentProduct.description
            txtProductPrice.text = "${decimal.format(currentProduct.price)} 원"

            //값이 없다면 헤더 텍스트도 숨긴다
            if (currentProduct.colors.isNullOrEmpty()) txtColor.visibility = View.INVISIBLE
            if (currentProduct.sizes.isNullOrEmpty()) txtSize.visibility = View.INVISIBLE

//            product.offerPercentage?.let {
//                txtOldPrice.text = "${decimal.format(product.price)} 원"
//                val offerPrice = product.price * (1f - (it / 100f))
//                txtNewPrice.text = "${decimal.format(product.price)} 원"
//            }
        }


        //이미지는 null이 허용되지 않는다
        imageSViewPagerAdapter.differ.submitList(currentProduct.images)
        currentProduct.colors?.let {
            colorsAdapter.differ.submitList(currentProduct.colors)
        }
        currentProduct.sizes?.let {
            sizesAdapter.differ.submitList(currentProduct.sizes)
        }
    }

    private fun setClick() {
        binding.apply {
            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }

            btnAddCart.setOnClickListener {
                viewModel.addUpdateProduct(
                    CartProduct(
                        currentProduct,
                        1,
                        selectedColor,
                        selectedSize
                    )
                )
            }
        }
    }

    private fun initImagesViewPager() {
        binding.viewPagerProductImages.apply {
            adapter = imageSViewPagerAdapter
        }
    }

    private fun initColorsRecyclerView() {
        colorsAdapter.onItemClick = {
            selectedColor = it //선택한 색상을 저장한다
        }

        binding.recyclerColors.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = colorsAdapter
        }
    }

    private fun initSizesRecyclerView() {
        sizesAdapter.onItemClick = {
            selectedSize = it //선택한 사이즈를 저장한다
        }

        binding.recyclerSizes.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sizesAdapter
        }
    }

    private fun setObserve() {
        lifecycleScope.launch {
            viewModel.addCart.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnAddCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnAddCart.revertAnimation()
                        binding.btnAddCart.setBackgroundColor(resources.getColor(R.color.black))
                        Toast.makeText(requireContext(), "상품이 장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        binding.btnAddCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }
}