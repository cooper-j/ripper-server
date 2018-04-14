package com.hexan.ripper.repo

import com.hexan.ripper.model.Playlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaylistRepository : JpaRepository<Playlist, Long> {

    fun findByName(name: String): Iterable<Playlist>
    fun existsByName(name: String): Boolean
}