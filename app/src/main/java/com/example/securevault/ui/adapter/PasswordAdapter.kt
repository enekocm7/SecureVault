package com.example.securevault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.securevault.R
import com.example.securevault.domain.model.PasswordDto
import com.example.securevault.ui.view.fragments.PasswordDetailDialog

class PasswordAdapter() : RecyclerView.Adapter<PasswordViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<PasswordDto>() {
        override fun areItemsTheSame(
            oldItem: PasswordDto, newItem: PasswordDto
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: PasswordDto, newItem: PasswordDto
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PasswordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PasswordViewHolder(layoutInflater.inflate(R.layout.password_item, parent, false))
    }

    override fun onBindViewHolder(
        holder: PasswordViewHolder, position: Int
    ) {
        val password = asyncListDiffer.currentList[position]
        holder.render(password)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (context is AppCompatActivity){
                PasswordDetailDialog(password).show(context.supportFragmentManager,"PasswordDetailDialog")
            }
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun updatePasswords(newPasswords: List<PasswordDto>) {
        asyncListDiffer.submitList(newPasswords)
    }

}



