package com.salesianostriana.dam.apirealstate.repositories

import com.salesianostriana.dam.apirealstate.models.ImagenVivienda
import com.salesianostriana.dam.apirealstate.models.Vivienda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import java.util.*
import javax.swing.text.html.Option

interface ImagenRepository : JpaRepository<ImagenVivienda, Long> {

    
    fun findByVivienda(vivienda: Vivienda): MutableList<ImagenVivienda>


}