package ru.psuti.apache1337.homeowners.presentation.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.databinding.FragmentProfileBinding
import ru.psuti.apache1337.homeowners.domain.profile.addBlocker
import ru.psuti.apache1337.homeowners.domain.profile.model.ProfileModel
import ru.psuti.apache1337.homeowners.domain.profile.validateByRegex
import ru.psuti.apache1337.homeowners.presentation.profile.viewmodel.ProfileViewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        navController = findNavController()
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backendVersion.text = viewModel.backendVersion

        binding.editProfileButton.setOnClickListener {
            binding.setIsFieldsEditable(true)
            binding.switchButtons()
        }

        binding.saveChangesButton.isVisible = false
        binding.saveChangesButton.setOnClickListener {
            saveChanges()
        }

        binding.logoutButton.setOnClickListener {
            val dialog = LogoutDialogFragment()
            dialog.show(childFragmentManager, "logout")
        }

        viewModel.isLogoutAccepted.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.logout()
            }
        }

        viewModel.isLogout.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success<*> -> {
                    navController.navigate(ru.psuti.apache1337.homeowners.presentation.profile.fragment.ProfileFragmentDirections.actionProfileFragmentToAuthFragment())
                }
                is Response.Failure -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.profile.observe(viewLifecycleOwner) {
            binding.fillFields(it)
        }

        setValidators()
    }

    private fun setValidators() {
        binding.lastName.validateByRegex(
            "[а-яА-ЯёЁa-zA-Z- ]{1,256}".toRegex(),
            viewModel.buttonBlocker
        )
        binding.firstName.validateByRegex(
            "[а-яА-ЯёЁa-zA-Z- ]{1,256}".toRegex(),
            viewModel.buttonBlocker
        )
        binding.middleName.validateByRegex(
            "[а-яА-ЯёЁa-zA-Z- ]{1,256}".toRegex(),
            viewModel.buttonBlocker
        )

        val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (9__) ___ __ __")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        val watcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumber.editText!!)

        binding.email.validateByRegex(
            "[a-zA-Z_\\d-]+@[a-zA-Z_-]+\\.[a-zA-Z_-]+".toRegex(),
            viewModel.buttonBlocker
        )
        binding.city.validateByRegex("[а-яА-яёЁ\\- ]{1,256}".toRegex(), viewModel.buttonBlocker)
        binding.street.validateByRegex(
            "[\\dа-яА-яёЁ\\- ]{1,128}".toRegex(),
            viewModel.buttonBlocker
        )
        binding.house.validateByRegex("[\\d/-]{1,8}".toRegex(), viewModel.buttonBlocker)
        binding.building.validateByRegex("[А-Яа-яёЁ0-9]{0,8}".toRegex(), viewModel.buttonBlocker)
        binding.room.validateByRegex("\\d+".toRegex(), viewModel.buttonBlocker)

        binding.saveChangesButton.addBlocker(viewModel.buttonBlocker, viewLifecycleOwner)
    }

    private fun saveChanges(): Unit {
        with(binding) {
            val profile = toProfileModel()

            setIsFieldsEditable(false)
            viewModel.save(profile).observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success<*> -> {
                        Toast.makeText(
                            requireContext(),
                            "Сохранено",
                            Toast.LENGTH_SHORT
                        ).show()
                        switchButtons()
                    }
                    is Response.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            it.error,
                            Toast.LENGTH_SHORT
                        ).show()
                        setIsFieldsEditable(true)
                    }
                }
            }
        }
    }

    private fun FragmentProfileBinding.switchButtons() {
        saveChangesButton.isVisible = !saveChangesButton.isVisible
        editProfileButton.isVisible = !editProfileButton.isVisible
        saveChangesButton.isEnabled = true
        editProfileButton.isEnabled = true
    }

    private fun FragmentProfileBinding.setIsFieldsEditable(isEnabled: Boolean) {
        lastName.isEnabled = isEnabled
        firstName.isEnabled = isEnabled
        middleName.isEnabled = isEnabled
        email.isEnabled = isEnabled
        city.isEnabled = isEnabled
        street.isEnabled = isEnabled
        house.isEnabled = isEnabled
        building.isEnabled = isEnabled
        room.isEnabled = isEnabled
        saveChangesButton.isEnabled = isEnabled
    }

    private fun FragmentProfileBinding.toProfileModel(): ProfileModel {
        return ProfileModel(
            lastName.editText?.text.toString(),
            firstName.editText?.text.toString(),
            middleName.editText?.text.toString(),
            phoneNumber.editText?.text.toString(),
            email.editText?.text.toString(),
            city.editText?.text.toString(),
            street.editText?.text.toString(),
            house.editText?.text.toString(),
            building.editText?.text.toString(),
            room.editText?.text.toString()
        )
    }

    private fun FragmentProfileBinding.fillFields(profileModel: ProfileModel) {
        lastName.editText?.setText(profileModel.lastName)
        firstName.editText?.setText(profileModel.firstName)
        middleName.editText?.setText(profileModel.middleName)
        phoneNumber.editText?.setText(profileModel.phone)
        email.editText?.setText(profileModel.email)
        city.editText?.setText(profileModel.city)
        street.editText?.setText(profileModel.street)
        house.editText?.setText(profileModel.house)
        building.editText?.setText(profileModel.building)
        room.editText?.setText(profileModel.room)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}