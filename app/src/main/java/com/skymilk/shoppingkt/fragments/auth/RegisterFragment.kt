package com.skymilk.shoppingkt.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.databinding.FragmentRegisterBinding
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.RegisterValidation
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

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


            txtLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun setObserve() {
        //회원가입 시 상태 정보 처리
        lifecycleScope.launch {
            viewModel.register.collect {
                when (it) {
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

        //회원가입 시 검증 상태 정보 처리
        lifecycleScope.launch {
            viewModel.validation.collect {
                if (it.email is RegisterValidation.Failed) {
                    //Main = UI 상호작용을 위한 메인 스레드
                    //IO = IO 작업에 최적화된 스레드
                    //Default = 정렬/파싱과 같은 많은 동작을 수행에 최적화
                    withContext(Dispatchers.Main) {
                        binding.editEmail.apply {
                            //이메일 입력 뷰 포커싱 후 에러 메시지 표시
                            requestFocus()
                            error = it.email.message
                        }
                    }
                }

                if (it.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.editPassword.apply {
                            //비밀번호 입력 뷰 포커싱 후 에러 메시지 표시
                            requestFocus()
                            error = it.password.message
                        }
                    }
                }
            }
        }
    }
}