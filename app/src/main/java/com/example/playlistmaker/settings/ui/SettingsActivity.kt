package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    private var viewModel: SettingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Настройка Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        viewModel = ViewModelProvider(this,
            SettingsViewModel.getViewModelFactory(this))[SettingsViewModel::class.java]

        // Настройка switch "Темная тема"
        binding?.switchTheme?.setOnCheckedChangeListener { _, checked ->
            viewModel!!.switchTheme(checked)
        }
        viewModel?.themeSettingsState?.observe(this) { theme ->
            binding?.switchTheme?.isChecked = theme.switchTheme
        }

        // Настройка кнопки "Поделиться приложением"
        binding?.tvShareApp?.setOnClickListener {
            viewModel?.shareApp()
        }

        // Настройка кнопки "Написать в поддержку"
        binding?.tvSendSupport?.setOnClickListener {
            viewModel?.sendEmailToSupport()
        }

        // Настройка кнопки "Пользовательское соглашение"
        binding?.tvUserAgreement?.setOnClickListener {
            viewModel?.openUserAgreement()
        }
    }
}