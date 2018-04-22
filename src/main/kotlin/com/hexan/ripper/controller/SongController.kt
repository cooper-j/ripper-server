package com.hexan.ripper.controller

import com.hexan.ripper.model.NewSong
import com.hexan.ripper.model.Song
import com.hexan.ripper.repo.AlbumRepository
import com.hexan.ripper.repo.ArtistRepository
import com.hexan.ripper.repo.GenreRepository
import com.hexan.ripper.repo.SongRepository
import com.hexan.ripper.service.MediaProcessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
class SongController {

    @Autowired
    lateinit var songRepository: SongRepository
    @Autowired
    lateinit var genreRepository: GenreRepository
    @Autowired
    lateinit var albumRepository: AlbumRepository
    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var mediaProcessService: MediaProcessService

    @RequestMapping("/song", method = [(RequestMethod.POST)])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createSong(@RequestBody newSong: NewSong): String {
        if (!StringUtils.isEmpty(newSong.name) && !StringUtils.isEmpty(newSong.artistId)) {
            val artistEntity = artistRepository.findById(newSong.artistId)
            if (!artistEntity.isPresent)
                return "Error: No artist for id " + newSong.artistId

            if (songRepository.findByName(newSong.name).isPresent)
                return "Error: A song with this name already exists"

            if (!StringUtils.isEmpty(newSong.albumId)) {
                val albumEntity = albumRepository.findById(newSong.albumId!!)
                        if (!albumEntity.isPresent)
                            return "Error: No album for id " + newSong.albumId
                val song = Song(newSong.name, artistEntity.get(), newSong.mediaUrl, albumEntity.get(), HashSet(), genreRepository.findAllById(newSong.genres), true, Instant.now())

                mediaProcessService.executeAsynchronously(song)

                val save = songRepository.save(song)
                return "{\"id\":" + save.id +"}"
            } else
                return "Error albumId is mandatory"
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