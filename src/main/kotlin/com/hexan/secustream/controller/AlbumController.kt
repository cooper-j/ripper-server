package com.hexan.secustream.controller

import com.hexan.secustream.model.*
import com.hexan.secustream.repo.AlbumRepository
import com.hexan.secustream.repo.ArtistRepository
import com.hexan.secustream.repo.SongRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*

@RestController
class AlbumController {

    @Autowired
    lateinit var albumRepository: AlbumRepository
    @Autowired
    lateinit var songRepository: SongRepository
    @Autowired
    lateinit var artistRepository: ArtistRepository

    @RequestMapping("/album", method = [(RequestMethod.POST)])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createAlbum(@RequestBody newAlbum: NewAlbum): String {
        if (StringUtils.isEmpty(newAlbum.name))
            return "Error: Album name cannot be empty"
        val artistEntity = artistRepository.findById(newAlbum.artistId)
        if (!artistEntity.isPresent)
            return "Error: No artist for id " + newAlbum.artistId
        val save = albumRepository.save(Album(newAlbum.name, artistEntity.get(), if (newAlbum.coverUrl.isNullOrBlank()) "https://s3.eu-west-3.amazonaws.com/storage-hexan/album-covers/default_album_cover.jpg" else newAlbum.coverUrl!! , newAlbum.songs))
        return "{\"id\":" + save.id + "}"
    }

    @RequestMapping("/album/{id}/addsongs", method = [(RequestMethod.PATCH)])
    @ResponseStatus(value = HttpStatus.OK)
    fun addSongToAlbum(@PathVariable id: Long, @RequestBody songIdList: List<Long>): String {
        if (!songIdList.isEmpty()) {
            val albumEntity = albumRepository.findById(id)

            if (!albumEntity.isPresent)
                return "Error: No album for id " + id
            val album = albumEntity.get()

            if (album.songs == null)
                album.songs = ArrayList()

            for (songId in songIdList) {
                val songEntity = songRepository.findById(songId)
                if (!songEntity.isPresent)
                    return "Error: No song for id " + songId
                album.songs?.add(songEntity.get())
            }
            albumRepository.save(album)
            return "{\"id\":" + album.id + "}"
        }
        return "Error"
    }

    @RequestMapping("/album/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = albumRepository.findAll()

    @RequestMapping("/album/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = albumRepository.findById(id)

    @RequestMapping("/album/findbysongname/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = albumRepository.findByName(name)

}