package ru.psuti.apache1337.homeowners.presentation.requests.fragment

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.requests.remote.dto.FileLoadResponseDTO
import ru.psuti.apache1337.homeowners.databinding.FragmentCreateRequestsBinding
import ru.psuti.apache1337.homeowners.domain.profile.addBlocker
import ru.psuti.apache1337.homeowners.domain.profile.validateByPredicate
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import ru.psuti.apache1337.homeowners.presentation.requests.viewmodel.RequestViewModel
import java.io.File

@AndroidEntryPoint
class RequestsCreateFragment : Fragment() {
    private var _binding: FragmentCreateRequestsBinding? = null
    private val binding get() = _binding!!

    private val args: ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestsCreateFragmentArgs by navArgs()
    private val viewModel: RequestViewModel by viewModels()

    private var editMode = false
    private var fileName: String = ""

    private val sendButtonClickListener = { _: View ->
        val resultObserver = { it: Response ->
            when (it) {
                is Response.Success<*> -> {
                    Toast.makeText(requireContext(), "Отправлено", Toast.LENGTH_SHORT).show()
                    binding.clearFields()
                }
                is Response.Failure -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
            binding.setFieldsIsEnabled(true)
        }

        if (editMode) {
            viewModel.editRequest(
                binding.theme.editText?.text.toString(),
                binding.request.editText?.text.toString(),
                fileName,
                args.request!!.id!!
            ).observe(viewLifecycleOwner, resultObserver)
        } else {
            viewModel.sendRequest(
                binding.theme.editText?.text.toString(),
                binding.request.editText?.text.toString(),
                fileName
            ).observe(viewLifecycleOwner, resultObserver)
        }

        binding.setFieldsIsEnabled(false)
    }

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    openFileActivity.launch("*/*")
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(
                        requireContext(),
                        "Необходимо разрешение к файлам. Выдайте его в настройках",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Без этого разрешения, вы не сможете прикрепить файл",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private val openFileActivity = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            val file = it.toCacheFile()
            val fileName = it.toFileName() ?: file.name

            viewModel.loadFile(file, fileName).observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Success<*> -> {
                        binding.fileAttachButton.text =
                            getString(R.string.file_name).format(fileName)
                        this.fileName = (response.payload as FileLoadResponseDTO).message
                    }
                    is Response.Failure -> {
                        Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        viewModel.sendButtonBlocker.removeError(binding.fileAttachButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillProfile()

        val request = args.request
        if (request != null) {
            enableEditMode(request)
        }

        setValidators()

        binding.sendButton.setOnClickListener(sendButtonClickListener)

        binding.fileAttachButton.setOnClickListener {
            viewModel.sendButtonBlocker.addError(it) // блокировака кнопки
            permission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.fileRemoveButton.setOnClickListener {
            fileName = ""
            binding.fileAttachButton.setText(R.string.attach_file)
        }
    }

    private fun enableEditMode(request: RequestModel) {
        fillFields(request)
        binding.editModeLayout.isVisible = true
        editMode = true
        binding.editModeDisableButton.setOnClickListener() {
            disableEditMode()
        }
    }

    private fun disableEditMode() {
        editMode = false
        binding.editModeLayout.isVisible = false
    }

    private fun fillFields(request: RequestModel) {
        binding.theme.editText?.setText(request.theme)
        binding.request.editText?.setText(request.request)
        if (request.fileName != "") {
            fileName = request.fileName
            binding.fileAttachButton.text =
                getString(R.string.file_name).format(request.fileName)
        }
    }

    private fun Uri.toCacheFile(): File {
        val inputStream = requireContext().contentResolver.openInputStream(this)
        val mimeType = requireContext().contentResolver.getType(this)
        val fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val file: File = File.createTempFile("TEMP-FILE-", ".$fileType")
        inputStream?.let { stream ->
            file.writeBytes(stream.readBytes())
        }
        return file
    }

    private fun Uri.toFileName(): String? {
        var result: String? = null
        val cursor = requireContext().contentResolver.query(
            this,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            result = it.getString(index)
            it.close()
        }
        return result
    }

    private fun FragmentCreateRequestsBinding.clearFields() {
        theme.apply {
            editText?.setText("")
        }
        request.apply {
            editText?.setText("")
        }
        fileAttachButton.setText(R.string.attach_file)
        fileName = ""
        editModeLayout.isVisible = false
        editMode = false
    }

    private fun FragmentCreateRequestsBinding.setFieldsIsEnabled(isEnabled: Boolean) {
        theme.isEnabled = isEnabled
        request.isEnabled = isEnabled
        sendButton.isEnabled = isEnabled
        fileAttachButton.isEnabled = isEnabled
    }

    private fun setValidators() {
        binding.theme.validateByPredicate(
            { it.length in 1..256 },
            viewModel.sendButtonBlocker,
            "Не более 256 символов"
        )
        binding.request.validateByPredicate(
            { it.isNotEmpty() },
            viewModel.sendButtonBlocker,
            "Обращение не должно быть пустым"
        )

        binding.sendButton.addBlocker(viewModel.sendButtonBlocker, viewLifecycleOwner)
    }

    private fun fillProfile() {
        viewModel.profile.observe(viewLifecycleOwner) {
            binding.lastName.text = it.lastName
            binding.firstName.text = it.firstName
            binding.middleName.text = it.middleName
            binding.phone.text = it.phone
            binding.email.text = it.email
            binding.city.text = it.city
            binding.street.text = it.street
            binding.house.text = it.house
            binding.building.text = it.building
            binding.room.text = it.room
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}