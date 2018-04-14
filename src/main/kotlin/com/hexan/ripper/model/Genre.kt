package com.hexan.ripper.model

import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.*

@Entity
@Table(name = "genre")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Genre(
        val name: String,
        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long = -1) {

    constructor() : this("")
}