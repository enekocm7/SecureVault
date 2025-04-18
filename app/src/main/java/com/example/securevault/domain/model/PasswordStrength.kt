package com.example.securevault.domain.model

import androidx.core.graphics.toColorInt

enum class PasswordStrength(val score: Int,val label: String,val color: String){
    VERY_WEAK(0, "Very Weak", "#D32F2F"),
    WEAK(1, "Weak", "#F57C00"),
    MEDIUM(2, "Medium", "#FBC02D"),
    STRONG(3, "Strong", "#388E3C"),
    VERY_STRONG(4, "Very Strong", "#2E7D32");

    val progress get() = score*20+20
    val colorInt get() = color.toColorInt()

    companion object {
        fun fromScore(score: Int): PasswordStrength = when (score) {
            0 -> VERY_WEAK
            1 -> WEAK
            2 -> MEDIUM
            3 -> STRONG
            4 -> VERY_STRONG
            else -> VERY_WEAK
        }
    }
}


