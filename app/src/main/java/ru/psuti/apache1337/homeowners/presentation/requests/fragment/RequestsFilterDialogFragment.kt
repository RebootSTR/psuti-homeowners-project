package ru.psuti.apache1337.homeowners.presentation.requests.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.RequestsFilterDialogBinding
import ru.psuti.apache1337.homeowners.domain.requests.model.DateRanges
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestStatus
import ru.psuti.apache1337.homeowners.presentation.requests.viewmodel.RequestViewModel

@AndroidEntryPoint
class RequestsFilterDialogFragment : DialogFragment() {

    private var _binding: RequestsFilterDialogBinding? = null
    private val binding get() = _binding

    private val requestViewModel: RequestViewModel by viewModels(ownerProducer = {requireParentFragment()})

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(activity)

        val inflater = requireActivity().layoutInflater

        _binding = RequestsFilterDialogBinding.inflate(inflater)

        val statuses: ArrayList<String> = ArrayList()
        statuses.add("Все")
        statuses.addAll(RequestStatus.VALUES)
        val statusAdapter = ArrayAdapter(requireContext(), R.layout.material_list_item, statuses)

        val dates = DateRanges.VALUES
        val datesAdapter = ArrayAdapter(requireContext(), R.layout.material_list_item, dates)



        (binding!!.dateSelector.editText as? AutoCompleteTextView)?.setAdapter(datesAdapter)
        (binding!!.requestStatusSelector.editText as? AutoCompleteTextView)?.setAdapter(statusAdapter)

        return builder
            .setView(binding!!.root)
            .setTitle("Фильтры")
            .setPositiveButton("Применить") { _, _ ->
                val date = binding!!.dateSelector.editText?.text.toString()
                val status = binding!!.requestStatusSelector.editText?.text.toString()

                requestViewModel.date.value = date
                requestViewModel.status.value = status
                requestViewModel.filterRequests()
            }
            .setNegativeButton("Отмена") { _, _ ->
                dismiss()
            }
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }
}