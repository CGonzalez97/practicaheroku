package com.salesianostriana.dam.apirealstate.controllers

import com.salesianostriana.dam.apirealstate.errors.ApiError
import com.salesianostriana.dam.apirealstate.errors.ApiSubError
import com.salesianostriana.dam.apirealstate.errors.ApiValidationSubError
import org.hibernate.validator.internal.engine.path.PathImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
//import java.net.http.HttpHeaders
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders

@RestControllerAdvice
class RestControllerAdvice: ResponseEntityExceptionHandler(){

    @ExceptionHandler(value=[EntityNotFoundException::class])
    fun handleNotFoundException(ex: EntityNotFoundException) =
            ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiError(HttpStatus.NOT_FOUND, ex.message))

    override fun handleExceptionInternal(
            ex: Exception,
            body: Any?,
            headers: HttpHeaders,
            status: HttpStatus,
            request: WebRequest
    ): ResponseEntity<Any> {
        val apiError = ApiError(status, ex.message)
        return ResponseEntity.status(status).body(apiError)
    }

    @ExceptionHandler(value=[ConstraintViolationException::class])
    fun handleConstraintViolation(ex : ConstraintViolationException) =
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiError(
                                    HttpStatus.BAD_REQUEST,
                                    "Error de validación (handleConstraintViolation)",
                                    ex.constraintViolations
                                            .map { ApiValidationSubError(
                                                    it.rootBeanClass.simpleName, (it.propertyPath as PathImpl).leafNode.asString(), it.invalidValue, it.message)
                                            }
                            )
                    )


    fun handleMethodArgumentNotValid(
            ex: Exception,
            body: Any?,
            headers: HttpHeaders,
            status: HttpStatus,
            request: WebRequest
    ): ResponseEntity<Any> {
        val apiError = ApiError(status, ex.message)
        return ResponseEntity.status(status).body(apiError)
    }










}