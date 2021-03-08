package com.salesianostriana.dam.apirealstate.controllers

import com.salesianostriana.dam.apirealstate.dto.ImagenDto
import com.salesianostriana.dam.apirealstate.dto.NuevaImagenDto
import com.salesianostriana.dam.apirealstate.dto.toDto
import com.salesianostriana.dam.apirealstate.dto.toImagen
import com.salesianostriana.dam.apirealstate.errors.SingleEntityNotFoundException
import com.salesianostriana.dam.apirealstate.imageupload.ImgurBadRequest
import com.salesianostriana.dam.apirealstate.imageupload.ImgurImageAttribute
import com.salesianostriana.dam.apirealstate.imageupload.ImgurService
import com.salesianostriana.dam.apirealstate.models.ImagenVivienda
import com.salesianostriana.dam.apirealstate.models.Vivienda
import com.salesianostriana.dam.apirealstate.repositories.ImagenRepository
import com.salesianostriana.dam.apirealstate.repositories.ViviendaRepository
import com.salesianostriana.dam.apirealstate.services.ImagenServicio
import com.salesianostriana.dam.apirealstate.services.ViviendaServicio
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/vivienda")
class ImagenController(
        private val servicio: ImagenServicio,
        private val viviendaServicio:ViviendaServicio,
        private val imgurServicio: ImgurService,
        private var viviendaRepo:ViviendaRepository,
        private var imagenRepo:ImagenRepository
) {

    @GetMapping("/")
    fun getAll() : List<ImagenDto> {
        val result = servicio.findAll()
        if (result.isEmpty())
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No hay registros")
        return result.map { it.toDto() }

    }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long) : ImagenDto =
            servicio.findById(id).map { it.toDto() }
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Entidad $id no encontrada") }


    @PostMapping("/")
    fun create(@RequestPart("nuevo") new : NuevaImagenDto,
               @RequestPart("file") file: MultipartFile) : ResponseEntity<ImagenDto> {

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(servicio.save(new.toImagen(), file).toDto())
        } catch ( ex : ImgurBadRequest) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la subida de la imagen")
        }

    }


    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<Void> {
        servicio.deleteById(id)
        return ResponseEntity.noContent().build()
    }


    @PostMapping("/{idV}/img")
    fun addToVivienda(@PathVariable("idV")idV:Long,
                      @RequestPart("file") file: MultipartFile) : ResponseEntity<ImagenDto> {
        try {
            var vivienda = viviendaRepo.findById(idV).orElseThrow {
                SingleEntityNotFoundException(idV.toString(),Vivienda::class.java)
            }
            var imagenes = imagenRepo.findByVivienda(vivienda)
            vivienda.imagenes = imagenes
            var imagen = ImagenVivienda(vivienda, ImgurImageAttribute("",""))
            var aux = servicio.save(imagen,file)
            vivienda.addImagen(aux)
            viviendaRepo.save(vivienda)
            return ResponseEntity.status(HttpStatus.CREATED).body(aux.toDto())
        } catch ( ex : ImgurBadRequest) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la subida de la imagen")
        }
    }

    @DeleteMapping("/{id}/img/{hash}")
    fun removeFromVivienda(@PathVariable id: Long,@PathVariable hash:String) : ResponseEntity<Void> {
        var v = viviendaServicio.findById(id)
        imagenRepo.findByVivienda(v).map {
            if(it.img!!.deletehash == hash){
                servicio.delete(it)
                v.removeImagen(it)
                viviendaRepo.save(v)
                imgurServicio.delete(hash)
            }
        }

        return ResponseEntity.noContent().build()
    }
}