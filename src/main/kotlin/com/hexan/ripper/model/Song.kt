package com.hexan.ripper.model

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
        @JsonIgnoreProperties("songs", "albums")
        val artist: Artist,
        var mediaUrl: String,
        @ManyToOne(optional = true, cascade = [CascadeType.ALL])
        @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = true)
        @JsonIgnoreProperties("songs", "artist")
        var album: Album,
        @ManyToMany(mappedBy = "songs")
        @JsonIgnore
        val playlists: MutableSet<Playlist>,
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        val genres: MutableList<Genre>?,
        var processing: Boolean,
        @JsonIgnore
        val created: Instant,
        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long = -1) {

    constructor() : this("", Artist(), "", Album(), HashSet(), ArrayList(), true, Instant.now())

    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewSong @JsonCreator constructor(val name: String,
                                            val artistId: Long,
                                            val mediaUrl: String = "",
                                            val albumId: Long? = null,
                                            val genres: Iterable<Long>)