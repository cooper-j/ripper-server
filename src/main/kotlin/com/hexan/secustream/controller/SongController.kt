package com.hexan.secustream.controller

import com.hexan.secustream.model.Album
import com.hexan.secustream.model.NewSong
import com.hexan.secustream.model.Song
import com.hexan.secustream.repo.AlbumRepository
import com.hexan.secustream.repo.ArtistRepository
import com.hexan.secustream.repo.SongRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
class SongController {

    @Autowired
    lateinit var songRepository: SongRepository
    @Autowired
    lateinit var albumRepository: AlbumRepository
    @Autowired
    lateinit var artistRepository: ArtistRepository

    @RequestMapping("/song", method = [(RequestMethod.POST)])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createSong(@RequestBody newSong: NewSong): String {
        if (!StringUtils.isEmpty(newSong.name) && !StringUtils.isEmpty(newSong.artistId)) {
            val artistEntity = artistRepository.findById(newSong.artistId)
            if (!artistEntity.isPresent)
                return "Error: No artist for id " + newSong.artistId

            var album : Album? = null
            if (!StringUtils.isEmpty(newSong.albumId)) {
                val albumEntity = albumRepository.findById(newSong.albumId!!)
                        if (!albumEntity.isPresent)
                            return "Error: No album for id " + newSong.albumId
                album = albumEntity.get()
            }

            val song = Song(newSong.name, artistEntity.get(), newSong.mediaUrl, album, HashSet(), Instant.now())

            val save = songRepository.save(song)
            return "{\"id\":" + save.id +"}"
        }
        return "Error"
    }

    @RequestMapping("/song/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = songRepository.findAll()

    @RequestMapping("/song/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = songRepository.findById(id)

    @RequestMapping("/song/findbysongname/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = songRepository.findByName(name)

}