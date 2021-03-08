package com.salesianostriana.dam.apirealstate.dto

import com.salesianostriana.dam.apirealstate.models.Usuario
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Past
import javax.validation.constraints.PastOrPresent

data class UsuarioDto (
        @get:NotBlank(message="{usuario.username.blank}")
        var username:String,

        @get:NotBlank(message="{usuario.email.blank}")
        @get:Email(message="{usuario.email.email}")
        var email:String,

        @get:NotBlank(message="{usuario.nombrecompleto.blank}")
        var nombreCompleto:String,

        @get:Past(message="{usuario.fechanacimiento.date}")
        var fechaNacimiento: Date,

        var activo:Boolean,
        var id:UUID?

)

data class PropietarioDto(
        @get:NotBlank(message="{usuario.nombrecompleto.blank}")
        var nombreCompleto:String,

)

fun Usuario.toDto(): UsuarioDto = UsuarioDto(
        username,
        email,
        nombreCompleto,
        fechaNacimiento,
        activo,
        id)

fun Usuario.toPropietarioDto(): PropietarioDto = PropietarioDto(nombreCompleto)