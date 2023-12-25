package com.skymilk.shoppingkt.fragments.shopping

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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
                lifecycleScope.launch {
                    //권한 체크
                    val isGranted = requestAudioPermission()

                    //권한 미 획득 시 종료
                    if (!isGranted) {
                        Toast.makeText(requireContext(), "오디오 권한을 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }

                    //stt 시작
                    startStt()
                }
            }
        }
    }

    //stt 시작
    private fun startStt() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "검색어를 말해주세요.")
        }

        activityResult.launch(intent)
    }


    //STT 결과 정보 콜백
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        if (it.resultCode != RESULT_OK) {
            Toast.makeText(requireContext(), "인식된 음성이 없습니다.", Toast.LENGTH_SHORT)
                .show()
            return@registerForActivityResult
        }

        val res: ArrayList<String> =
            it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(res[0])
        findNavController().navigate(action)
    }

    //오디오 권한 요청
    private suspend fun requestAudioPermission(): Boolean {
        return TedPermission.create()
            .setPermissions(Manifest.permission.RECORD_AUDIO)
            .checkGranted()
    }

}