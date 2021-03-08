package com.salesianostriana.dam.apirealstate.models

import org.hibernate.FetchMode
import org.hibernate.annotations.Fetch
import org.hibernate.validator.constraints.Range
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.util.*

@Entity
class Vivienda (@get:NotBlank(message="{vivienda.titulo.blank}")
                var titulo: String,

                @Lob
                @get:NotNull
                var descripcion: String,

                @get:Min(0, message = "{vivienda.precio.min}")
                @get:NotNull(message = "{vivienda.precio.null}")
                var precio: Double,

                @get:Range(min = 1, max = 4, message = "{vivienda.numHabitaciones.range}")
                @get:NotNull(message = "{vivienda.numHabitaciones.null}")
                var numHabitaciones: Int,

                @get:Min(1, message = "{vivienda.metrosCuadrados.min}")
                var metrosCuadrados: Int,

                @get:NotNull(message="{vivienda.direccion.null}")
                var direccion: String,

                @get: NotBlank(message = "{vivienda.localidad.blank}")
                var localidad: String,

                @get:NotBlank(message = "{vivienda.provincia.blank")
                var provincia: String,

                @ElementCollection(fetch = FetchType.EAGER)
                @get:Size(message = "{vivienda.coordenadas.size}",min = 2, max = 2)
                var coordenadas: MutableList<Double>? = mutableListOf(),

                @get:NotBlank(message = "{vivienda.categoria.blank}")
                var categoria: String,

                @ManyToOne
                var usuario: Usuario?,

                @ManyToMany
                var likes: MutableList<Usuario>?= mutableListOf(),

                @OneToMany(mappedBy = "vivienda")
                var imagenes: MutableList<ImagenVivienda> = mutableListOf(),

                @Id @GeneratedValue
                var id: Long?){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Vivienda
        if (id != that.id) return false
        return true
    }

    override fun hashCode(): Int {
        return if (id != null)
            id.hashCode()
        else 0
    }

    //helper imagen
    fun addImagen(img:ImagenVivienda){
        imagenes.add(img)
    }
    fun removeImagen(img:ImagenVivienda){
        imagenes.remove(img)
    }
}