package ru.psuti.apache1337.homeowners.presentation.counters.pages.apply

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.CounterAddDialogBinding
import ru.psuti.apache1337.homeowners.domain.counters.ADD_COUNTER_DIALOG_RETURN_CODE
import ru.psuti.apache1337.homeowners.domain.counters.EDIT_COUNTER_DIALOG_RETURN_CODE
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType
import java.util.*


class AddCounterDialogFragment : DialogFragment() {


    private val item = MutableLiveData<CounterEntry>()
    private var counter: Counter? = null

    private var _binding: CounterAddDialogBinding? = null
    private val binding get() = _binding

    private val viewModel: CountersApplyViewModel by activityViewModels()

    private var isCounterTypeEmpty = true
    private var isCounterValueEmpty = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            _binding = CounterAddDialogBinding.inflate(inflater)

            val fieldType = binding!!.inputCounterType
            val fieldPrevious = binding!!.inputPreviousCounters
            val fieldCurrent = binding!!.inputCurrentCounters



            if (targetRequestCode == EDIT_COUNTER_DIALOG_RETURN_CODE) {
                counter =
                    viewModel.countersList.value!!.find { e -> e.name == fieldType.editText?.text.toString() }
                val currentEntry = viewModel.counterEntriesList.value?.find { e -> e.id == tag }
                binding!!.inputCounterType.editText?.text =
                    SpannableStringBuilder(currentEntry?.counter?.name)
                binding!!.inputCounterType.isEnabled = false
                binding!!.inputCurrentCounters.editText?.text =
                    SpannableStringBuilder(currentEntry?.current)
                fieldPrevious.editText?.text =
                    SpannableStringBuilder(if (currentEntry?.counter?.type == CounterType.ELECTRICITY) "${currentEntry.counter.prev} кВт/ч" else "${currentEntry?.counter?.prev} м. куб.")
                isCounterTypeEmpty = false
                displayPrevValue()
            }

            if (targetRequestCode == ADD_COUNTER_DIALOG_RETURN_CODE && tag != "empty") {
                counter = viewModel.countersList.value!!.find { e -> e.name == tag }
                binding!!.inputCounterType.editText?.text = SpannableStringBuilder(counter?.name)
                binding!!.inputCounterType.isEnabled = false
                isCounterTypeEmpty = false
                displayPrevValue()
            }

            fieldCurrent.editText?.addTextChangedListener { text ->
                if (text.toString() == "") {
                    isCounterValueEmpty = true
                    fieldCurrent.isErrorEnabled = true
                    fieldCurrent.error = "Необходимо ввести показания"
                } else {
                    fieldCurrent.isErrorEnabled = false
                    isCounterValueEmpty = false
                }
            }

            fieldType.editText?.addTextChangedListener { text ->
                if (text.toString() == "") {
                    fieldType.isErrorEnabled = true
                    fieldType.error = "Необходимо выбрать счетчик"
                    isCounterTypeEmpty = true
                } else {
                    fieldType.isErrorEnabled = false
                    isCounterTypeEmpty = false

                    counter = viewModel.countersList.value!!.find { e -> e.name == text.toString() }
                    displayPrevValue()
                }
            }

            val items =
                viewModel.countersList.value!!.filter { e -> !e.alreadyUsed }.map { e -> e.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)

            (fieldType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            builder
                .setView(binding!!.root)
                .setTitle(if (targetRequestCode == ADD_COUNTER_DIALOG_RETURN_CODE) "Добавить показания" else "Редактировать показания")
                .setPositiveButton("Применить") { _, _ ->
                    dismiss()
                }
                .setNegativeButton("Отмена") { _, _ ->
                    dismiss()
                }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun displayPrevValue() {
        val fieldType = binding!!.inputCounterType
        val fieldPrevious = binding!!.inputPreviousCounters

        if (fieldType.editText?.text.toString() != "" && counter != null) {

            if (counter?.prev != null) {
                fieldPrevious.editText?.text =
                    SpannableStringBuilder(if (counter?.type == CounterType.ELECTRICITY) "${counter?.prev} кВт/ч" else "${counter?.prev} м. куб.")
            }
        }
    }

    private fun isCounterValueValid(value: String): Boolean {
        val regex = "[0-9]{1,6}[.]?[0-9]?".toRegex()
        return if (counter != null && counter!!.prev != null) {
            regex.matches(value) && value.toDouble() >= counter!!.prev!!
        } else {
            regex.matches(value)
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog?

        if (dialog != null) {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                var wantToCloseDialog = false

                val fieldType = binding!!.inputCounterType
                val fieldCurrent = binding!!.inputCurrentCounters

                if (!isCounterTypeEmpty && !isCounterValueEmpty) {

                    if (!isCounterValueValid(fieldCurrent.editText?.text.toString())) {
                        fieldCurrent.error = "Неверно введено значение"
                    } else {
                        val counter =
                            viewModel.countersList.value!!.find { e -> e.name == fieldType.editText?.text.toString() }

                        item.value = CounterEntry(
                            id = if (targetRequestCode == ADD_COUNTER_DIALOG_RETURN_CODE) UUID.randomUUID()
                                .toString() else tag!!,
                            counter = counter!!,
                            current = fieldCurrent.editText?.text.toString(),
                        )

                        viewModel.setCounterAlreadyUsed(counter.name, true)

                        val intent = Intent().putExtra("counterEntry", item.value)
                        targetFragment?.onActivityResult(
                            targetRequestCode,
                            Activity.RESULT_OK,
                            intent
                        )
                        wantToCloseDialog = true
                    }
                }

                if (wantToCloseDialog) {
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }
}
