package com.salesianostriana.dam.apirealstate.repositories

import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.models.Vivienda
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ViviendaRepository : JpaRepository<Vivienda,Long>{

    fun findByCategoria(categoria:String): List<Vivienda>
    fun findByLocalidad(localidad:String):List<Vivienda>
    fun findByPrecio(precio:Double):List<Vivienda>
    fun findByCategoriaAndLocalidad(categoria:String,localidad:String):List<Vivienda>
    fun findByCategoriaAndPrecio(categoria:String,precio:Double):List<Vivienda>
    fun findByLocalidadAndPrecio(localidad:String,precio:Double):List<Vivienda>
    fun findByCategoriaAndLocalidadAndPrecio(categoria:String,localidad:String,precio:Double): List<Vivienda>
    fun findByLikesContains(usuario:Usuario): MutableList<Vivienda>
    fun findByUsuario(usuario:Usuario): List<Vivienda>

}