package com.skymilk.shoppingkt.fragments.setting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.skymilk.shoppingkt.databinding.FragmentUserAccountBinding
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.LoginViewModel
import com.skymilk.shoppingkt.viewmodels.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel: UserAccountViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val data = it.data?.data
                if (data == null) {
                    Toast.makeText(requireContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                imageUri = data
                Glide.with(this).load(imageUri).error(ColorDrawable(Color.BLACK))
                    .into(binding.imgUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClick()
        setObserver()
    }

    private fun setClick() {
        binding.apply {
            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }

            imgEdit.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                imageActivityResultLauncher.launch(intent)
            }

            txtUpdatePassword.setOnClickListener {
//                setUpBottomSheetDialog {
//                }
            }

            btnSave.setOnClickListener {
                val name = editName.text.toString().trim()
                val email = editEmail.text.toString().trim()
                val user = User(name, email)

                viewModel.updateUser(user, imageUri)
            }
        }
    }

    private fun setObserver() {
        //유저 정보 가져오기
        lifecycleScope.launch {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showUserLoading()
                    }

                    is Resource.Success -> {
                        showUserLoading()
                        setUserInfo(it.data!!)
                    }

                    is Resource.Error -> {
                        hideUserLoading()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        //유저 정보 수정
        lifecycleScope.launch {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnSave.revertAnimation()
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        binding.btnSave.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        //비밀번호 리셋하기
        lifecycleScope.launch {
            loginViewModel.reset.collectLatest {
                when (it) {
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

    private fun setUserInfo(user: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(user.imagePath)
                .error(ColorDrawable(Color.BLACK)).into(imgUser)

            editName.setText(user.name)
            editEmail.setText(user.email)
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressBar.visibility = View.GONE

            imgUser.visibility = View.VISIBLE
            imgEdit.visibility = View.VISIBLE
            editName.visibility = View.VISIBLE
            editEmail.visibility = View.VISIBLE
            txtUpdatePassword.visibility = View.VISIBLE
            btnSave.visibility = View.VISIBLE
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE

            imgUser.visibility = View.INVISIBLE
            imgEdit.visibility = View.INVISIBLE
            editName.visibility = View.INVISIBLE
            editEmail.visibility = View.INVISIBLE
            txtUpdatePassword.visibility = View.INVISIBLE
            btnSave.visibility = View.INVISIBLE
        }
    }
}