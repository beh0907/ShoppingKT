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
import androidx.recyclerview.widget.GridLayoutManager
import com.skymilk.shoppingkt.adapters.BestProductsAdapter
import com.skymilk.shoppingkt.databinding.FragmentSearchBinding
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    private val productsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    private val args by navArgs<SearchFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchRecyclerView()

        setSearchListener()
        setObserve()
        setArguments()
    }

    private fun setArguments() {
        val speechText = args.speechText

        if (!speechText.isNullOrEmpty()) {
            binding.editSearch.setText(speechText)
            viewModel.getSearchByKeyword(speechText)
        }
    }

    private fun initSearchRecyclerView() {
        productsAdapter.onItemClick = {
            val action = SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }

        binding.recyclerSearch.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = productsAdapter
        }
    }

    private fun setSearchListener() {
        binding.apply {

            imgSearch.setOnClickListener {
                val keyword = editSearch.text.toString()
                if (keyword.isEmpty()) {
                    Toast.makeText(requireContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.getSearchByKeyword(keyword)
            }
        }
    }

    private fun setObserve() {
        lifecycleScope.launch {
            viewModel.search.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        productsAdapter.differ.submitList(it.data)
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