package com.hexan.secustream.repo

import com.hexan.secustream.model.Album
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlbumRepository : JpaRepository<Album, Long> {

    fun findByName(name: String): Iterable<Album>
}