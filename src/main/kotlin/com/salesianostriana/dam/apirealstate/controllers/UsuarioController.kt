package com.salesianostriana.dam.apirealstate.controllers

import com.salesianostriana.dam.apirealstate.dto.UsuarioDto
import com.salesianostriana.dam.apirealstate.dto.toDto
import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.services.UsuarioServicio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UsuarioController(val usuarioServicio: UsuarioServicio) {


    @PostMapping("/")
    fun nuevoUsuario(@Valid @RequestBody usuarioNuevo:Usuario):ResponseEntity<UsuarioDto> =
            usuarioServicio.create(usuarioNuevo).map { ResponseEntity.status(HttpStatus.CREATED).
            body(it.toDto()) }.orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ${usuarioNuevo.username} ya existe")
            }
}