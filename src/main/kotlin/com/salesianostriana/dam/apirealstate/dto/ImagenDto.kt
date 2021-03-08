package com.salesianostriana.dam.apirealstate.dto

import com.salesianostriana.dam.apirealstate.models.ImagenVivienda
import com.salesianostriana.dam.apirealstate.models.Vivienda

data class NuevaImagenDto(
        var vivienda: Vivienda,
        var texto: String
)

fun NuevaImagenDto.toImagen() = ImagenVivienda(vivienda)

data class ImagenDto(
        //var vivienda:ListadoViviendaDto,
        val imageId: String?,
        val id: Long?
)

data class ImagenViviendaPropiaDto(
        val imageId: String?,
        val hash: String?,
        val id: Long?
)

fun ImagenVivienda.toDto() = ImagenDto( /*vivienda.toDto(),*/ img?.id, id)

fun ImagenVivienda.toPropiaDto() = ImagenViviendaPropiaDto(img?.id, img?.deletehash,id)