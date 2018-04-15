package com.hexan.ripper.model

import com.fasterxml.jackson.annotation.*
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "playlists")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator::class,
                property = "id")
class Playlist(val name: String,
               @ManyToOne(optional = true, cascade = [CascadeType.ALL])
               @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
               val user: User,
               @ElementCollection
               @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
               @JoinTable(name = "playlist_song",
                       joinColumns =  [JoinColumn(name = "playlist_id")] ,
                       inverseJoinColumns = [JoinColumn(name = "song_id")])
               val songs: MutableList<Song>,
               @JsonIgnore
               val created: Instant,
               @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
               val id: Long = -1) {

    private constructor() : this("", User(), ArrayList<Song>(), Instant.now())

    companion object {
        private val serialVersionUID = 1L
    }
}

data class NewPlaylist @JsonCreator constructor(val name: String)