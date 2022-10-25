package com.github.tesis.runnerms.repositories

import com.github.tesis.runnerms.domain.entities.Integration
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface IntegrationRepository : JpaRepository<Integration, Long> {
    fun getByUserCode(userCode: UUID): List<Integration>

    fun getByCodeAndUserCode(code: UUID, userCode: UUID): Optional<Integration>
}
