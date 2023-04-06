package ru.psuti.apache1337.homeowners.presentation.registration

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.FragmentRegistrationBinding
import ru.psuti.apache1337.homeowners.domain.model.ResponseState
import ru.psuti.apache1337.homeowners.domain.registration.model.User
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.util.*

private const val TWENTY_SECOND_IN_MS: Long = 20000

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private enum class State {
        REGISTRATION,
        CONFIRM_CODE
    }

    private var state = State.REGISTRATION
    private var timerTime: Long = TWENTY_SECOND_IN_MS
    private var timer: Timer? = null
    private var exitTime = Date()

    private inner class Timer : CountDownTimer(timerTime, 10) {

        override fun onTick(millisUntilFinished: Long) {
            timerTime = millisUntilFinished
            if (_binding != null) {
                binding.resendCodeButton.text = getString(
                    R.string.button_resend_code_text_until_resend,
                    millisUntilFinished / 1000
                )
            } else {
                cancel()
            }
        }

        override fun onFinish() {
            binding.resendCodeButton.isEnabled = true
            binding.resendCodeButton.text =
                getString(R.string.button_resend_code_text_resend_code)
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.start()
    }

    private val registrationViewModel: RegistrationViewModel by viewModels()
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val args: ru.psuti.apache1337.homeowners.presentation.registration.RegistrationFragmentArgs by navArgs()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fioRegex = "[^а-яА-яёЁa-zA-Z-]+".toRegex()

        binding.firstName.editText?.doAfterTextChanged { s ->
            validate(binding.firstName, fioRegex, "Можно вводить только русские и английские буквы")
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.firstName.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.lastName.editText?.doAfterTextChanged { s ->
            validate(binding.lastName, fioRegex, "Можно вводить только русские и английские буквы")
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.lastName.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.middleName.editText?.doAfterTextChanged { s ->
            validate(
                binding.middleName,
                fioRegex,
                "Можно вводить только русские и английские буквы"
            )
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.middleName.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }


        val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (9__) ___ __ __")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        val watcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumberTextEdit)

        binding.phoneNumber.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.email.editText?.doAfterTextChanged { _ ->
            binding.email.error = ""
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.email.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }



        binding.city.editText?.doAfterTextChanged { s ->
            validate(binding.city, "[^а-яА-яёЁ -]+".toRegex(), "Можно вводить только русские буквы")
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.city.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.street.editText?.doAfterTextChanged { s ->
            validate(
                binding.street,
                "[^а-яА-яёЁ0-9\\s-]+".toRegex(),
                "Можно вводить только русские буквы"
            )
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.street.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.house.editText?.doAfterTextChanged {
            binding.house.error = ""
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.house.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.korpus.editText?.doAfterTextChanged { s ->
            validate(
                binding.korpus,
                "[^А-Яа-яёЁ0-9]*+".toRegex(),
                "Можно вводить только русские буквы и цифры"
            )
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.korpus.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }

        binding.apartment.editText?.doAfterTextChanged {
            binding.apartment.error = ""
            binding.registrationButton.isEnabled = isFieldsRight()
        }

        binding.apartment.editText?.setOnFocusChangeListener { _, _ ->
            validateFields()
        }


        binding.registrationButton.setOnClickListener {
            if (checkInternetConnection()) {
                val user = User(
                    lastName = binding.lastName.editText?.text.toString(),
                    firstName = binding.firstName.editText?.text.toString(),
                    middleName = binding.middleName.editText?.text.toString(),
                    phoneNumber = binding.phoneNumber.editText?.text.toString(),
                    email = binding.email.editText?.text.toString(),
                    city = binding.city.editText?.text.toString(),
                    street = binding.street.editText?.text.toString(),
                    house = binding.house.editText?.text.toString().toInt(),
                    korpus = binding.korpus.editText?.text.toString(),
                    apartment = binding.apartment.editText?.text.toString().toInt()
                )
                timer?.cancel()
                timerTime = TWENTY_SECOND_IN_MS
                registrationViewModel.sendCode(user)
            } else {
                val toast = Toast.makeText(context, "Нет сети", Toast.LENGTH_LONG)
                toast.show()
            }

        }

        binding.phoneNumber.editText?.setText(args.phone)

        registrationViewModel.code.observe(viewLifecycleOwner, {
            if (registrationViewModel.isCodeShowed.value == false) {
                val toast = Toast.makeText(context, it.toString(), Toast.LENGTH_LONG)
                toast.show()
            }
        })

        registrationViewModel.registrationResponseState.observe(viewLifecycleOwner, { response ->
            when (response) {
                is ResponseState.Loading -> {
                    binding.lastName.isEnabled = false
                    binding.firstName.isEnabled = false
                    binding.middleName.isEnabled = false
                    binding.phoneNumber.isEnabled = false
                    binding.email.isEnabled = false
                    binding.city.isEnabled = false
                    binding.street.isEnabled = false
                    binding.house.isEnabled = false
                    binding.korpus.isEnabled = false
                    binding.apartment.isEnabled = false
                    binding.registrationButton.isEnabled = false
                }
                is ResponseState.Success -> {
                    binding.registrationButton.isEnabled = true
                    binding.code.isEnabled = true
                    if (state == State.REGISTRATION) {
                        state = State.CONFIRM_CODE

                        timer?.cancel()
                        binding.code.editText?.setText("")

                        initAnimationFromRegistrationLayoutToConfirmLayout(250)

                        binding.sendCodeDescription.text =
                            getString(
                                R.string.text_view_send_code_description,
                                binding.phoneNumber.editText?.text
                            )
                        startTimer()
                    }
                }
                is ResponseState.Failure -> {
                    binding.lastName.isEnabled = true
                    binding.firstName.isEnabled = true
                    binding.middleName.isEnabled = true
                    binding.phoneNumber.isEnabled = true
                    binding.email.isEnabled = true
                    binding.city.isEnabled = true
                    binding.street.isEnabled = true
                    binding.house.isEnabled = true
                    binding.korpus.isEnabled = true
                    binding.apartment.isEnabled = true
                    binding.registrationButton.isEnabled = true

                    when (response.message) {
                        else -> {
                            val toast = Toast.makeText(
                                context,
                                "Произошла непредвиденная ошибка",
                                Toast.LENGTH_LONG
                            )
                            toast.show()
                        }
                    }
                }
            }
        })

        binding.backButton.setOnClickListener {
            binding.lastName.isEnabled = true
            binding.firstName.isEnabled = true
            binding.middleName.isEnabled = true
            binding.phoneNumber.isEnabled = true
            binding.email.isEnabled = true
            binding.city.isEnabled = true
            binding.street.isEnabled = true
            binding.house.isEnabled = true
            binding.korpus.isEnabled = true
            binding.apartment.isEnabled = true


            binding.code.isEnabled = false
            state = State.REGISTRATION

            initAnimationFromConfirmLayoutToRegistrationLayout(250)
        }

        binding.code.editText?.doAfterTextChanged { s ->
            s?.let {
                binding.confirmButton.isEnabled =
                    binding.termOfUseCheckbox.isChecked && registrationViewModel.checkCodeLength(
                        s.toString()
                    )
            }
        }


        binding.resendCodeButton.setOnClickListener {
            if (checkInternetConnection()) {
                binding.resendCodeButton.isEnabled = false
                timerTime = TWENTY_SECOND_IN_MS
                startTimer()
                val user = User(
                    lastName = binding.lastName.editText?.text.toString(),
                    firstName = binding.firstName.editText?.text.toString(),
                    middleName = binding.middleName.editText?.text.toString(),
                    phoneNumber = binding.phoneNumber.editText?.text.toString(),
                    email = binding.email.editText?.text.toString(),
                    city = binding.city.editText?.text.toString(),
                    street = binding.street.editText?.text.toString(),
                    house = binding.house.editText?.text.toString().toInt(),
                    korpus = binding.korpus.editText?.text.toString(),
                    apartment = binding.apartment.editText?.text.toString().toInt()
                )
                registrationViewModel.sendCode(user)
            } else {
                val toast = Toast.makeText(context, "Нет сети", Toast.LENGTH_LONG)
                toast.show()
            }

        }

        binding.termOfUseCheckbox.setOnClickListener { checkbox ->
            binding.confirmButton.isEnabled =
                (checkbox as CheckBox).isChecked && registrationViewModel.checkCodeLength(binding.code.editText?.text.toString())
        }

        binding.termOfUseTextView.text = getTermOfUseTextWithLinks()
        binding.termOfUseTextView.setLinkTextColor(Color.BLUE)
        binding.termOfUseTextView.movementMethod = LinkMovementMethod.getInstance()

        binding.confirmButton.setOnClickListener {
            val code = binding.code.editText?.text.toString().toInt()

            if (checkInternetConnection()) registrationViewModel.checkCode(code)
            else {
                val toast = Toast.makeText(context, "Нет сети", Toast.LENGTH_LONG)
                toast.show()
            }
        }
        registrationViewModel.logInResponseState.observe(viewLifecycleOwner,
            { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        binding.code.isEnabled = false
                        binding.confirmButton.isEnabled = false
                    }
                    is ResponseState.Success -> {
                        binding.code.isEnabled = true
                        binding.confirmButton.isEnabled = true
                        val toast = Toast.makeText(
                            context,
                            "Вы успешно зарегистрировались",
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                        navController.navigate(ru.psuti.apache1337.homeowners.presentation.registration.RegistrationFragmentDirections.actionAuthFragmentToIndicationsFragment())

                    }
                    is ResponseState.Failure -> {
                        binding.code.isEnabled = true
                        binding.confirmButton.isEnabled = true
                        when (response.message) {
                            "Password wasn't valid" -> binding.code.error =
                                getString(R.string.error_code)
                            else -> {
                                val toast = Toast.makeText(
                                    context,
                                    "Произошла непредвиденная ошибка",
                                    Toast.LENGTH_LONG
                                )
                                toast.show()
                            }
                        }

                    }
                }
            }
        )

        binding.supportTextView.text = getSupportTextWithLinks()
        binding.supportTextView.setLinkTextColor(Color.BLUE)
        binding.supportTextView.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun onStart() {
        super.onStart()
        binding.sendCodeDescription.text =
            getString(R.string.text_view_send_code_description, binding.phoneNumber.editText?.text)
    }

    override fun onResume() {
        super.onResume()
        binding.lastName.editText?.setText(registrationViewModel.filledLastName)
        binding.firstName.editText?.setText(registrationViewModel.filledFirstName)
        binding.middleName.editText?.setText(registrationViewModel.filledMiddleName)
        binding.email.editText?.setText(registrationViewModel.filledEmail)
        binding.city.editText?.setText(registrationViewModel.filledCity)
        binding.street.editText?.setText(registrationViewModel.filledStreet)
        binding.house.editText?.setText(registrationViewModel.filledHouse)
        binding.korpus.editText?.setText(registrationViewModel.filledKorpus)
        binding.apartment.editText?.setText(registrationViewModel.filledApartment)
        binding.code.editText?.setText(registrationViewModel.filledCode)
        changeStateTo(state)
    }


    override fun onPause() {
        super.onPause()
        registrationViewModel.rememberFields(
            lastName = binding.lastName.editText?.text.toString(),
            firstName = binding.firstName.editText?.text.toString(),
            middleName = binding.middleName.editText?.text.toString(),
            email = binding.email.editText?.text.toString(),
            city = binding.city.editText?.text.toString(),
            street = binding.street.editText?.text.toString(),
            house = binding.house.editText?.text.toString(),
            korpus = binding.korpus.editText?.text.toString(),
            apartment = binding.apartment.editText?.text.toString(),
            code = binding.code.editText?.text.toString()
        )
        exitTime = Date()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAnimationFromRegistrationLayoutToConfirmLayout(
        animDuration: Long,
        chosenInterpolator: Interpolator = LinearInterpolator()
    ) {
        val hideLoginLayout =
            ObjectAnimator.ofFloat(
                binding.registrationLayout,
                "translationX",
                -binding.registrationLayout.width.toFloat()
            )
                .apply {
                    duration = animDuration
                    doOnEnd {
                        binding.registrationLayout.visibility = View.GONE
                    }
                    interpolator = chosenInterpolator
                }
        val showConfirmLayout = ObjectAnimator.ofFloat(
            binding.confirmLayout,
            "translationX",
            binding.registrationLayout.width.toFloat(),
            0f
        ).apply {
            duration = animDuration
            startDelay = 100
            doOnStart {
                binding.confirmLayout.visibility = View.VISIBLE
            }
            interpolator = chosenInterpolator
        }

        AnimatorSet().apply {
            playTogether(hideLoginLayout, showConfirmLayout)
            start()
        }
    }

    private fun initAnimationFromConfirmLayoutToRegistrationLayout(
        animDuration: Long,
        chosenInterpolator: Interpolator = LinearInterpolator()
    ) {
        val showLoginLayout =
            ObjectAnimator.ofFloat(
                binding.confirmLayout,
                "translationX",
                binding.confirmLayout.width.toFloat()
            )
                .apply {
                    duration = animDuration
                    doOnEnd {
                        binding.confirmLayout.visibility = View.GONE
                    }
                    interpolator = chosenInterpolator
                }
        val hideConfirmLayout = ObjectAnimator.ofFloat(
            binding.registrationLayout,
            "translationX",
            -binding.registrationLayout.width.toFloat(),
            0f
        ).apply {
            duration = animDuration
            startDelay = 100
            doOnStart {
                binding.registrationLayout.visibility = View.VISIBLE
            }
            interpolator = chosenInterpolator
        }

        AnimatorSet().apply {
            playTogether(showLoginLayout, hideConfirmLayout)
            start()
        }
    }

    private fun getSupportTextWithLinks(): SpannableString {
        val supportText = SpannableString(getString(R.string.text_view_support_text))

        val supportTextClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navController.navigate(ru.psuti.apache1337.homeowners.presentation.registration.RegistrationFragmentDirections.actionRegistrationFragmentToSupportPlaceHolderFragment())
            }
        }
        supportText.setSpan(supportTextClickableSpan, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return supportText
    }

    private fun getTermOfUseTextWithLinks(): SpannableString {
        val termsOfUseText = SpannableString(getString(R.string.text_view_terms_of_use_text))

        val termsOfUseClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    val inflater = requireActivity().layoutInflater
                    builder.apply {
                        setTitle(getString(R.string.terms_of_use))
                        setView(inflater.inflate(R.layout.dialog_terms_of_use, null))
                        setPositiveButton(getString(R.string.continue_text), null)

                    }
                    builder.create()
                }
                alertDialog?.show()
            }

        }

        val disclosurePolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    val inflater = requireActivity().layoutInflater
                    builder.apply {
                        setTitle(getString(R.string.disclosure_Policy))
                        setView(inflater.inflate(R.layout.dialog_data_processing_policy, null))
                        setPositiveButton(getString(R.string.continue_text), null)

                    }
                    builder.create()
                }
                alertDialog?.show()
            }

        }

        termsOfUseText.setSpan(termsOfUseClickableSpan, 13, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termsOfUseText.setSpan(
            disclosurePolicyClickableSpan,
            44,
            83,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return termsOfUseText
    }

    private fun changeStateTo(state: State) {
        when (state) {
            State.REGISTRATION -> {
                binding.registrationLayout.visibility = View.VISIBLE
                binding.confirmLayout.visibility = View.GONE
            }
            State.CONFIRM_CODE -> {
                binding.registrationLayout.visibility = View.GONE
                binding.confirmLayout.visibility = View.VISIBLE

                val currentTime = Date()
                if (currentTime.time - exitTime.time < timerTime) {
                    timerTime -= (currentTime.time - exitTime.time)
                    startTimer()
                } else {
                    binding.resendCodeButton.isEnabled = true
                    binding.resendCodeButton.text =
                        getString(R.string.button_resend_code_text_resend_code)
                }

                binding.confirmButton.isEnabled =
                    binding.termOfUseCheckbox.isChecked && registrationViewModel.checkCodeLength(
                        binding.code.editText?.text.toString()
                    )
            }
        }
    }

    private fun validate(textInputLayout: TextInputLayout, regex: Regex, error: String) {
        val s = textInputLayout.editText?.text ?: return
        val tempString = s.replace(regex, "")
        if (textInputLayout.editText?.text.toString() != tempString) {
            textInputLayout.editText?.setText(tempString)
            textInputLayout.editText?.setSelection(tempString.length)
            textInputLayout.error = error
        } else {
            textInputLayout.error = ""
        }
    }

    private fun isFieldsRight(): Boolean {
        val phoneNumber = binding.phoneNumber.editText?.text?.toString() ?: ""
        val isLenRight =
            binding.firstName.editText?.text?.isNotEmpty() == true && binding.lastName.editText?.text?.isNotEmpty() == true
                    && binding.middleName.editText?.text?.isNotEmpty() == true && binding.email.editText?.text?.isNotEmpty() == true
                    && binding.street.editText?.text?.isNotEmpty() == true && binding.house.editText?.text?.isNotEmpty() == true
                    && binding.apartment.editText?.text?.isNotEmpty() == true &&
                    registrationViewModel.checkPhoneLength(phoneNumber)
        val nameRegex = "^[а-яА-яёЁa-zA-Z]+(?:[-][а-яА-яёЁa-zA-Z]+)*\$".toRegex()

        val cityRegex = "^[а-яА-яёЁ]+(?:[\\s-][а-яА-яёЁ]+)*\$".toRegex()
        val streetRegex = "^[а-яА-яёЁ0-9]+(?:[\\s-][а-яА-яёЁ0-9]+)*\$".toRegex()
        var count = 0
        if (!nameRegex.matches(binding.lastName.editText!!.text)) {
            count++
        }
        if (!nameRegex.matches(binding.firstName.editText!!.text)) {
            count++
        }
        if (!nameRegex.matches(binding.middleName.editText!!.text)) {
            count++
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.editText?.text.toString())
                .matches()
        ) {
            count++
        }
        if (!cityRegex.matches(binding.city.editText!!.text)) {
            count++
        }
        if (!streetRegex.matches(binding.street.editText!!.text)) {
            count++
        }
        return count == 0 && isLenRight
    }

    private fun validateFields(): Boolean {
        val nameRegex = "^[а-яА-яёЁa-zA-Z]+(?:[-][а-яА-яёЁa-zA-Z]+)*\$".toRegex()
        val cityRegex = "^[а-яА-яёЁ]+(?:[\\s-][а-яА-яёЁ]+)*\$".toRegex()
        val streetRegex = "^[а-яА-яёЁ0-9]+(?:[\\s-][а-яА-яёЁ0-9]+)*\$".toRegex()

        var count = 0
        if (!nameRegex.matches(binding.lastName.editText!!.text) && binding.lastName.editText!!.text.isNotEmpty()) {
            binding.lastName.error =
                getString(R.string.last_name_error)
            count++
        }
        if (!nameRegex.matches(binding.firstName.editText!!.text) && binding.firstName.editText!!.text.isNotEmpty()) {
            binding.firstName.error =
                getString(R.string.first_name_error)
            count++
        }
        if (!nameRegex.matches(binding.middleName.editText!!.text) && binding.middleName.editText!!.text.isNotEmpty()) {
            binding.middleName.error =
                getString(R.string.middle_name_error)
            count++
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.editText?.text.toString())
                .matches() && binding.email.editText!!.text.isNotEmpty()
        ) {
            binding.email.error = getString(R.string.email_error)
            count++
        }
        if (!cityRegex.matches(binding.city.editText!!.text) && binding.city.editText!!.text.isNotEmpty()) {
            binding.city.error =
                getString(R.string.city_error)
            count++
        }
        if (!streetRegex.matches(binding.street.editText!!.text) && binding.street.editText!!.text.isNotEmpty()) {
            binding.street.error =
                getString(R.string.street_error)
            count++
        }
        return if (count == 0) {
            binding.lastName.error = ""
            binding.firstName.error = ""
            binding.middleName.error = ""
            binding.email.error = ""
            binding.city.error = ""
            binding.street.error = ""
            true
        } else {
            false
        }

    }

    private fun checkInternetConnection(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}