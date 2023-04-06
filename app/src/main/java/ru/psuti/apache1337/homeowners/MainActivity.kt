package ru.psuti.apache1337.homeowners

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HomeownersDataFeed)
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setUpNavigation()

        setContentView(binding.root)
    }

    private fun setUpNavigation() {
        val navigationView = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        if (navHostFragment != null) {
            val navController = navHostFragment.findNavController()

            NavigationUI.setupWithNavController(navigationView, navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (
                    destination.id == R.id.authFragment ||
                    destination.id == R.id.supportPlaceHolderFragment ||
                    destination.id == R.id.registrationFragment ||
                            destination.id == R.id.splashFragment
                ) {
                    binding.bottomNavigationView.visibility = View.GONE
                } else {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }

        } else {
            throw Exception("NavHostFragment is null")
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}