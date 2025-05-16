package com.example.securevault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securevault.R
import com.example.securevault.domain.model.PasswordDto

class PasswordAdapter(val passwords: List<PasswordDto>) :
    RecyclerView.Adapter<PasswordViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PasswordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PasswordViewHolder(layoutInflater.inflate(R.layout.password_item, parent, false))
    }

    override fun onBindViewHolder(
        holder: PasswordViewHolder,
        position: Int
    ) {
        val password = passwords[position]
        holder.render(password)
    }

    override fun getItemCount(): Int {
        return passwords.size
    }

}

