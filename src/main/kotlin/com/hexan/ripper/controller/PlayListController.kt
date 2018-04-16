package com.hexan.ripper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hexan.ripper.model.NewPlaylist
import com.hexan.ripper.model.Playlist
import com.hexan.ripper.model.UserDetails
import com.hexan.ripper.repo.PlaylistRepository
import com.hexan.ripper.repo.SongRepository
import com.hexan.ripper.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jackson.JsonObjectSerializer
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
class PlayListController {

    @Autowired
    lateinit var playlistRepository: PlaylistRepository
    @Autowired
    lateinit var songRepository: SongRepository
    @Autowired
    lateinit var userRepository: UserRepository

    @RequestMapping("/playlist", method = [(RequestMethod.POST)])
    fun createPlaylist(@RequestBody newPlaylist: NewPlaylist): ResponseEntity<String> {
        if (playlistRepository.existsByName(newPlaylist.name))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Playlist already exists")
        if (!StringUtils.isEmpty(newPlaylist.name)) {
            val playlist = Playlist(newPlaylist.name, (SecurityContextHolder.getContext().authentication.principal as UserDetails).user, ArrayList(), Instant.now())
            val save = playlistRepository.save(playlist)

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ObjectMapper().writeValueAsString(save))
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Playlist already exists")
    }

    @RequestMapping("/playlist/{id}/addsongs", method = [(RequestMethod.PATCH)])
    @ResponseStatus(value = HttpStatus.OK)
    fun addSongToPlaylist(@PathVariable id: Long, @RequestBody songIdList: List<Long>): String {
        if (!songIdList.isEmpty()) {
            val playlistEntity = playlistRepository.findById(id)

            if (!playlistEntity.isPresent)
                return "No playlist for id " + id
            val playlist = playlistEntity.get()
            for (songId in songIdList) {
                playlist.songs.add(songRepository.findById(songId).get())
            }
            playlistRepository.save(playlist)
            return "{\"id\":" + playlist.id + "}"
        }
        return "Error"
    }

    @RequestMapping("/playlist/{id}/songs", method = [(RequestMethod.GET)])
    @ResponseStatus(value = HttpStatus.OK)
    fun getPlaylistSongs(@PathVariable id: Long): ResponseEntity<String> {
        val playlistEntity = playlistRepository.findById(id)
        if (!playlistEntity.isPresent)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Playlist does not exist")
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ObjectMapper().writeValueAsString(playlistEntity.get().songs))
    }

    @RequestMapping("/playlist/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = playlistRepository.findAll(Sort(Sort.Direction.ASC, "created"))

    @RequestMapping("/playlist/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = playlistRepository.findById(id)

    @RequestMapping("/playlist/findbysongname/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = playlistRepository.findByName(name)

}