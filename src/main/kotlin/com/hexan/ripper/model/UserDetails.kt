package com.hexan.ripper.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.ArrayList
import org.springframework.security.core.userdetails.UserDetails


class UserDetails (var user: User) : UserDetails {
    private val authorities: Collection<GrantedAuthority>
    private val password: String? = user.password
    private val username: String? = user.username

    init {
        this.authorities = translate(user.roles.toList())
    }

    private fun translate(roles: List<UserRole>): Collection<GrantedAuthority> {
        val authorities = ArrayList<GrantedAuthority>()
        for (role in roles) {
            var name = role.name!!.toUpperCase()
            if (!name.startsWith("ROLE_")) {
                name = "ROLE_" + name
            }
            authorities.add(SimpleGrantedAuthority(name))
        }
        return authorities
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String? {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        private const val serialVersionUID = 1L
    }

}