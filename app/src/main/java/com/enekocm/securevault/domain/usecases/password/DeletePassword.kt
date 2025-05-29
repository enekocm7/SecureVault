package com.enekocm.securevault.domain.usecases.password

import com.enekocm.securevault.data.mapper.PasswordMapper
import com.enekocm.securevault.domain.model.PasswordDto
import com.enekocm.securevault.domain.repository.PasswordRepository
import javax.inject.Inject

class DeletePassword @Inject constructor(private val passwordRepository: PasswordRepository) {
    operator fun invoke(passwordDto: PasswordDto) = passwordRepository.deletePassword(PasswordMapper.mapToEntity(passwordDto))

}