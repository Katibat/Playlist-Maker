package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Настройка switch "Темная тема"
        binding.switchTheme.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
        if ((applicationContext as App).darkTheme) {
            binding.switchTheme.isChecked = true;
        }

        // Настройка кнопки "Поделиться приложением"
        binding.tvShareApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = ACTION_SEND
                putExtra(EXTRA_TEXT, getString(R.string.url_android_developer))
                type = "text/plain"
            }
            val shareIntent = createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        // Настройка кнопки "Написать в поддержку"
        binding.tvSendSupport.setOnClickListener {
            Intent().apply {
                action = ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(EXTRA_SUBJECT, getString(R.string.theme_support_message))
                putExtra(EXTRA_TEXT, getString(R.string.support_message))
                startActivity(createChooser(this, null))
            }
        }

        // Настройка кнопки "Пользовательское соглашение"
        binding.tvUserAgreement.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = ACTION_VIEW
                data = Uri.parse(getString(R.string.url_oferta))
            }
            val sendEmail = createChooser(sendIntent, null)
            startActivity(sendEmail)
        }
    }
}