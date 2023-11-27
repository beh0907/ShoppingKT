package com.skymilk.shoppingkt.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.skymilk.shoppingkt.databinding.FragmentRegisterBinding
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClick()
        setObserve()
    }

    private fun setClick() {
        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    editName.text.toString().trim(),
                    editEmail.text.toString().trim()
                )
                val password = editPassword.text.toString().trim()

                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it) {
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d(TAG, it.data.toString())
                        binding.btnRegister.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.d(TAG, it.message.toString())
                        binding.btnRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setObserve() {

    }

}