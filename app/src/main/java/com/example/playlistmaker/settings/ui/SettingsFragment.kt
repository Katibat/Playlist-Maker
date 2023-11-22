package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка switch "Темная тема"
        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
        viewModel.themeSettingsState.observe(viewLifecycleOwner) { theme ->
            binding.switchTheme.isChecked = theme.switchTheme
        }

        // Настройка кнопки "Поделиться приложением"
        binding.tvShareApp.setOnClickListener {
            viewModel.shareApp()
        }

        // Настройка кнопки "Написать в поддержку"
        binding.tvSendSupport.setOnClickListener {
            viewModel.sendEmailToSupport()
        }

        // Настройка кнопки "Пользовательское соглашение"
        binding.tvUserAgreement.setOnClickListener {
            viewModel.openUserAgreement()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}