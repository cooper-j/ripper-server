package com.hexan.ripper.service

import com.hexan.ripper.model.User
import com.hexan.ripper.model.UserRole
import org.springframework.beans.factory.annotation.Autowired
import com.hexan.ripper.repo.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct
import javax.transaction.Transactional


@Service
@Transactional
class SignupService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    fun addUser(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    /**
     *
     * set up a default user with two roles USER and ADMIN
     *
     */
    @PostConstruct
    private fun setupDefaultUser() {
        //-- just to make sure there is an ADMIN user exist in the database for testing purpose
        if (userRepository.count() == 0L) {
            userRepository.save(User("admin",
                    passwordEncoder.encode("adminpass"),
                    null,
                    true, hashSetOf(UserRole("USER"), UserRole("ADMIN"))))
        }
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

}