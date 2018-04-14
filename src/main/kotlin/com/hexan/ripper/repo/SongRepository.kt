package com.hexan.ripper.repo

import com.hexan.ripper.model.Song
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SongRepository : JpaRepository<Song, Long> {

    fun findByName(name: String): Optional<Song>
}