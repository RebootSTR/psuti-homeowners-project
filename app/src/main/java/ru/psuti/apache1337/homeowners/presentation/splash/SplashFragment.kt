package ru.psuti.apache1337.homeowners.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.domain.model.ResponseState

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var appVersion: TextView
    private lateinit var backendVersion: TextView
    private lateinit var navController: NavController

    private val splashViewModel by viewModels<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        appVersion = view.findViewById(R.id.app_version)
        backendVersion = view.findViewById(R.id.backend_version)
        navController = findNavController()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appVersion.text = getString(R.string.app_version)

        splashViewModel.backendVersion.observe(viewLifecycleOwner) {
            val versionText = if (splashViewModel.isVersionReceived) {
                getString(R.string.backend_version, it)
            } else {
                getString(R.string.backend_version_error)
            }

            splashViewModel.saveBackendVersion(versionText)

            backendVersion.text = versionText
        }

        splashViewModel.logInResponseState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseState.Success -> {
                    navController.navigate(ru.psuti.apache1337.homeowners.presentation.splash.SplashFragmentDirections.actionSplashFragmentToIndicationsFragment())
                }
                is ResponseState.Failure -> {
                    navController.navigate(ru.psuti.apache1337.homeowners.presentation.splash.SplashFragmentDirections.actionSplashFragmentToAuthFragment())
                }
                else -> {

                }
            }
        }
        autoLogin()
    }

    private fun autoLogin() {
        splashViewModel.autoLogIn()
    }
}