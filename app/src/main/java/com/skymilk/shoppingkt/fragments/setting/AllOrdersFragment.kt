package com.skymilk.shoppingkt.fragments.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.skymilk.shoppingkt.adapters.AllOrdersAdapter
import com.skymilk.shoppingkt.databinding.FragmentAllOrdersBinding
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentAllOrdersBinding
    private val viewModel: AllOrdersViewModel by viewModels()

    private val allOrdersAdapter: AllOrdersAdapter by lazy { AllOrdersAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOrdersRecyclerView()

        setClick()
        setObserver()
    }

    private fun initOrdersRecyclerView() {
        allOrdersAdapter.onItemClick = {
            //shopping_graph에서 지정한 action과 argument
            val action =
                AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }

        binding.recyclerAllOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = allOrdersAdapter
        }
    }

    private fun setClick() {
        binding.apply {
            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setObserver() {
        //주문 목록 가져오기
        lifecycleScope.launch {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        allOrdersAdapter.differ.submitList(it.data)

                        if (it.data.isNullOrEmpty()) {
                            binding.txtEmptyOrders.visibility = View.VISIBLE
                        } else {
                            binding.txtEmptyOrders.visibility = View.GONE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

}