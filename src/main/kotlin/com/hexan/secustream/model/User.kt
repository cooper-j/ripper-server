package com.hexan.secustream.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*


@Entity
@Table(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
class User(@Column(name = "username", nullable = false, unique = true)
           val username: String? = null,

           @JsonIgnore
           @Column(name = "password", nullable = false)
           var password: String? = null,

           @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = [CascadeType.ALL])
           var playlists: MutableList<Playlist>? = ArrayList(),

           @Column(name = "enabled", nullable = false)
           val isEnabled: Boolean = false,

           @OneToMany(fetch = FetchType.EAGER, cascade = [(CascadeType.ALL)])
           val roles: MutableSet<UserRole>,

           @Id
           @GeneratedValue(strategy = GenerationType.SEQUENCE)
           val id: Long? = null) {

    constructor() : this("", "", null, true, HashSet<UserRole>())

    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewUser @JsonCreator constructor(val username: String, val password: String)