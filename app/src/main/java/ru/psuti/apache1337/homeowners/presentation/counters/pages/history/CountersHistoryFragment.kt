package ru.psuti.apache1337.homeowners.presentation.counters.pages.history

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.psuti.apache1337.homeowners.databinding.FragmentCountersHistoryBinding
import ru.psuti.apache1337.homeowners.domain.counters.COUNTER_FILTER_DIALOG_RETURN_CODE
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterFilter
import kotlin.properties.Delegates

class CountersHistoryFragment(private val demo: Boolean) : Fragment() {

    private var _binding: FragmentCountersHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CountersHistoryViewModel by activityViewModels()

    private lateinit var listAdapter: CounterHistoryListAdapter
    private var isConnected by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentCountersHistoryBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        isConnected = activeNetwork?.isConnected == true


        binding.buttonFilter.setOnClickListener {
            val dialog = CountersFilterDialogFragment()
            dialog.setTargetFragment(this, COUNTER_FILTER_DIALOG_RETURN_CODE)
            dialog.show(requireFragmentManager(), "dialog")
        }

        viewModel.sorted.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.e("d", "not null")
                binding.recyclerView.adapter = CounterHistoryListAdapter(it)
                (binding.recyclerView.adapter as CounterHistoryListAdapter).notifyDataSetChanged()
            }
        }

        viewModel.address.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.currentAddress.text = it
            }
        }

        viewModel.filter.observe(viewLifecycleOwner) {
            viewModel.sort(isConnected, demo)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCountersHistory(isConnected, demo)
        viewModel.getAddress(isConnected, demo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && data.extras != null) {
            if (requestCode == COUNTER_FILTER_DIALOG_RETURN_CODE && resultCode == Activity.RESULT_OK && data.extras?.containsKey(
                    "counterFilter"
                ) == true
            ) {
                viewModel.updateFilter(data.extras!!["counterFilter"] as CounterFilter)
            }
        }

    }
}
