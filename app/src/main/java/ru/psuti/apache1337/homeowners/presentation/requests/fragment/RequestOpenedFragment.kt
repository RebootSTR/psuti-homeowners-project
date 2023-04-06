package ru.psuti.apache1337.homeowners.presentation.requests.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.databinding.FragmentOpenedRequestBinding
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestStatus
import ru.psuti.apache1337.homeowners.presentation.requests.viewmodel.RequestViewModel
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class RequestOpenedFragment : Fragment() {

    private var _binding: FragmentOpenedRequestBinding? = null
    private val binding get() = _binding!!

    private val args: ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestOpenedFragmentArgs by navArgs()
    private val viewModel: RequestViewModel by viewModels()

    private lateinit var navController: NavController
    private var loadedFile: File? = null

    private val onFileButtonClickListener = { fileButton: View ->
        fileButton.isEnabled = false
        val fileName = args.request.fileName
        if (loadedFile == null) {
            viewModel.saveFile(fileName)
                .observe(viewLifecycleOwner, saveFileObserver)
        } else {
            openFile(loadedFile!!)
            fileButton.isEnabled = true
        }
    }

    private val onChangeButtonClickListener = { changeButton: View ->
        val request = args.request
        val action =
            ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestOpenedFragmentDirections.actionOpenedRequestToRequestFragment()
        action.toCreateFragment = request
        navController.navigate(action)
    }

    private val saveFileObserver = { it: Response ->
        when (it) {
            is Response.Success<*> -> {
                try {
                    val fileName = args.request.fileName
                    val file = saveFile(it.payload as ResponseBody, fileName)
                    loadedFile = file
                    openFile(file)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            is Response.Failure -> {
                Toast.makeText(
                    requireContext(),
                    "${it.error}\n" +
                            "Попробуйте еще раз",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.fileButton.isEnabled = true
    }

    private fun saveFile(responseBody: ResponseBody, fileName: String): File {
        val downloads =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, fileName)
        val fos = FileOutputStream(file)
        fos.write(responseBody.bytes())
        fos.close()
        return file
    }

    private fun openFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uri,
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        )
        intent.flags =
            Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Открытие файла не удалось",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenedRequestBinding.inflate(inflater, container, false)
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillFields()
    }

    private fun fillFields() {
        val request: RequestModel = args.request
        viewModel.profile.observe(viewLifecycleOwner) {
            with(binding) {
                lastName.text = it.lastName
                firstName.text = it.firstName
                middleName.text = it.middleName
                phone.text = it.phone
                email.text = it.email
                city.text = it.city
                street.text = it.street
                house.text = it.house
                building.text = it.building
                room.text = it.room
                theme.text = request.theme
                binding.request.text = request.request
                if (request.fileName != "") {
                    fileButton.isVisible = true
                    fileButton.text = getString(R.string.file_name).format(request.fileName)
                    fileButton.setOnClickListener(onFileButtonClickListener)
                }
                if (request.status == RequestStatus.NEW) {
                    changeButton.isVisible = true
                    changeButton.setOnClickListener(onChangeButtonClickListener)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}