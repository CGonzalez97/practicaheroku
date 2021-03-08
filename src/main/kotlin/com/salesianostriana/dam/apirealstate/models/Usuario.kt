package com.salesianostriana.dam.apirealstate.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.validation.constraints.*

@Entity
class Usuario(

        @Column(nullable = false, unique = true)
        @get:NotBlank(message="{usuario.username.blank}")
        private var username:String,

        @get:NotBlank(message="{usuario.password.blank}")
        private var password:String,

        @Column(nullable = false, unique = true)
        @get:NotBlank(message="{usuario.email.blank}")
        @get:Email(message="{usuario.email.email}")
        var email:String,

        @get:NotBlank(message="{usuario.nombrecompleto.blank}")
        var nombreCompleto:String,

        @get:PastOrPresent(message="{usuario.fechaalta.date}")
        var fechaAlta:Date = Date(),
        @get:Past(message="{usuario.fechanacimiento.date}")
        var fechaNacimiento:Date,

        @get:NotNull(message="{usuario.activo.null}")
        var activo:Boolean = true,

        @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
        var propiedades:MutableList<Vivienda> = mutableListOf(),

        @ManyToMany(mappedBy = "likes")
        var meGusta:MutableList<Vivienda> = mutableListOf(),

        @ElementCollection(fetch = FetchType.EAGER)
        val roles: MutableSet<String> = HashSet(),

        private val nonExpired: Boolean = true,

        private val nonLocked: Boolean = true,

        private val enabled: Boolean = true,

        private val credentialsNonExpired : Boolean = true,

        @Id @GeneratedValue
        var id:UUID? = null
): UserDetails {

        constructor(username:String,password:String,email:String,nombreCompleto:String,
        fechaNacimiento:Date,rol:String):this(username,password,email,nombreCompleto,Date(),fechaNacimiento,
        true,mutableListOf<Vivienda>(),mutableListOf<Vivienda>(),mutableSetOf(rol),
        true,true,true,true)

        fun addPropiedad(vivienda:Vivienda){
                this.propiedades.add(vivienda)
                vivienda.usuario=this
        }
        fun removePropiedad(vivienda:Vivienda){
                this.propiedades.remove(vivienda)
                vivienda.usuario=null
        }

        fun addMeGusta(vivienda:Vivienda){
                this.meGusta.add(vivienda)
                vivienda.likes!!.add(this)
        }
        fun removeMeGusta(vivienda:Vivienda){
                this.meGusta.remove(vivienda)
                vivienda.likes!!.remove(this)
        }

        override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
                roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList()

        override fun isEnabled() = enabled
        override fun getUsername() = username
        override fun isCredentialsNonExpired() = credentialsNonExpired
        override fun getPassword() = password
        override fun isAccountNonExpired() = nonExpired
        override fun isAccountNonLocked() = nonLocked

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as Usuario
                if (id != other.id) return false
                return true
        }

        override fun hashCode(): Int {
                return id?.hashCode() ?: 0
        }
}