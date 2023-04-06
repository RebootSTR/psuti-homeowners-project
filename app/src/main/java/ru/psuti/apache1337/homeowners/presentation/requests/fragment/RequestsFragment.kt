package ru.psuti.apache1337.homeowners.presentation.requests.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.databinding.FragmentRequestsBinding
import ru.psuti.apache1337.homeowners.presentation.requests.adapter.RequestFragmentStateAdapter
import ru.psuti.apache1337.homeowners.presentation.requests.viewmodel.RequestViewModel

@AndroidEntryPoint
class RequestsFragment : Fragment() {
    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!

    private val args: ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestsFragmentArgs by navArgs()
    private val viewModel: RequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentRequestsBinding.inflate(inflater, container, false)

        val pager = binding.requestsViewPager
        val tabLayout = binding.tabLayout

        pager.adapter = RequestFragmentStateAdapter(args.toCreateFragment, requireActivity())

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Создать заявку"
                }
                1 -> {
                    tab.text = "История заявок"
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