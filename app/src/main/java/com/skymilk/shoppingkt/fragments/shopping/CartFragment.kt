package com.skymilk.shoppingkt.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.adapters.CartProductsAdapter
import com.skymilk.shoppingkt.databinding.FragmentCartBinding
import com.skymilk.shoppingkt.firebase.FirebaseCommon
import com.skymilk.shoppingkt.helper.toCommaString
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.utils.VerticalItemDecoration
import com.skymilk.shoppingkt.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()

    private val cartProductsAdapter: CartProductsAdapter by lazy { CartProductsAdapter() }

    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCartProductsRecyclerView()

        setClick()
        setObserve()
    }

    private fun initCartProductsRecyclerView() {
        cartProductsAdapter.onItemClick = {
            val bundle = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        cartProductsAdapter.onPlusClick = {
            println("onPlusClick $it")
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChangeState.INCREASE)
        }

        cartProductsAdapter.onMinusClick = {
            println("onMinusClick $it")
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChangeState.DECREASE)
        }

        binding.recyclerCartProduct.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartProductsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setClick() {
        binding.apply {
            btnCheckOut.setOnClickListener {
                //shopping_graph에서 지정한 action과 argument
                val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                    totalPrice,
                    cartProductsAdapter.differ.currentList.toTypedArray(),
                    true
                )
                findNavController().navigate(action)
            }

            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }

        }
    }

    private fun setObserve() {
        //합계 액수 갱신
        lifecycleScope.launch {
            viewModel.productsTotalPrice.collectLatest { totalPrice ->
                this@CartFragment.totalPrice = totalPrice
                binding.txtTotalPrice.text = "${totalPrice.toCommaString()} 원"
            }
        }

        //장바구니 목록 갱신
        lifecycleScope.launch {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE

                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                        } else {
                            hideEmptyCart()
                            cartProductsAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }

        //삭제 다이얼로그 팝업
        lifecycleScope.launch {
            viewModel.deleteDialog.collectLatest { cartProduct ->
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("장바구니 상품 삭제")
                    setMessage("정말 ${cartProduct.product.name}을(를) 삭제하시겠습니까?")
                    setPositiveButton("삭제") { dialog, _ ->
                        viewModel.deleteCartProducts(cartProduct)
                        dialog.dismiss()
                    }
                    setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.create()
                alertDialog.show()
            }
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE

            //나머지 뷰 숨기기
            recyclerCartProduct.visibility = View.GONE
            layoutTotalPrice.visibility = View.GONE
            btnCheckOut.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            binding.layoutCartEmpty.visibility = View.GONE

            //나머지 뷰 표시하기
            recyclerCartProduct.visibility = View.VISIBLE
            layoutTotalPrice.visibility = View.VISIBLE
            btnCheckOut.visibility = View.VISIBLE
        }
    }
}