package com.skymilk.shoppingkt.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.adapters.AddressAdapter
import com.skymilk.shoppingkt.adapters.BillingProductsAdapter
import com.skymilk.shoppingkt.databinding.FragmentBillingBinding
import com.skymilk.shoppingkt.helper.toCommaString
import com.skymilk.shoppingkt.models.Address
import com.skymilk.shoppingkt.models.CartProduct
import com.skymilk.shoppingkt.models.Order
import com.skymilk.shoppingkt.models.OrderStatus
import com.skymilk.shoppingkt.utils.HorizontalItemDecoration
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.BillingViewModel
import com.skymilk.shoppingkt.viewmodels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {

    private lateinit var binding: FragmentBillingBinding
    private val billingViewModel: BillingViewModel by viewModels() //결제 처리 뷰 모델

    private val addressAdapter: AddressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter: BillingProductsAdapter by lazy { BillingProductsAdapter() }

    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>() //장바구니 상품 목록
    private var totalPrice: Int = 0 // 총 합계액

    private var selectedAddress: Address? = null // 선택한 배송 주소
    private val orderViewModel: OrderViewModel by viewModels() //주문 처리 뷰 모델

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAddressRecyclerView()
        initBillingProductsRecyclerView()

        setClick()
        setBillingObserve()
        setOrderObserve()

        binding.txtTotalPrice.text = "${totalPrice.toCommaString()} 원"
    }

    private fun initAddressRecyclerView() {
        addressAdapter.onItemClick = {
            selectedAddress = it
        }


        binding.recyclerAddress.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalItemDecoration())
            adapter = addressAdapter
        }
    }

    private fun initBillingProductsRecyclerView() {
        billingProductsAdapter.onItemClick = {

        }

        billingProductsAdapter.differ.submitList(products)

        binding.recyclerProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalItemDecoration())
            adapter = billingProductsAdapter
        }
    }

    private fun setClick() {
        binding.apply {
            btnPlaceOrder.setOnClickListener {
                if (selectedAddress == null) {
                    Toast.makeText(requireContext(), "배송 주소를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val order = Order(
                    OrderStatus.Ordered,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                showOrderConfirmDialog(order)
            }

            imgAddAddress.setOnClickListener {
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
            }
        }
    }

    private fun setBillingObserve() {
        lifecycleScope.launch {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setOrderObserve() {
        lifecycleScope.launch {
            orderViewModel.order.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.btnPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnPlaceOrder.revertAnimation()
                        findNavController().navigateUp() // 주문이 완료되었다면 화면을 종료한다
                        Snackbar.make(requireView(), "주문 신청이 완료되었습니다.", Snackbar.LENGTH_LONG).show()

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.btnPlaceOrder.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showOrderConfirmDialog(order: Order) {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("상품 주문")
            setMessage("정말 주문하시겠습니까?")
            setPositiveButton("주문하기") { dialog, _ ->
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
            setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
        alertDialog.show()
    }
}