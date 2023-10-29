package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.buttonSearch?.setOnClickListener {
            viewModel.openSearchScreen()
        }

        binding?.buttonMedia?.setOnClickListener {
            viewModel.openMediaScreen()
        }

        binding?.buttonSettings?.setOnClickListener {
            viewModel.openSettingScreen()
        }
    }
}