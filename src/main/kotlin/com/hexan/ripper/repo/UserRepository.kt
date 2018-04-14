package com.hexan.ripper.repo

import com.hexan.ripper.model.User
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User
}