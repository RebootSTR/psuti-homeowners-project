package ru.psuti.apache1337.homeowners.presentation.profile.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.presentation.profile.viewmodel.ProfileViewModel

@AndroidEntryPoint
class LogoutDialogFragment : DialogFragment() {

    private val viewModel: ProfileViewModel by viewModels(ownerProducer = {requireParentFragment()})

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return AlertDialog.Builder(activity)
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.acceptLogout()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}