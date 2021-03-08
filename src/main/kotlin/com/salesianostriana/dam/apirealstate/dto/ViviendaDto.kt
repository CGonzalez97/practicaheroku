package com.salesianostriana.dam.apirealstate.dto

import com.salesianostriana.dam.apirealstate.models.ImagenVivienda
import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.models.Vivienda
import org.hibernate.validator.constraints.Range
import javax.persistence.Lob
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class ListadoViviendaDto(var id:Long,
                              @get:NotBlank(message="{vivienda.titulo.blank}")
                              var titulo: String,
                              @get:Min(0, message = "{vivienda.precio.min}")
                              var precio: Double,
                              @get:Min(1, message = "{vivienda.metrosCuadrados.min}")
                              var metrosCuadrados: Int,
                              @get:Range(min = 1, max = 4, message = "{vivienda.numHabitaciones.range}")
                              @get:NotBlank(message = "{vivienda.numHabitaciones.blank}")
                              var numeroHabitaciones: Int,
                              @get: NotBlank(message = "{vivienda.localidad.blank}")
                              var localidad: String,
                              @get:NotBlank(message = "{vivienda.provincia.blank")
                              var provincia: String,
                              @get:NotBlank(message = "{vivienda.categoria.blank}")
                              var categoria: String)

data class UnaViviendaDto(var id:Long,
                          @get:NotBlank(message="{vivienda.titulo.blank}")
                          var titulo: String,
                          @Lob
                          @get:NotNull
                          var descripcion: String,
                          @get:Min(0, message = "{vivienda.precio.min}")
                          var precio: Double,
                          @get:Min(1, message = "{vivienda.metrosCuadrados.min}")
                          var metrosCuadrados: Int?,
                          @get:Range(min = 1, max = 4, message = "{vivienda.numHabitaciones.range}")
                          @get:NotBlank(message = "{vivienda.numHabitaciones.blank}")
                          var numHabitaciones: Int,
                          @get:NotNull(message="{vivienda.direccion.null}")
                          var direccion: String,
                          @get: NotBlank(message = "{vivienda.localidad.blank}")
                          var localidad: String,
                          @get:NotBlank(message = "{vivienda.provincia.blank")
                          var provincia: String,
                          @get:Size(message = "{vivienda.coordenadas.size}",min = 2, max = 2)
                          var coordenadas: List<Double>?,
                          @get:NotBlank(message = "{vivienda.categoria.blank}")
                          var categoria: String,
                          @get:NotBlank(message = "{UnaViviendaDto.propietario.blank}")
                          var propietario: PropietarioDto,
                          var imagenes: List<ImagenDto>?)


fun Vivienda.toDto() = ListadoViviendaDto(
        id!!,
        titulo,
        precio,
        metrosCuadrados,
        numHabitaciones,
        localidad,
        provincia,
        categoria,
)

fun Vivienda.toSpecificDto() = UnaViviendaDto(
        id!!,
        titulo,
        descripcion,
        precio,
        metrosCuadrados,
        numHabitaciones,
        direccion,
        localidad,
        provincia,
        coordenadas,
        categoria,
        usuario!!.toPropietarioDto(),
        imagenes.map { it.toDto() }
)

data class MiVivienda(var id:Long,
                      @get:NotBlank(message="{vivienda.titulo.blank}")
                      var titulo: String,
                      @Lob
                      @get:NotNull
                      var descripcion: String,
                      @get:Min(0, message = "{vivienda.precio.min}")
                      var precio: Double,
                      @get:Min(1, message = "{vivienda.metrosCuadrados.min}")
                      var metrosCuadrados: Int?,
                      @get:Range(min = 1, max = 4, message = "{vivienda.numHabitaciones.range}")
                      @get:NotBlank(message = "{vivienda.numHabitaciones.blank}")
                      var numHabitaciones: Int,
                      @get:NotNull(message="{vivienda.direccion.null}")
                      var direccion: String,
                      @get: NotBlank(message = "{vivienda.localidad.blank}")
                      var localidad: String,
                      @get:NotBlank(message = "{vivienda.provincia.blank")
                      var provincia: String,
                      @get:Size(message = "{vivienda.coordenadas.size}",min = 2, max = 2)
                      var coordenadas: List<Double>?,
                      @get:NotBlank(message = "{vivienda.categoria.blank}")
                      var categoria: String,
                      @get:NotBlank(message = "{UnaViviendaDto.propietario.blank}")
                      var propietario: PropietarioDto,
                      var imagenes: List<ImagenViviendaPropiaDto>?)

fun Vivienda.toPropiaDto() = MiVivienda(
        id!!,
        titulo,
        descripcion,
        precio,
        metrosCuadrados,
        numHabitaciones,
        direccion,
        localidad,
        provincia,
        coordenadas,
        categoria,
        usuario!!.toPropietarioDto(),
        imagenes.map { it.toPropiaDto() }
)