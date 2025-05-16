package com.example.securevault.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.securevault.databinding.PasswordItemBinding
import com.example.securevault.domain.model.PasswordDto

class PasswordViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = PasswordItemBinding.bind(view)

    fun render(passwordDto: PasswordDto) {
        binding.tvTitle.text = passwordDto.name
        binding.tvEmail.text = passwordDto.username
    }
}