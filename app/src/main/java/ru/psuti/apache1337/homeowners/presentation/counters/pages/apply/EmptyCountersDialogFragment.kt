package ru.psuti.apache1337.homeowners.presentation.counters.pages.apply

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.psuti.apache1337.homeowners.R

class EmptyCountersDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(R.string.counter_empty_dialog_title)
                setTitle(R.string.counter_empty_dialog_message)
                setPositiveButton(R.string.dialog_ok) { _, _ ->
                    targetFragment?.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        Intent()
                    )
                }
                setNegativeButton(R.string.dialog_cancel) { _, _ ->

                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}