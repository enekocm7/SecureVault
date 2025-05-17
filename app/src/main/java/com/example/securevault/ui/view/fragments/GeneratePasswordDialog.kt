package com.example.securevault.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.securevault.databinding.PasswordGeneratorBinding
import com.example.securevault.ui.viewmodel.fragments.GeneratePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeneratePasswordDialog : DialogFragment() {

    private lateinit var binding: PasswordGeneratorBinding
    private val viewModel: GeneratePasswordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PasswordGeneratorBinding.inflate(layoutInflater)

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()

        val dialog = super.onCreateDialog(savedInstanceState).apply {
            setContentView(binding.root)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setLayout(
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        setUpView()
        setListeners()
        setObservers()

        return dialog
    }

    private fun setListeners() {
        binding.passwordTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.passwordRadioButton.id -> {
                    binding.passwordOptionsLayout.visibility = ViewGroup.VISIBLE
                    binding.passphraseOptionsLayout.visibility = ViewGroup.GONE
                    generatePassword()
                }

                binding.passphraseRadioButton.id -> {
                    binding.passwordOptionsLayout.visibility = ViewGroup.GONE
                    binding.passphraseOptionsLayout.visibility = ViewGroup.VISIBLE
                    generatePassphrase()
                }
            }
        }

        binding.lengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.lengthTextView.text = buildString {
                    append("Length: ")
                    append(progress)
                }
                if (fromUser) generatePassword()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.lowercaseSwitch.setOnCheckedChangeListener { _, _ -> generatePassword() }
        binding.uppercaseSwitch.setOnCheckedChangeListener { _, _ -> generatePassword() }
        binding.numbersSwitch.setOnCheckedChangeListener { _, _ -> generatePassword() }
        binding.symbolsSwitch.setOnCheckedChangeListener { _, _ -> generatePassword() }

        binding.wordsCountSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.wordsCountTextView.text = buildString {
                    append("Number of words: ")
                    append(progress)
                }
                if (fromUser) generatePassphrase()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.regenerateButton.setOnClickListener {
            if (binding.passwordRadioButton.isChecked) {
                generatePassword()
            } else {
                generatePassphrase()
            }
        }

        binding.closeButton.setOnClickListener { dismiss() }
        binding.usePasswordButton.setOnClickListener {

            dismiss()
        }

    }


    private fun setUpView() {
        generatePassword()

        binding.lengthTextView.text = buildString {
            append("Length: ")
            append(binding.lengthSeekBar.progress)
        }
        binding.wordsCountTextView.text = buildString {
            append("Number of words: ")
            append(binding.wordsCountSeekBar.progress)
        }

        if (!binding.lowercaseSwitch.isChecked &&
            !binding.uppercaseSwitch.isChecked &&
            !binding.numbersSwitch.isChecked &&
            !binding.symbolsSwitch.isChecked
        ) {
            binding.lowercaseSwitch.isChecked = true
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.password.collect { password ->
                binding.generatedPasswordTextView.text = password
            }
        }
    }

    private fun generatePassword() {
        viewModel.getPassword(
            length = binding.lengthSeekBar.progress,
            lower = binding.lowercaseSwitch.isChecked,
            upper = binding.uppercaseSwitch.isChecked,
            numbers = binding.numbersSwitch.isChecked,
            symbols = binding.symbolsSwitch.isChecked
        )
    }

    private fun generatePassphrase() {
        viewModel.getPassphrase(binding.wordsCountSeekBar.progress, "-")
    }

}