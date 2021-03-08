package com.salesianostriana.dam.apirealstate.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException

@Component
class JwtAuthenticationEntryPoint(val mapper: ObjectMapper): AuthenticationEntryPoint {

    override fun commence(request: HttpServletRequest?, response: HttpServletResponse?,
                          authException: AuthenticationException?) {
        response?.status = 401
        response?.contentType = "application/json"
        response?.writer?.println(mapper.writeValueAsString(authException?.message?.let { MensajeError(it) }))
    }

}

data class MensajeError(
        val mensaje: String
)