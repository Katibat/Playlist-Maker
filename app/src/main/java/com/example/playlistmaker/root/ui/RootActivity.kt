package com.example.playlistmaker.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private var binding: ActivityRootBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navHostFragment = supportFragmentManager.
        findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // Toolbar
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding?.toolbar?.setupWithNavController(navController, appBarConfiguration)
        // скрыть стрелку назад, через supportActionBar с флагом false не работает
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if ((destination.id == R.id.searchFragment) ||
                (destination.id == R.id.settingsFragment)) {
                binding?.toolbar?.navigationIcon = null
            }
        }

        binding?.bottomNavigationView?.setupWithNavController(navController)
    }
}