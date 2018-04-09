package com.hexan.secustream.repo

import com.hexan.secustream.model.Song
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SongRepository : JpaRepository<Song, Long> {

    fun findByName(name: String): Iterable<Song>
}