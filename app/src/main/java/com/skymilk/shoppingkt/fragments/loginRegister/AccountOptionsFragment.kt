package com.skymilk.shoppingkt.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.databinding.FragmentAccountOptionsBinding
import com.skymilk.shoppingkt.databinding.FragmentIntroductionBinding

class AccountOptionsFragment : Fragment() {
    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClick()
    }

    private fun setClick() {
        binding.apply {
            btnLogin.setOnClickListener{
                findNavController().navigate(R.id.loginFragment)
            }

            btnRegister.setOnClickListener{
                findNavController().navigate(R.id.registerFragment)
            }
        }
    }

}