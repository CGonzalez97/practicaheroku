package com.salesianostriana.dam.apirealstate.errors

import javax.persistence.EntityNotFoundException

open class EntityNotFoundException(val msg: String) : RuntimeException(msg)

data class SingleEntityNotFoundException(
        val id: String,
        val javaClass: Class<out Any>
) : EntityNotFoundException("No se puede encontrar la entidad de tipo ${javaClass.name} con ID: ${id}")

data class SingleEntityNotFoundExceptionSinId(
        val javaClass: Class<out Any>
) : EntityNotFoundException("No se puede encontrar la entidad de tipo ${javaClass.name}")

data class ListEntityNotFoundException(
        val javaClass: Class<out Any>):
        EntityNotFoundException("No se puede encontrar elementos del tipo ${javaClass.name} para esa consulta. ")

data class ImageNotFoundByHashException(
        val hash: String,
        val javaClass: Class<out Any>
) : EntityNotFoundException("No se puede encontrar la imagen de tipo ${javaClass.name} con deleteHash: ${hash}")

