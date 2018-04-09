package com.hexan.secustream.repo

import com.hexan.secustream.model.User
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User
}