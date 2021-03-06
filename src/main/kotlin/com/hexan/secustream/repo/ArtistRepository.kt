package com.hexan.secustream.repo

import com.hexan.secustream.model.Artist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArtistRepository : JpaRepository<Artist, Long> {

    fun findByName(name: String): Iterable<Artist>
    fun existsByName(name: String): Boolean
}