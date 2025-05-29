package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class AddPassword @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke(previousName:String, password: PasswordDto) = passwordRepository.insertPassword(previousName, PasswordMapper.mapToEntity(password))

    operator fun invoke(password: PasswordDto) = passwordRepository.insertPassword(PasswordMapper.mapToEntity(password))

}