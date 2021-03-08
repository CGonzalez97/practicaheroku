package com.salesianostriana.dam.apirealstate.controllers

import com.salesianostriana.dam.apirealstate.dto.ListadoViviendaDto
import com.salesianostriana.dam.apirealstate.dto.toDto
import com.salesianostriana.dam.apirealstate.dto.toPropiaDto
import com.salesianostriana.dam.apirealstate.dto.toSpecificDto
import com.salesianostriana.dam.apirealstate.errors.SingleEntityNotFoundException
import com.salesianostriana.dam.apirealstate.models.Vivienda
import com.salesianostriana.dam.apirealstate.repositories.ViviendaRepository
import com.salesianostriana.dam.apirealstate.services.ViviendaServicio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/viviendas")
class ViviendaController {

    @Autowired
    lateinit var viviendaServicio: ViviendaServicio

    @GetMapping()
    fun getViviendas() = viviendaServicio.findAll().map { it.toDto() }

    @GetMapping("/search")
    fun searchVivienda(
            @RequestParam(name = "cat") categoria:String,
            @RequestParam(name = "ciu") ciudad:String,
            @RequestParam(name="pre") precio: Double) = viviendaServicio.searchVivienda(categoria, ciudad, precio)

    @PostMapping
    fun createVivienda(@Valid @RequestBody viviendaNueva:Vivienda,
                       @RequestHeader("Authorization") token: String): ResponseEntity<ListadoViviendaDto> =
            viviendaServicio.createVivienda(viviendaNueva,token)


    @GetMapping("/{id}")
    fun getViviendaPorId(@PathVariable id:Long) = viviendaServicio.getViviendaById(id).toSpecificDto()


    @GetMapping("/mine")
    fun getMyViviendas(@RequestHeader("Authorization") auth:String) = viviendaServicio.getListOfViviendas(auth).map { it.toPropiaDto() }


    @GetMapping("/favs")
    fun getViviendasFavs(@RequestHeader("Authorization") auth:String)= viviendaServicio.findAllFavs(auth)


    @PutMapping("/{id}")
    fun modifyVivienda(@PathVariable id: Long,@Valid @RequestBody viviendaNueva: Vivienda) =
        viviendaServicio.modifyVivienda(id, viviendaNueva)


    @PostMapping("/favs/{id}")
    fun addVivivendaAsFav(@PathVariable id: Long, @RequestHeader("Authorization") token: String) =
        viviendaServicio.addViviendaToFav(id,token)


    @DeleteMapping("/{id}")
    fun deleteVivienda(@PathVariable id: Long) =
        viviendaServicio.deleteVivienda(id)


    @DeleteMapping("/favs/{id}")
    fun deleteViviendaPorId(@PathVariable id:Long, @RequestHeader("Authorization") token: String) =
        viviendaServicio.deleteFavById(id, token)

}