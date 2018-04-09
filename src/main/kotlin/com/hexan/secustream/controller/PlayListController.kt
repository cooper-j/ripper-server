package com.hexan.secustream.controller

import com.hexan.secustream.model.NewPlaylist
import com.hexan.secustream.model.Playlist
import com.hexan.secustream.model.UserDetails
import com.hexan.secustream.repo.PlaylistRepository
import com.hexan.secustream.repo.SongRepository
import com.hexan.secustream.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createPlaylist(@RequestBody newPlaylist: NewPlaylist): String {
        if (playlistRepository.existsByName(newPlaylist.name))
            return "Playlist already exists"
        if (!StringUtils.isEmpty(newPlaylist.name)) {
            val playlist = Playlist(newPlaylist.name, (SecurityContextHolder.getContext().authentication.principal as UserDetails).user, ArrayList(), Instant.now())
            val save = playlistRepository.save(playlist)
            return "{\"id\":" + save.id + "}"
        }
        return "Error"
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

    @RequestMapping("/playlist/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = playlistRepository.findAll()

    @RequestMapping("/playlist/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = playlistRepository.findById(id)

    @RequestMapping("/playlist/findbysongname/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = playlistRepository.findByName(name)

}