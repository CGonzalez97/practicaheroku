package com.salesianostriana.dam.apirealstate.services

import com.salesianostriana.dam.apirealstate.dto.UsuarioDto
import com.salesianostriana.dam.apirealstate.errors.SingleEntityNotFoundException
import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.repositories.UsusarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UsuarioServicio {

    @Autowired
    lateinit var usuarioRepo: UsusarioRepository
    @Autowired
    lateinit var encoder : PasswordEncoder

    fun findByUsername(username : String) = usuarioRepo.findByUsername(username)

    fun create(nuevo : Usuario): Optional<Usuario> {
        if (findByUsername(nuevo.username).isPresent)
            return Optional.empty()
        return Optional.of(
                with(nuevo) {
                    usuarioRepo.save(Usuario(
                            nuevo.username,
                            encoder.encode(nuevo.password),
                            nuevo.email,
                            nuevo.nombreCompleto,
                            nuevo.fechaNacimiento,
                            "USER"
                    ))
                }
        )
    }

    fun findById(id:UUID) =
            usuarioRepo.findById(id).orElseThrow {
                SingleEntityNotFoundException(id.toString(),Usuario::class.java)
            }

}