package com.skymilk.shoppingkt.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.skymilk.shoppingkt.BuildConfig
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.activities.AuthActivity
import com.skymilk.shoppingkt.databinding.FragmentProfileBinding
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.utils.showBottomNavigation
import com.skymilk.shoppingkt.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVersion()
        setClick()
        setObserve()
    }

    private fun setVersion() {
        binding.txtVersion.text = "Version ${BuildConfig.VERSION_NAME}"
    }

    private fun setClick() {
        binding.apply {
            layoutProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
            }

            layoutAllOrders.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
            }

            layoutBilling.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                    0,
                    emptyArray(),
                    false
                )
                findNavController().navigate(action)
            }

            layoutLogOut.setOnClickListener {

                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("로그아웃")
                    setMessage("정말 로그아웃 하시겠습니까?")
                    setPositiveButton("로그아웃") { dialog, _ ->
                        viewModel.logout()
                        val intent = Intent(requireActivity(), AuthActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                        dialog.dismiss()
                    }
                    setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.create()
                alertDialog.show()
            }
        }
    }

    private fun setObserve() {
        lifecycleScope.launch {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setUserInfo(it.data!!)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUserInfo(user: User) {
        binding.apply {
            Glide.with(requireContext()).load(user.imagePath).error(ColorDrawable(Color.BLACK))
                .into(imgUser)

            txtUserName.text = user.name
            txtEmail.text = user.email
        }
    }

    override fun onResume() {
        super.onResume()

        //숨긴 하단 탭을 다시 표시한다
        showBottomNavigation(requireActivity())
    }
}