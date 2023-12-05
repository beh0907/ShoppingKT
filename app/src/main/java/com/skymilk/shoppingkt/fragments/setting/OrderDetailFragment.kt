package com.skymilk.shoppingkt.fragments.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.skymilk.shoppingkt.adapters.BillingProductsAdapter
import com.skymilk.shoppingkt.databinding.FragmentOrderDetailBinding
import com.skymilk.shoppingkt.helper.toCommaString
import com.skymilk.shoppingkt.models.Order
import com.skymilk.shoppingkt.models.OrderStatus
import com.skymilk.shoppingkt.models.getOrderStatus
import com.skymilk.shoppingkt.models.getOrderStep
import com.skymilk.shoppingkt.utils.VerticalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding

    private val productsAdapter: BillingProductsAdapter by lazy { BillingProductsAdapter() }

    private val args by navArgs<OrderDetailFragmentArgs>()
    private var order: Order = Order()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        order = args.order
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOrdersRecyclerView()
        initOrderDetail()

        setClick()
    }

    private fun initOrderDetail() {
        binding.apply {
            txtOrderId.text = "주문 #${order.orderId}"

            //주문 상태 4단계 설정
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            //현재 상태에 따라 스텝 인덱스 설정
            val currentStep = getOrderStep(getOrderStatus(order.orderStatus))
            stepView.go(currentStep, true)
            if (currentStep == 3) stepView.done(true)

            //주문자 정보
            txtName.text = order.address.name
            txtAddress.text = order.address.addressMain
            txtPhoneNumber.text = order.address.phone

            //합계 비용
            txtTotalPrice.text = "${order.totalPrice.toCommaString()} 원"
        }
    }

    private fun initOrdersRecyclerView() {
        productsAdapter.onItemClick = {

        }

        productsAdapter.differ.submitList(order.products)

        binding.recyclerProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = productsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setClick() {
        binding.apply {
            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}