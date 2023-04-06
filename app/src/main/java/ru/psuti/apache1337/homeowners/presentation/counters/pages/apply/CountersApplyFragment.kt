package ru.psuti.apache1337.homeowners.presentation.counters.pages.apply

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.CounterItemBinding
import ru.psuti.apache1337.homeowners.databinding.FragmentCountersApplyBinding
import ru.psuti.apache1337.homeowners.domain.counters.ADD_COUNTER_DIALOG_RETURN_CODE
import ru.psuti.apache1337.homeowners.domain.counters.EDIT_COUNTER_DIALOG_RETURN_CODE
import ru.psuti.apache1337.homeowners.domain.counters.EMPTY_COUNTERS_DIALOG_RETURN_CODE
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType
import ru.psuti.apache1337.homeowners.presentation.counters.CountersFragmentDirections


class CountersApplyFragment(private val demo: Boolean) : Fragment() {
    private var _binding: FragmentCountersApplyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CountersApplyViewModel by activityViewModels()

    private lateinit var dialog: AddCounterDialogFragment

    private lateinit var emptyCountersDialog: EmptyCountersDialogFragment

    //TODO: Restructurize


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentCountersApplyBinding.inflate(inflater, container, false)

        dialog = AddCounterDialogFragment()

        emptyCountersDialog = EmptyCountersDialogFragment()

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected = activeNetwork?.isConnected == true

        init(isConnected, demo)

        viewModel.countersList.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.flagReRenderPrev.observe(viewLifecycleOwner) { _ ->
                    binding.countersPrevContainer.removeAllViews()
                    for (item in viewModel.countersList.value!!) {
                        if (!item.alreadyUsed) {
                            addPreviousCounterView(inflater, item)
                        }
                    }
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.flagClearCurr.observe(viewLifecycleOwner) {
            binding.countersCurrContainer.removeAllViews()
        }

        viewModel.address.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.currentAddress.text = it
            }
        }

        binding.buttonAddCounter.setOnClickListener {

            if (viewModel.countersList.value == null || viewModel.countersList.value!!.isEmpty()) {
                emptyCountersDialog.setTargetFragment(this, EMPTY_COUNTERS_DIALOG_RETURN_CODE)
                emptyCountersDialog.show(requireFragmentManager(), "empty")
            } else {
                dialog.setTargetFragment(this, ADD_COUNTER_DIALOG_RETURN_CODE)
                dialog.show(requireFragmentManager(), "empty")
            }
        }

        binding.buttonSend.setOnClickListener {
            viewModel.sendEntries(isConnected, demo)
        }

        viewModel.counterEntriesList.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                binding.countersCurrContainer.removeAllViews()
                list.forEach {
                    addCurrentCounterView(inflater, it)
                }
            }
        }

        return binding.root
    }

    private fun init(isConnected: Boolean, demo: Boolean) {
        if (isConnected) {
            viewModel.getCounters(isConnected, demo)
            viewModel.getAddress(isConnected, demo)
            binding.buttonSend.isEnabled = true
        } else {
            viewModel.setError(getString(R.string.error_no_connection))
            binding.buttonSend.isEnabled = false
        }
        if (demo) {
            binding.buttonSend.isEnabled = false
        }
    }

    private fun addPreviousCounterView(inflater: LayoutInflater, counter: Counter) {
        val view = CounterItemBinding.inflate(inflater, binding.countersPrevContainer, false)
        val image = when (counter.type) {
            CounterType.WATER_HOT -> R.drawable.ic_water_hot
            CounterType.WATER_COLD -> R.drawable.ic_water_cold
            CounterType.GAS -> R.drawable.ic_gas
            CounterType.ELECTRICITY -> R.drawable.ic_electro
            else -> R.drawable.ic_gas
        }

        view.counterIcon.setImageResource(image)
        view.counterDelete.visibility = View.GONE

        view.counterType.text = counter.name
        view.counterValue.text =
            if (counter.type == CounterType.ELECTRICITY) "${counter.prev} кВт/ч" else "${counter.prev} м. куб."

        view.root.setOnLongClickListener {
            dialog.setTargetFragment(this, ADD_COUNTER_DIALOG_RETURN_CODE)
            dialog.show(requireFragmentManager(), counter.name)
            return@setOnLongClickListener true
        }

        binding.countersPrevContainer.addView(view.root)
    }

    private fun addCurrentCounterView(inflater: LayoutInflater, entry: CounterEntry) {
        val view = CounterItemBinding.inflate(inflater, binding.countersCurrContainer, false)

        val image = when (entry.counter.type) {
            CounterType.WATER_HOT -> R.drawable.ic_water_hot
            CounterType.WATER_COLD -> R.drawable.ic_water_cold
            CounterType.GAS -> R.drawable.ic_gas
            CounterType.ELECTRICITY -> R.drawable.ic_electro
            else -> R.drawable.ic_gas

        }

        view.root.setOnLongClickListener {
            dialog.setTargetFragment(this, EDIT_COUNTER_DIALOG_RETURN_CODE)
            dialog.show(requireFragmentManager(), entry.id)
            return@setOnLongClickListener true
        }

        view.counterIcon.setImageResource(image)

        view.counterType.text = entry.counter.name
        view.counterValue.text =
            if (entry.counter.type == CounterType.ELECTRICITY) "${entry.current} кВт/ч" else "${entry.current} м. куб."

        view.counterDelete.setOnClickListener {
            viewModel.removeCounterEntry(entry.id)
            viewModel.setCounterAlreadyUsed(entry.counter.name, false)
        }

        binding.countersCurrContainer.addView(view.root)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && data.extras != null) {
            if (requestCode == ADD_COUNTER_DIALOG_RETURN_CODE && resultCode == Activity.RESULT_OK && data.extras?.containsKey(
                    "counterEntry"
                ) == true
            ) {
                viewModel.addCounterEntry(data.extras!!["counterEntry"] as CounterEntry)
            }
            if (requestCode == EDIT_COUNTER_DIALOG_RETURN_CODE && resultCode == Activity.RESULT_OK && data.extras?.containsKey(
                    "counterEntry"
                ) == true
            ) {
                viewModel.editCounterEntry(data.extras!!["counterEntry"] as CounterEntry)
            }
        }
        if (requestCode == EMPTY_COUNTERS_DIALOG_RETURN_CODE) {
            findNavController().navigate(CountersFragmentDirections.actionIndicationsFragmentToRequestFragment2())
        }

    }
}