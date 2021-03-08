package com.salesianostriana.dam.apirealstate.security

import com.salesianostriana.dam.apirealstate.dto.UsuarioDto
import com.salesianostriana.dam.apirealstate.dto.toDto
import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.services.UsuarioServicio
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
class AuthenticationController(
        private val authenticationManager: AuthenticationManager,
        private val jwtTokenProvider: JwtTokenProvider,
        private val bearerTokenExtractor: BearerTokenExtractor,
        private val userService: UsuarioServicio,
        private val encoder: BCryptPasswordEncoder
){
    /**
     * Método que procesa la petición de login. Recibe un dto con la información de login
     * y si los datos son correctos, devuelve una respuesta con los datos de usuario,
     * un token de autenticación y uno de refresco.
     */
    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody loginRequest : LoginRequest) : ResponseEntity<JwtUserResponse> {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.username, loginRequest.password
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val user = authentication.principal as Usuario
        val jwtToken = jwtTokenProvider.generateToken(user)
        val jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user)

        return ResponseEntity.status(HttpStatus.CREATED).body(JwtUserResponse(jwtToken, jwtRefreshToken, user.toDto()))
    }


    /**
     * Método que procesa la petición de refresco del token. Esta operación se puede realizar
     * cuando ha caducado el token de autenticación y disponemos de un token de refresco,
     * de forma que no tenemos que repetir la operación de login.
     *
     * Si la operación tiene exito, la respuesta es la misma que con el login, en otro caso
     * se lanza un error.
     */
    @PostMapping("/auth/token")
    fun refreshToken(request : HttpServletRequest) : ResponseEntity<JwtUserResponse> {

        val refreshToken = bearerTokenExtractor.getJwtFromRequest(request).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al procesar el token de refresco")
        }
        try {
            if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
                val userId = jwtTokenProvider.getUserIdFromJWT(refreshToken)
                val user: Usuario = userService.findById(userId)
                val jwtToken = jwtTokenProvider.generateToken(user)
                val jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user)

                return ResponseEntity.status(HttpStatus.CREATED).body(JwtUserResponse
                    (jwtToken, jwtRefreshToken, user.toDto()))
            }
        } catch (ex : Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error en la validación del token")
        }
        return ResponseEntity.badRequest().build()
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/me")
    fun me(@AuthenticationPrincipal user : Usuario) = user.toDto()

    @PostMapping("/auth/register")
    fun registerUser(@Valid @RequestBody user:Usuario): ResponseEntity<UsuarioDto> =
    ResponseEntity.status(HttpStatus.CREATED).body(userService.create(user).orElseThrow {
            ResponseStatusException(HttpStatus.PRECONDITION_FAILED)
        }.toDto())
}

data class LoginRequest(
        @NotBlank val username : String,
        @NotBlank val password: String
)

data class JwtUserResponse(
        val token: String,
        val refreshToken: String,
        val user : UsuarioDto
)