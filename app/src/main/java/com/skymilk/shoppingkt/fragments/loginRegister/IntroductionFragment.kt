package com.skymilk.shoppingkt.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClick()
    }

    private fun setClick() {
        binding.apply {
            btnStart.setOnClickListener{
                findNavController().navigate(R.id.accountOptionsFragment)
            }
        }
    }

}