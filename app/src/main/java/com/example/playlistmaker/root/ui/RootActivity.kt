package com.example.playlistmaker.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.playlist.ui.BackNavigationListenerRoot
import com.example.playlistmaker.playlist.ui.PlaylistCreateFragment
import kotlinx.coroutines.launch

class RootActivity : AppCompatActivity(), BackNavigationListenerRoot {
    private var binding: ActivityRootBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcvRootConteiner) as NavHostFragment
        val navController = navHostFragment.navController

        // Toolbar
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding?.toolbar?.setupWithNavController(navController, appBarConfiguration)
        // скрыть стрелку назад, через supportActionBar с флагом false не работает
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if ((destination.id == R.id.searchFragment) ||
                (destination.id == R.id.settingsFragment)
            ) {
                binding?.toolbar?.navigationIcon = null
            }
        }

        binding?.bottomNavigationView?.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playlistCreateFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }

    override fun onNavigateBack(isEmpty: Boolean) {
        if (isEmpty) {
            super.onBackPressedDispatcher.onBackPressed()
        } else {
            backCheckFragment()
        }
    }

    private fun showBottomNavigation() {
        binding?.bottomNavigationView?.isVisible = true
        binding?.llDivider?.isVisible = true

    }

    private fun hideBottomNavigation() {
        binding?.bottomNavigationView?.isVisible = false
        binding?.llDivider?.isVisible = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onNavigateBack(false)
    }

    private fun backCheckFragment() {
        val currentNavHostFragment = supportFragmentManager.findFragmentById(R.id.fcvRootConteiner)
        if (currentNavHostFragment is NavHostFragment) {
            val childFragmentManager = currentNavHostFragment.childFragmentManager
            val currentFragment = childFragmentManager.primaryNavigationFragment
            if (currentFragment is PlaylistCreateFragment) {
                lifecycleScope.launch {
                    currentFragment.navigateBack()
                }
            } else {
                super.onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}