package com.skymilk.shoppingkt.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.skymilk.shoppingkt.databinding.FragmentAddressBinding
import com.skymilk.shoppingkt.models.Address
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClick()
        setObserve()
    }

    private fun setClick() {
        binding.apply {
            btnSave.setOnClickListener {
                val address = Address(
                    editAddressTitle.text.toString(),
                    editName.text.toString(),
                    editAddressMain.text.toString(),
                    editAddressSub.text.toString(),
                    editPhone.text.toString(),
                )

                viewModel.addAddress(address)
            }

            btnDelete.setOnClickListener {

            }
        }
    }

    private fun setObserve() {
        lifecycleScope.launch {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {

                        binding.progressBar.visibility = View.INVISIBLE

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}