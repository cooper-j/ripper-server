package com.hexan.secustream.model

import com.fasterxml.jackson.annotation.*
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "songs")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Song(
        val name: String,
        @ManyToOne(optional = true, cascade = [CascadeType.ALL])
        @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = true)
        @JsonBackReference("artist-song")
        val artist: Artist,
        val mediaUrl: String,
        @ManyToOne(optional = true, cascade = [CascadeType.ALL])
        @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = true)
        @JsonBackReference("album-song")
        var album: Album? = null,
        @ManyToMany(mappedBy = "songs")
        @JsonIgnore
        val playlists: MutableSet<Playlist>,
        @JsonIgnore
        val created: Instant,
        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long = -1) {

    constructor() : this("", Artist(), "", null, HashSet(), Instant.now())


    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewSong @JsonCreator constructor(val name: String,
                                            val artistId: Long,
                                            val mediaUrl: String = "",
                                            val albumId: Long? = null)