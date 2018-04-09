package com.hexan.secustream.model

import com.fasterxml.jackson.annotation.*
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "albums")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Album(val name: String,
            @ManyToOne(optional = true, cascade = [CascadeType.ALL])
            @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = true)
            @JsonBackReference("artist-album")
            val artist: Artist,
            val coverUrl: String,
            @ElementCollection
            @OneToMany(fetch = FetchType.LAZY, mappedBy = "album", cascade = [CascadeType.ALL])
            @JsonManagedReference("song-album")
            var songs: MutableList<Song>?,
            @JsonIgnore
            val created: Instant = Instant.now(),
            @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
            val id: Long = -1) {

    constructor() : this("", Artist(), "", null)

    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewAlbum @JsonCreator constructor(val name: String, val artistId: Long, val coverUrl: String?, val songs: ArrayList<Song>?)