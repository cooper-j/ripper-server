package com.hexan.secustream.controller

import com.hexan.secustream.model.Genre
import com.hexan.secustream.repo.GenreRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*

@RestController
class GenreController {

    @Autowired
    lateinit var genreRepository: GenreRepository

    @RequestMapping("/genre", method = [(RequestMethod.POST)])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createSong(@RequestBody newGenre: Genre): String {
        if (!StringUtils.isEmpty(newGenre.name)) {

            if (genreRepository.findByName(newGenre.name).isPresent)
                return "Error: A genre with this name already exists"

            val save = genreRepository.save(newGenre)
            return "{\"id\":" + save.id +"}"
        }
        return "Error"
    }

    @RequestMapping("/genre/findall")
    @ResponseStatus(value = HttpStatus.OK)
    fun findAll() = genreRepository.findAll()

    @RequestMapping("/genre/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findById(@PathVariable id: Long) = genreRepository.findById(id)

    @RequestMapping("/genre/findbygenrename/{name}")
    @ResponseStatus(value = HttpStatus.OK)
    fun findBySongName(@PathVariable name: String) = genreRepository.findByName(name)

}