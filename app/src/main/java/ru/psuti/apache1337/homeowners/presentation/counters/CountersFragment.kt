package ru.psuti.apache1337.homeowners.presentation.counters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.databinding.FragmentCountersBinding

@AndroidEntryPoint
class CountersFragment: Fragment() {
    private var _binding: FragmentCountersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CountersViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentCountersBinding.inflate(inflater, container, false)

        val pager = binding.indicationsViewPager
        val tabLayout = binding.tabLayout

        val id = viewModel.getId()
        val demo = id == 0

        pager.adapter = CountersFragmentStateAdapter(requireActivity(), demo = demo)

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Подать показания"
                }
                1 -> {
                    tab.text = "История"
                }
            }
        }.attach()




        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}