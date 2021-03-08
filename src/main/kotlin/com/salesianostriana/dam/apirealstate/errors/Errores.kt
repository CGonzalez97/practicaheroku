package com.salesianostriana.dam.apirealstate.errors

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ApiError(
        var estado: HttpStatus,
        var mensaje: String?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        var subErrores: List<out ApiSubError>? = null,

        @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
        var fecha: LocalDateTime = LocalDateTime.now()
)

open abstract class ApiSubError

data class ApiValidationSubError(
    var objeto : String,
    var campo : String,
    var valorRechazado : Any?,
    var mensaje : String?
) : ApiSubError()