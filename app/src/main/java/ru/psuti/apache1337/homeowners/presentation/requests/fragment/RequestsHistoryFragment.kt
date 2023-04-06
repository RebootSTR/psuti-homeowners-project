package ru.psuti.apache1337.homeowners.presentation.requests.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.databinding.FragmentRequestsHistoryBinding
import ru.psuti.apache1337.homeowners.presentation.requests.adapter.RequestItemsAdapter
import ru.psuti.apache1337.homeowners.presentation.requests.viewmodel.RequestViewModel


@AndroidEntryPoint
class RequestsHistoryFragment : Fragment() {
    private var _binding: FragmentRequestsHistoryBinding? = null
    private val binding get() = _binding!!

    private val requestViewModel: RequestViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestsHistoryBinding.inflate(inflater, container, false)
        navController = findNavController()
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRequests()

        binding.buttonFilter.setOnClickListener {
            val dialog = RequestsFilterDialogFragment()
            dialog.show(childFragmentManager, "dialog")
        }
    }

    private fun setRequests() {
        val adapter: RequestItemsAdapter = RequestItemsAdapter(
            navController
        )
        binding.requests.layoutManager = LinearLayoutManager(requireContext())
        binding.requests.adapter = adapter
        requestViewModel.filteredData.observe(viewLifecycleOwner) {
            adapter.addElement(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}