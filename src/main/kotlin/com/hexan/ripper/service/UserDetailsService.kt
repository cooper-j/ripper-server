package com.hexan.ripper.service

import com.hexan.ripper.model.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import com.hexan.ripper.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepository.findByUsername(userName).get()
        return UserDetails(user)
    }
}