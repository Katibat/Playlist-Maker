package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val shareApp = findViewById<TextView>(R.id.shareApp)
        val sendSupport = findViewById<TextView>(R.id.sendSupport)
        val userAgreement = findViewById<TextView>(R.id.userAgreement)
        val themeSwitch = findViewById<Switch>(R.id.switchTheme)

        // Настройка Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Настройка switch "Темная тема"
        themeSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Настройка кнопки "Поделиться приложением"
        shareApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = ACTION_SEND
                putExtra(EXTRA_TEXT, getString(R.string.url_android_developer))
                type = "text/plain"
            }
            val shareIntent = createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        // Настройка кнопки "Написать в поддержку"
        sendSupport.setOnClickListener {
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
        userAgreement.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = ACTION_VIEW
                data = Uri.parse(getString(R.string.url_oferta))
            }
            val sendEmail = createChooser(sendIntent, null)
            startActivity(sendEmail)
        }
    }
}