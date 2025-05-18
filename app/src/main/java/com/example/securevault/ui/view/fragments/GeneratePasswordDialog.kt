package com.example.securevault.ui.view.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.securevault.databinding.PasswordGeneratorBinding
import com.example.securevault.domain.model.PasswordStrength
import com.example.securevault.ui.viewmodel.fragments.GeneratePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeneratePasswordDialog : DialogFragment() {

    private lateinit var binding: PasswordGeneratorBinding
    private val viewModel: GeneratePasswordViewModel by activityViewModels()

    companion object{
        const val REQUEST_KEY = "GENERATED_PASSWORD_REQUEST_KEY"
    }

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
        setObservers()
        setUpView()
        setListeners()

        return dialog
    }

    private fun setListeners() {
        binding.regenerateButton.setOnClickListener {
            generatePassword()
        }

        binding.passwordTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.passwordRadioButton.id -> {
                    binding.regenerateButton.setOnClickListener {
                        generatePassword()
                    }
                    binding.passwordOptionsLayout.visibility = ViewGroup.VISIBLE
                    binding.passphraseOptionsLayout.visibility = ViewGroup.GONE
                    generatePassword()
                }

                binding.passphraseRadioButton.id -> {
                    binding.regenerateButton.setOnClickListener {
                        generatePassphrase()
                    }
                    binding.passwordOptionsLayout.visibility = ViewGroup.GONE
                    binding.passphraseOptionsLayout.visibility = ViewGroup.VISIBLE
                    generatePassphrase()
                }
            }
        }

        var lengthSeekBarJob: Job? = null
        var wordsCountSeekBarJob: Job? = null

        binding.lengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.lengthTextView.text = buildString {
                    append("Length: ")
                    append(progress)
                }
                if (fromUser) {
                    lengthSeekBarJob?.cancel()
                    lengthSeekBarJob = lifecycleScope.launch {
                        delay(300)
                        generatePassword()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Not going to use this function
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                lengthSeekBarJob?.cancel()
                generatePassword()
            }
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
                if (fromUser) {
                    wordsCountSeekBarJob?.cancel()
                    wordsCountSeekBarJob = lifecycleScope.launch {
                        delay(300)
                        generatePassphrase()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Not going to use this function
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                wordsCountSeekBarJob?.cancel()
                generatePassphrase()
            }
        })

        binding.closeButton.setOnClickListener { dismiss() }
        binding.usePasswordButton.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf("password" to binding.generatedPasswordTextView.text.toString()))
            dismiss()
        }

    }


    private fun setUpView() {
        binding.lengthTextView.text = buildString {
            append("Length: ")
            append(binding.lengthSeekBar.progress)
        }
        binding.wordsCountTextView.text = buildString {
            append("Number of words: ")
            append(binding.wordsCountSeekBar.progress)
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.password.collect { password ->
                binding.generatedPasswordTextView.text = password
            }
        }
        lifecycleScope.launch {
            viewModel.passwordStrength.collect { strength ->
                changeStrengthBar(strength)
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.generatedPasswordTextView.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            }
        }
    }

    private fun changeStrengthBar(strength: PasswordStrength) {
        binding.strengthTextView.text = strength.label
        binding.strengthProgressBar.progress = strength.progress
        binding.strengthProgressBar.progressTintList = ColorStateList.valueOf(strength.colorInt)
    }

    private fun generatePassword() {
        if (!binding.lowercaseSwitch.isChecked &&
            !binding.uppercaseSwitch.isChecked &&
            !binding.numbersSwitch.isChecked &&
            !binding.symbolsSwitch.isChecked
        ) {
            binding.lowercaseSwitch.isChecked = true
        }
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