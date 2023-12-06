package com.skymilk.shoppingkt.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.activities.ShoppingActivity
import com.skymilk.shoppingkt.databinding.FragmentLoginBinding
import com.skymilk.shoppingkt.dialogs.setUpResetPassword
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.utils.hideKeyboard
import com.skymilk.shoppingkt.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClick()
        setObserve()
    }

    private fun setClick() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = editEmail.text.toString()
                val password = editPassword.text.toString()
                viewModel.login(email, password)
            }

            //회원가입 화면 이동
            txtRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            //비밀번호 초기화
            txtForgotPassword.setOnClickListener {
                setUpResetPassword {
                    viewModel.resetPassword(it)
                }
            }

            //비밀번호 입력 후 완료할 때 로그인 수행
            editPassword.setOnEditorActionListener { _, id, _ ->
                var handled = false
                if (id == EditorInfo.IME_ACTION_DONE) {
                    binding.btnLogin.performClick() // 로그인 버튼 클릭
                    hideKeyboard(requireActivity()) // 키보드 숨기기
                    handled = true
                }
                handled
            }
        }
    }

    private fun setObserve() {
        //회원가입 시 상태 정보 처리
        lifecycleScope.launch {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).apply {
                            //ShoppingActivity만 남도록 플래그 설정
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(this)
                        }

                    }

                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }


        //비밀번호 초기화 처리
        lifecycleScope.launch {
            viewModel.reset.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Snackbar.make(requireView(), "비밀번호 초기화 링크를 전송했습니다.", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error : ${it.message}", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }

}