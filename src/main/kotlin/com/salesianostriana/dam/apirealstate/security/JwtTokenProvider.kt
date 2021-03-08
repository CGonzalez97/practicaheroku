package com.salesianostriana.dam.apirealstate.security

import com.salesianostriana.dam.apirealstate.models.Usuario
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.expression.common.ExpressionUtils.convertTypedValue
import org.springframework.expression.common.ExpressionUtils.toLong
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class JwtTokenProvider {
    companion object{
        const val TOKEN_HEADER = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
        const val TOKEN_TYPE = "JWT"
    }

    private val jwtSecreto : String = "mJI.w|g!kCv(5bLr0Agr6TC,N9mNM]hy34F9h0[?!KB1~IMakIj40€f[_Pm_v(asdfghasdfg"
    private val jwtDuracionToken : Long = 3
    private val jwtDuracionRefreshToken : Long = 10


    private val parser = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecreto.toByteArray())).build()

    private val logger : Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private fun generateTokens(usuario: Usuario, isRefreshToken : Boolean) : String{

        val tokenExpirationDate =
            Date.from(Instant.now()
                .plus(if (isRefreshToken) jwtDuracionRefreshToken else jwtDuracionToken, ChronoUnit.DAYS))

        val builder = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(jwtSecreto.toByteArray()), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", TOKEN_TYPE)
            .setSubject(usuario.id.toString())
            .setExpiration(tokenExpirationDate)
            .setIssuedAt(Date())
            .claim("refresh", isRefreshToken)

        if(!isRefreshToken){
            builder
                .claim("fullname", usuario.nombreCompleto)
                .claim("roles", usuario.roles.joinToString())
        }
        return builder.compact()
    }

    /**
     * Método privado usado para validar un token. Comprueba que no hay error en la firma,
     * que está bien formado, que no ha expirado, que está completo y soportado.
     * Además, comprueba que es el tipo de token adecuado, es decir, que no estamos
     * usando el token de refresco para hacer peticiones.
     */
    private fun validateToken(token : String, isRefreshToken: Boolean) : Boolean {
        try {
            val claims = parser.parseClaimsJws(token)
            if (isRefreshToken != claims.body["refresh"])
                throw UnsupportedJwtException("No se ha utilizado el token apropiado")
            return true
        } catch (ex : Exception) {
            with(logger) { // Dejará de fallar una vez reciba de la clase JwtAuthorizationFilter el filtro logger
                when (ex) {
                    is SignatureException -> info("Error en la firma del token ${ex.message}")
                    is MalformedJwtException -> info("Token malformado ${ex.message}")
                    is ExpiredJwtException -> info("Token expirado ${ex.message}")
                    is UnsupportedJwtException -> info("Token no soportado ${ex.message}")
                    is IllegalArgumentException -> info("Token incompleto (claims vacío) ${ex.message}")
                    else -> info("Error indeterminado")
                }
            }
        }
        return false
    }


    /**
     * Método que se encarga de generar el token de autenticación
     * para el usuario actualmente autenticado
     */
    fun generateToken(authentication : Authentication) : String {
        val user : Usuario = authentication.principal as Usuario
        return generateTokens(user, false)
    }

    /**
     * Método encargado de generar el token de autenticación para un
     * determinado usuario
     */
    fun generateToken(user : Usuario) = generateTokens(user, false)


    /**
     * Método encargado de generar el token de refresco para el
     * usuario actualmente autenticado
     */
    fun generateRefreshToken(authentication: Authentication) : String {
        val user : Usuario = authentication.principal as Usuario
        return generateTokens(user, true)
    }

    /**
     * Método encargado de generar el token de refresco para un usuario
     */
    fun generateRefreshToken(user : Usuario) = generateTokens(user, true)

    /**
     * Método que recibe el token y devuelve el identificador del usuario
     */
    fun getUserIdFromJWT(token: String): UUID = UUID.fromString(parser.parseClaimsJws(token).body.subject)

    /**
     * Método que recibe un token de refresco y lo valida
     */
    fun validateRefreshToken(token : String) = validateToken(token, true)

    /**
     * Método que recibe un token de autenticación y lo valida
     */
    fun validateAuthToken(token : String) = validateToken(token, false)

}