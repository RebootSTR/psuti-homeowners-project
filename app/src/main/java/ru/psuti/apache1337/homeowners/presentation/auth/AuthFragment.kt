package ru.psuti.apache1337.homeowners.presentation.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
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
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.databinding.FragmentAuthBinding
import ru.psuti.apache1337.homeowners.domain.model.ResponseState
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.util.*
import javax.inject.Inject


private const val TWENTY_SECOND_IN_MS: Long = 20000

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private enum class State {
        LOGIN,
        CONFIRM_CODE
    }

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var navController: NavController

    @Inject
    lateinit var appSharedPreferences: AppSharedPreferences

    private var state = State.LOGIN
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        navController = findNavController()

        return binding.root
    }

    private fun triggerRebirth(context: Context) {
        val packageManager: PackageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (9__) ___ __ __")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        val watcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumberTextEdit)

        binding.selectedUrl.text = getString(R.string.current_url, appSharedPreferences.backendUrl.get())
        binding.urlEditText.setText(appSharedPreferences.backendUrl.get())
        binding.saveUrlButton.setOnClickListener {
            val url = binding.urlEditText.text!!.toString()
            appSharedPreferences.backendUrl.set(url)
            view.postDelayed({triggerRebirth(requireContext())}, 100L)
        }

        binding.phoneNumberTextEdit.setText(appSharedPreferences.numberInput.get())

        binding.phoneNumberTextLayout.editText?.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (binding.continueButton.isEnabled) binding.continueButton.performClick()
                    true
                }
                else -> false
            }
        }

        binding.phoneNumberTextLayout.editText?.doAfterTextChanged { s ->
            s?.let {
                if (authViewModel.checkPhoneLength(s.toString())) {
                    binding.continueButton.isEnabled = true
                    binding.resendCodeButton.isEnabled = false

                } else {
                    binding.continueButton.isEnabled = false
                }
            }
        }

        binding.continueButton.setOnClickListener {
            appSharedPreferences
            timer?.cancel()
            timerTime = TWENTY_SECOND_IN_MS
            val phone = binding.phoneNumberTextLayout.editText?.text.toString()
            appSharedPreferences.numberInput.set(phone)

            if (checkInternetConnection()) authViewModel.sendCode(phone)
            else {
                val toast = Toast.makeText(context, "Нет сети", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        binding.demoButton.setOnClickListener {
            authViewModel.createDemoUser()
        }

        authViewModel.demoUserResponseState.observe(viewLifecycleOwner) { response ->
            if (response is ResponseState.Success) {
                navController.navigate(ru.psuti.apache1337.homeowners.presentation.auth.AuthFragmentDirections.actionAuthFragmentToIndicationsFragment())
            }
        }

        authViewModel.authResponseState.observe(viewLifecycleOwner
        ) { response ->
            when (response) {
                is ResponseState.Loading -> {
                    binding.phoneNumberTextLayout.isEnabled = false
                    binding.continueButton.isEnabled = false
                }
                is ResponseState.Success -> {
                    binding.phoneNumberTextLayout.isEnabled = true
                    binding.continueButton.isEnabled = true
                    if (state == State.LOGIN) {
                        toConfirmLayout()
                    }

                }
                is ResponseState.Failure -> {
                    binding.phoneNumberTextLayout.isEnabled = true
                    binding.continueButton.isEnabled = true
                    handleErrorsInLogInLayout(response)
                }
            }
        }

        binding.backButton.setOnClickListener {
            binding.phoneNumberTextLayout.isEnabled = true
            binding.codeTextLayout.isEnabled = false

            toLogInLayout()
        }

        binding.codeTextLayout.editText?.doAfterTextChanged { s ->
            s?.let {
                binding.confirmButton.isEnabled =
                    binding.termOfUseCheckbox.isChecked && authViewModel.checkCodeLength(s.toString())
            }
        }

        authViewModel.code.observe(viewLifecycleOwner) {
            if (authViewModel.isCodeShowed.value == false) {
                val toast = Toast.makeText(context, it.toString(), Toast.LENGTH_LONG)
                toast.show()
            }
        }

        binding.resendCodeButton.setOnClickListener {
            val phone = binding.phoneNumberTextLayout.editText?.text.toString()
            if (checkInternetConnection()) {
                authViewModel.sendCode(phone)

                binding.resendCodeButton.isEnabled = false
                timerTime = TWENTY_SECOND_IN_MS
                startTimer()
            } else {
                val toast = Toast.makeText(context, "Нет сети", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        binding.termOfUseCheckbox.setOnClickListener { checkbox ->
            binding.confirmButton.isEnabled =
                (checkbox as CheckBox).isChecked && authViewModel.checkCodeLength(binding.codeTextLayout.editText?.text.toString())
        }

        binding.termOfUseTextView.text = getTermOfUseTextWithLinks()
        binding.termOfUseTextView.setLinkTextColor(Color.BLUE)
        binding.termOfUseTextView.movementMethod = LinkMovementMethod.getInstance()

        binding.confirmButton.setOnClickListener {
            val code = binding.codeTextLayout.editText?.text.toString().toInt()
            if (checkInternetConnection()) authViewModel.checkCode(code)
            else {
                val toast = Toast.makeText(context, "Нет сети", Toast.LENGTH_LONG)
                toast.show()
            }

        }

        authViewModel.logInResponseState.observe(viewLifecycleOwner)
        { response ->
            when (response) {
                is ResponseState.Loading -> {
                    binding.codeTextLayout.isEnabled = false
                    binding.confirmButton.isEnabled = false
                }
                is ResponseState.Success -> {
                    binding.codeTextLayout.isEnabled = true
                    binding.confirmButton.isEnabled = true
                    navController.navigate(ru.psuti.apache1337.homeowners.presentation.auth.AuthFragmentDirections.actionAuthFragmentToIndicationsFragment())
                }
                is ResponseState.Failure -> {
                    binding.codeTextLayout.isEnabled = true
                    binding.confirmButton.isEnabled = true
                    handleErrorsInConfirmLayout(response)
                }
            }
        }

        binding.supportTextView.text = getSupportTextWithLinks()
        binding.supportTextView.setLinkTextColor(Color.BLUE)
        binding.supportTextView.movementMethod = LinkMovementMethod.getInstance()

        val version = appSharedPreferences.backendVersion.get()
        binding.backendVersionTextView.text = version
    }

    override fun onStart() {
        super.onStart()
        binding.sendCodeDescription.text =
            getString(
                R.string.text_view_send_code_description,
                binding.phoneNumberTextLayout.editText?.text
            )

    }

    override fun onResume() {
        super.onResume()
        binding.codeTextLayout.editText?.setText(authViewModel.filledCode)
        changeStateTo(state)
    }

    override fun onPause() {
        super.onPause()
        authViewModel.rememberFields(
            binding.codeTextLayout.editText?.text.toString()
        )
        exitTime = Date()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleErrorsInLogInLayout(response: ResponseState) {
        when (response.message) {
            "Phone didn't found" -> {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setTitle("Регистрация")
                        setMessage("Приложение не нашло вас в базе данных зарегистрированных пользователей.\nХотите зарегистрироваться?")
                        setPositiveButton("Да",
                            DialogInterface.OnClickListener { dialog, id ->
                                val amount =
                                    binding.phoneNumberTextLayout.editText?.text.toString()
                                val action =
                                    ru.psuti.apache1337.homeowners.presentation.auth.AuthFragmentDirections.actionAuthFragmentToRegistrationFragment(
                                        amount
                                    )
                                navController.navigate(action)
                            })
                        setNegativeButton("Нет", null)

                    }
                    builder.create()
                }
                alertDialog?.show()
            }
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

    private fun handleErrorsInConfirmLayout(response: ResponseState) {
        when (response.message) {
            "Password wasn't valid" -> binding.codeTextLayout.error =
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

    private fun toConfirmLayout() {
//        if (isPhoneChanged) {
//
//        }
        timer?.cancel()
        binding.codeTextLayout.editText?.setText("")

        binding.codeTextLayout.isEnabled = true

        state = State.CONFIRM_CODE

        initAnimationFromLoginLayoutToConfirmLayout(250)

        binding.sendCodeDescription.text =
            getString(
                R.string.text_view_send_code_description,
                binding.phoneNumberTextLayout.editText?.text
            )
        startTimer()
    }

    private fun toLogInLayout() {
        state = State.LOGIN

        initAnimationFromConfirmLayoutToLoginLayout(250)
    }


    private fun getSupportTextWithLinks(): SpannableString {
        val supportText = SpannableString(getString(R.string.text_view_support_text))

        val supportTextClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navController.navigate(ru.psuti.apache1337.homeowners.presentation.auth.AuthFragmentDirections.actionAuthFragmentToSupportPlaceHolderFragment())
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

    private fun initAnimationFromLoginLayoutToConfirmLayout(
        animDuration: Long,
        chosenInterpolator: Interpolator = LinearInterpolator()
    ) {
        val hideLoginLayout =
            ObjectAnimator.ofFloat(
                binding.loginLayout,
                "translationX",
                -binding.loginLayout.width.toFloat()
            )
                .apply {
                    duration = animDuration
                    doOnEnd {
                        binding.loginLayout.visibility = View.GONE
                    }
                    interpolator = chosenInterpolator
                }
        val showConfirmLayout = ObjectAnimator.ofFloat(
            binding.confirmLayout,
            "translationX",
            binding.loginLayout.width.toFloat(),
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

    private fun initAnimationFromConfirmLayoutToLoginLayout(
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
            binding.loginLayout,
            "translationX",
            -binding.loginLayout.width.toFloat(),
            0f
        ).apply {
            duration = animDuration
            startDelay = 100
            doOnStart {
                binding.loginLayout.visibility = View.VISIBLE
            }
            interpolator = chosenInterpolator
        }

        AnimatorSet().apply {
            playTogether(showLoginLayout, hideConfirmLayout)
            start()
        }
    }

    private fun changeStateTo(state: State) {
        when (state) {
            State.LOGIN -> {
                binding.loginLayout.visibility = View.VISIBLE
                binding.confirmLayout.visibility = View.GONE
            }
            State.CONFIRM_CODE -> {
                binding.loginLayout.visibility = View.GONE
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
                    binding.termOfUseCheckbox.isChecked && authViewModel.checkCodeLength(binding.codeTextLayout.editText?.text.toString())
            }
        }
    }

    private fun checkInternetConnection(): Boolean {
        val cm = context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
    }
}
