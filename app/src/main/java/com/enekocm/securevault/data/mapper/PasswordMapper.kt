package com.enekocm.securevault.data.mapper

import com.enekocm.securevault.data.json.model.Password
import com.enekocm.securevault.domain.model.PasswordDto

object PasswordMapper {

    fun mapToDto(password: Password): PasswordDto {
        return PasswordDto(
            name = password.name,
            url = password.url,
            username = password.username,
            value = password.value
        )
    }

    fun mapToEntity(passwordDto: PasswordDto): Password {
        return Password(
            name = passwordDto.name,
            url = passwordDto.url,
            username = passwordDto.username,
            value = passwordDto.value
        )
    }

}