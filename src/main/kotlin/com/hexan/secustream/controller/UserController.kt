package com.hexan.secustream.controller

import com.hexan.secustream.model.NewUser
import com.hexan.secustream.model.User
import com.hexan.secustream.model.UserDetails
import com.hexan.secustream.repo.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.hexan.secustream.model.UserRole
import com.hexan.secustream.service.SignupService
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class UserController {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var signupService: SignupService

    @RequestMapping("/me", method = [(RequestMethod.GET)])
    fun userAuth(auth: Authentication): Authentication {
        return auth
    }

    @RequestMapping("/user", method = [(RequestMethod.GET)])
    fun user(auth: Authentication): User {
        return userRepository.getOne((auth.principal as UserDetails).user.id!!)
    }

    @RequestMapping("/user", method = [(RequestMethod.POST)])
    fun saveUser(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    @RequestMapping("/signup", method = [(RequestMethod.POST)])
    fun signup(@RequestBody newUser: NewUser): ResponseEntity<*> {

        val user = User(newUser.username, newUser.password, null, true,  hashSetOf(UserRole("USER")))
        signupService.addUser(user)
        return ResponseEntity<Any>(HttpStatus.CREATED)
    }


    @RequestMapping("/user/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = userRepository.findAll()

    @RequestMapping("/user/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = userRepository.findById(id)

    @RequestMapping("/user/findbyusername/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = userRepository.findByUsername(name)
}