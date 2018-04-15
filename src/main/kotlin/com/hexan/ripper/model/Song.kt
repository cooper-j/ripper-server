package com.hexan.ripper.model

import com.fasterxml.jackson.annotation.*
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "songs")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator::class,
        property = "id")
class Song(
        val name: String,
        @ManyToOne(optional = true, cascade = [CascadeType.ALL])
        @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = true)
        val artist: Artist,
        val mediaUrl: String,
        @ManyToOne(optional = true, cascade = [CascadeType.ALL])
        @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = true)
        var album: Album,
        @ManyToMany(mappedBy = "songs")
        val playlists: MutableSet<Playlist>,
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        val genres: MutableList<Genre>?,
        @JsonIgnore
        val created: Instant,
        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long = -1) {

    constructor() : this("", Artist(), "", Album(), HashSet(), ArrayList(), Instant.now())


    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewSong @JsonCreator constructor(val name: String,
                                            val artistId: Long,
                                            val mediaUrl: String = "",
                                            val albumId: Long? = null,
                                            val genres: Iterable<Long>)