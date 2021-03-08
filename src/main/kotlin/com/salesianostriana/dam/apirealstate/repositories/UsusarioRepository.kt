package com.salesianostriana.dam.apirealstate.repositories

import org.springframework.data.jpa.repository.JpaRepository
import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.models.Vivienda
import java.util.*

interface UsusarioRepository :JpaRepository<Usuario,UUID>{

    fun findByUsername(username : String) : Optional<Usuario>
    fun findByEmail(email:String):List<Usuario>
    fun findByMeGustaContains(vivienda:Vivienda): MutableList<Usuario>

    fun findByPropiedadesContains(vivienda:Vivienda): Optional<Usuario>

}