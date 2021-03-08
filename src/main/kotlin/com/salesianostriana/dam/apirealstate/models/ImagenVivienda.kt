package com.salesianostriana.dam.apirealstate.models

import com.salesianostriana.dam.apirealstate.imageupload.ImgurImageAttribute
import org.hibernate.validator.constraints.URL
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class ImagenVivienda (

        @ManyToOne
        var vivienda:Vivienda,



        var img: ImgurImageAttribute? = null,


        @Id @GeneratedValue
        var id: Long? = null

        ){
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || javaClass != other.javaClass) return false
                val that = other as ImagenVivienda
                if (id != that.id) return false
                return true
        }
        override fun hashCode(): Int {
                return if (id != null)
                        id.hashCode()
                else 0
        }
}