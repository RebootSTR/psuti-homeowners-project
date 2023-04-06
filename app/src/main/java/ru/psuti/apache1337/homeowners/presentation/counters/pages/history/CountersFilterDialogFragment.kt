package ru.psuti.apache1337.homeowners.presentation.counters.pages.history

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.CountersFilterDialogBinding
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterDate
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterFilter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType

class CountersFilterDialogFragment : DialogFragment() {

    private var _binding: CountersFilterDialogBinding? = null
    private val binding get() = _binding

    private val viewModel: CountersHistoryViewModel by activityViewModels()

    var item: CounterFilter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            _binding = CountersFilterDialogBinding.inflate(inflater)

            val itemsDate = CounterDate.values().map { e ->
                e.value
            }
            val itemsType = CounterType.values().map { e ->
                e.value
            }

            val dateAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, itemsDate)
            val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, itemsType)

            (binding!!.inputFilterDate.editText as? AutoCompleteTextView)?.setAdapter(dateAdapter)
            (binding!!.inputFilterType.editText as? AutoCompleteTextView)?.setAdapter(typeAdapter)

            builder
                .setView(binding!!.root)
                .setTitle(R.string.filters_dialog_title)
                .setPositiveButton(R.string.dialog_apply) { _, _ ->
                    val date = when (binding!!.inputFilterDate.editText?.text.toString()) {
                        CounterDate.ALL.value -> CounterDate.ALL
                        CounterDate.LAST_MONTH.value -> CounterDate.LAST_MONTH
                        CounterDate.LAST_YEAR.value -> CounterDate.LAST_YEAR
                        CounterDate.LAST_QUARTER.value -> CounterDate.LAST_QUARTER
                        else -> CounterDate.ALL
                    }
                    val type =
                        when (binding!!.inputFilterType.editText?.text.toString()) {
                            CounterType.GAS.value -> CounterType.GAS
                            CounterType.WATER_COLD.value -> CounterType.WATER_COLD
                            CounterType.WATER_HOT.value -> CounterType.WATER_HOT
                            CounterType.ELECTRICITY.value -> CounterType.ELECTRICITY
                            CounterType.NONE.value -> CounterType.NONE
                            else -> CounterType.NONE
                        }

                    item = CounterFilter(date, type)

                    val intent = Intent().putExtra("counterFilter", item)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    dismiss()
                }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }
}