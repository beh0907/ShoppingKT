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

    private val args by navArgs<AddressFragmentArgs>()

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

        initAddress()

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
                val address = Address(
                    editAddressTitle.text.toString(),
                    editName.text.toString(),
                    editAddressMain.text.toString(),
                    editAddressSub.text.toString(),
                    editPhone.text.toString(),
                )
                viewModel.deleteAddress(address)
            }
        }
    }

    //목록에서 주소를 선택하면 확인을 위해 정보를 보여준다
    private fun initAddress() {
        val address = args.address

        if (address == null) binding.btnDelete.visibility = View.GONE
        else {
            binding.apply {
                editAddressTitle.setText(address.addressTitle)
                editName.setText(address.name)
                editAddressMain.setText(address.addressMain)
                editAddressSub.setText(address.addressSub)
                editPhone.setText(address.phone)

                btnSave.text = "확인"
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
                        findNavController().navigateUp()

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }
    }
}