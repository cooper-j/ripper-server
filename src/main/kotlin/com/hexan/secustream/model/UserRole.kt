package com.hexan.secustream.model

import javax.persistence.*


@Entity
@Table(name = "roles")
class UserRole(
        var name: String? = null,
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private val id: Long? = null

) {

    private constructor() : this(null)
}