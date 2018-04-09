package com.hexan.secustream.model

import com.fasterxml.jackson.annotation.*
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "artists")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Artist(val name: String,
             var profileUrl: String?,
             @ElementCollection
             @OneToMany(fetch = FetchType.LAZY, mappedBy = "artist", cascade = [CascadeType.ALL])
             @JsonManagedReference("album-artists")
             var albums: MutableList<Album>?,
             @ElementCollection
             @OneToMany(fetch = FetchType.LAZY, mappedBy = "artist", cascade = [CascadeType.ALL])
             @JsonManagedReference("song-artists")
             @JsonIgnore
             var songs: MutableList<Song>?,
             @JsonIgnore
             val created: Instant = Instant.now(),
             @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
             val id: Long = -1) {

    constructor() : this("", "", null, null)

    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewArtist @JsonCreator constructor(val name: String)