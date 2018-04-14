package com.hexan.ripper.controller

import com.hexan.ripper.model.Artist
import com.hexan.ripper.repo.AlbumRepository
import com.hexan.ripper.repo.ArtistRepository
import com.hexan.ripper.repo.SongRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*

@RestController
class ArtistController {

    @Autowired
    lateinit var artistRepository: ArtistRepository
    @Autowired
    lateinit var albumRepository: AlbumRepository
    @Autowired
    lateinit var songRepository: SongRepository

    @RequestMapping("/artist", method = [(RequestMethod.POST)])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createArtist(@RequestBody artist: Artist): String {

        if (StringUtils.isEmpty(artist.name)) {
            return "Error: Artist name cannot be empty"
        }
        if (artistRepository.existsByName(artist.name)) {
            return "Artist already exists with that name"
        }

        if (artist.profileUrl.isNullOrBlank())
            artist.profileUrl = "https://s3.eu-west-3.amazonaws.com/storage-hexan/artist-image/default_artist_image.jpg"

        val save = artistRepository.save(artist)
        return "{\"id\":" + save.id + "}"
    }

    @RequestMapping("/artist/{id}/addsongs", method = [(RequestMethod.PATCH)])
    @ResponseStatus(value = HttpStatus.OK)
    fun addSongToArtist(@PathVariable id: Long, @RequestBody songIdList: List<Long>): String {
        if (!songIdList.isEmpty()) {
            val artistEntity = artistRepository.findById(id)

            if (!artistEntity.isPresent)
                return "No playlist for id " + id
            val artist = artistEntity.get()
            if (artist.songs == null)
                artist.songs = ArrayList()
            for (songId in songIdList) {
                val songEntity = songRepository.findById(songId)
                if (!songEntity.isPresent) {
                    return "Error: No song for id $songId"
                }
                artist.songs!!.add(songRepository.findById(songId).get())
            }
            artistRepository.save(artist)
            return "{\"id\":" + artist.id + "}"
        }
        return "Error: Song list empty"
    }

    @RequestMapping("/artist/{id}/addalbums", method = [(RequestMethod.PATCH)])
    @ResponseStatus(value = HttpStatus.OK)
    fun addArtistToArtist(@PathVariable id: Long, @RequestBody albumIdList: List<Long>): String {
        if (!albumIdList.isEmpty()) {
            val artistEntity = artistRepository.findById(id)

            if (!artistEntity.isPresent)
                return "No playlist for id " + id
            val artist = artistEntity.get()
            if (artist.songs == null)
                artist.songs = ArrayList()
            for (albumId in albumIdList) {
                val albumEntity = albumRepository.findById(albumId)
                if (!albumEntity.isPresent) {
                    return "Error: Album with id : $albumId not found"
                }
                artist.albums!!.add(albumEntity.get())
            }
            artistRepository.save(artist)
            return "{\"id\":" + artist.id + "}"
        }
        return "Error"
    }

    @RequestMapping("/artist/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = artistRepository.findAll()

    @RequestMapping("/artist/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = artistRepository.findById(id)

    @RequestMapping("/artist/findbysongname/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = artistRepository.findByName(name)

}