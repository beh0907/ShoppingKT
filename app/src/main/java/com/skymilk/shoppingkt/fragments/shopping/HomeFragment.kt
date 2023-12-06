package com.skymilk.shoppingkt.fragments.shopping

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.gun0912.tedpermission.coroutine.TedPermission
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.adapters.HomeViewPagerAdapter
import com.skymilk.shoppingkt.databinding.FragmentHomeBinding
import com.skymilk.shoppingkt.fragments.category.AccessoryFragment
import com.skymilk.shoppingkt.fragments.category.ChairFragment
import com.skymilk.shoppingkt.fragments.category.CupboardFragment
import com.skymilk.shoppingkt.fragments.category.FurnitureFragment
import com.skymilk.shoppingkt.fragments.category.MainCategoryFragment
import com.skymilk.shoppingkt.fragments.category.TableFragment
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()

        setClick()
        setObserve()
    }

    private fun initViewPager() {
        val categoryFragments = arrayListOf(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment(),
        )

        //슬라이드 뷰페이저 탭 이동을 막는다
        binding.viewPagerHome.isUserInputEnabled = false

        val viewPagerAdapter =
            HomeViewPagerAdapter(categoryFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "메인"
                1 -> tab.text = "의자"
                2 -> tab.text = "찬장"
                3 -> tab.text = "테이블"
                4 -> tab.text = "액세서리"
                5 -> tab.text = "가구"
            }
        }.attach()
    }

    private fun setClick() {
        binding.apply {
            layoutSearchBar.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }

            imgAudio.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    val isGranted = requestAudioPermission()

                    if (!isGranted) {
                        Toast.makeText(requireContext(), "오디오 권한을 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }

                    viewModel.startSTT()
                }
            }
        }
    }

    private fun setObserve() {
        lifecycleScope.launch {
            viewModel.speechText.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "음성 인식이 시작되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Success -> {
                        viewModel.stopStt()

                        val text = it.data

                        if (text.isNullOrEmpty()) {
                            Toast.makeText(requireContext(), "인식된 음성이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                            return@collectLatest
                        }

                        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(text)
                        findNavController().navigate(action)
                    }

                    is Resource.Error -> {
                        viewModel.stopStt()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    //오디오 권한 요청
    private suspend fun requestAudioPermission(): Boolean {
        return TedPermission.create()
            .setPermissions(Manifest.permission.RECORD_AUDIO)
            .checkGranted()
    }

}