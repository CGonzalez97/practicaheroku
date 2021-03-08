package com.salesianostriana.dam.apirealstate.services

import com.salesianostriana.dam.apirealstate.dto.ListadoViviendaDto
import com.salesianostriana.dam.apirealstate.dto.toDto
import com.salesianostriana.dam.apirealstate.errors.ListEntityNotFoundException
import com.salesianostriana.dam.apirealstate.errors.SingleEntityNotFoundException
import com.salesianostriana.dam.apirealstate.errors.SingleEntityNotFoundExceptionSinId
import com.salesianostriana.dam.apirealstate.models.Usuario
import com.salesianostriana.dam.apirealstate.models.Vivienda
import com.salesianostriana.dam.apirealstate.repositories.ImagenRepository
import com.salesianostriana.dam.apirealstate.repositories.UsusarioRepository
import com.salesianostriana.dam.apirealstate.repositories.ViviendaRepository
import com.salesianostriana.dam.apirealstate.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class ViviendaServicio {

    @Autowired
    lateinit var viviendaRepo: ViviendaRepository
    @Autowired
    lateinit var usuarioRepo: UsusarioRepository
    @Autowired
    lateinit var usuarioServicio: UsuarioServicio
    @Autowired
    lateinit var jwt: JwtTokenProvider
    @Autowired
    lateinit var imagenRepo:ImagenRepository

    fun findAll():List<Vivienda> {
        var resultado = viviendaRepo.findAll()
        if (!resultado.isEmpty()) {
            return resultado
        } else {
            throw ListEntityNotFoundException(Vivienda::class.java)
        }
    }

    fun findAllFavs(token:String) = usuarioRepo.findById(jwt.getUserIdFromJWT(token.split(" ")
        .toTypedArray()[1])).map {
        usu -> viviendaRepo.findByLikesContains(usu).map { it.toDto() }//usu.meGusta
        }.orElseThrow {
            SingleEntityNotFoundException(jwt.getUserIdFromJWT(token.split(" ")
                .toTypedArray()[1]).toString(),Usuario::class.java)
        }

    fun findById(id:Long): Vivienda  =
        viviendaRepo.findById(id).orElseThrow {
            SingleEntityNotFoundException(id.toString(),Vivienda::class.java)
        }

    fun  getViviendaById(id:Long): Vivienda {
        var vivienda = findById(id)
        var imagenes = imagenRepo.findByVivienda(vivienda)
        vivienda.imagenes = imagenes
        return vivienda
    }


    fun deleteFavById(idV:Long, token:String){
        var vivienda: Vivienda = findById(idV)
        var usu: Usuario = usuarioServicio.findById(jwt.getUserIdFromJWT(token.split(" ").toTypedArray()[1]))
        usu.meGusta = viviendaRepo.findByLikesContains(usu)
        var likesVivienda = usuarioRepo.findByMeGustaContains(vivienda)
        vivienda.likes = likesVivienda
        usu.removeMeGusta(vivienda)
        usuarioRepo.save(usu)
        viviendaRepo.save(vivienda)
    }

    fun searchVivienda(categoria: String, ciudad: String, precio: Double): ResponseEntity<List<ListadoViviendaDto>> {
        var resultado: List<Vivienda>
        if (categoria == "todas" && ciudad == "todas" && precio == 99999999.0) {
            resultado = findAll()
        } else if (categoria == "todas" && ciudad == "todas") {
            resultado = viviendaRepo.findByPrecio(precio)
        } else if (categoria == "todas" && precio == 99999999.0) {
            resultado = viviendaRepo.findByLocalidad(ciudad)
        } else if (ciudad == "todas" && precio == 99999999.0) {
            resultado = viviendaRepo.findByCategoria(categoria)
        } else if(categoria == "todas"){
            resultado = viviendaRepo.findByLocalidadAndPrecio(ciudad,precio)
        }else if(ciudad == "todas"){
            resultado = viviendaRepo.findByCategoriaAndPrecio(categoria,precio)
        }else if(precio == 99999999.0){
            resultado = viviendaRepo.findByCategoriaAndLocalidad(categoria, ciudad)
        }else {
            resultado = viviendaRepo.findByCategoriaAndLocalidadAndPrecio(categoria, ciudad, precio)
        }

        if (!resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(resultado.map { it.toDto() })
        } else {
            throw ListEntityNotFoundException(Vivienda::class.java)
        }
    }

    fun createVivienda(viviendaNueva: Vivienda,token:String): ResponseEntity<ListadoViviendaDto> {
        var usuario = usuarioRepo.findById(jwt.getUserIdFromJWT(token.split(" ")
            .toTypedArray()[1])).orElseThrow {
            SingleEntityNotFoundException(jwt.getUserIdFromJWT(token.split(" ")
                .toTypedArray()[1]).toString(),Usuario::class.java)
        }
        var vivienda = viviendaRepo.save(viviendaNueva)
        usuario.addPropiedad(vivienda)
        usuarioRepo.save(usuario)
        viviendaRepo.save(vivienda)
        return ResponseEntity.status(HttpStatus.CREATED).body(vivienda.toDto())
    }

    fun getListOfViviendas(token: String): List<Vivienda> {
        var idUsuario = jwt.getUserIdFromJWT(token.split(" ").toTypedArray()[1])
        var result = usuarioRepo.findById(idUsuario).orElseThrow {
            SingleEntityNotFoundException(idUsuario.toString(),Usuario::class.java)
        }
        var viviendas = viviendaRepo.findByUsuario(result)
        viviendas.map {
            it.imagenes = imagenRepo.findByVivienda(it)
        }
        if (!viviendas.isEmpty())
            return viviendas
        else
            throw ListEntityNotFoundException(Vivienda::class.java)
    }

    fun modifyVivienda(id: Long, viviendaNueva: Vivienda): ResponseEntity<ListadoViviendaDto> = viviendaRepo.findById(id)
        .map { viviendaAModificar ->
            viviendaAModificar.titulo = viviendaNueva.titulo
            viviendaAModificar.descripcion = viviendaNueva.descripcion
            viviendaAModificar.precio = viviendaNueva.precio
            viviendaAModificar.numHabitaciones = viviendaNueva.numHabitaciones
            viviendaAModificar.metrosCuadrados = viviendaNueva.metrosCuadrados
            viviendaAModificar.direccion = viviendaNueva.direccion
            viviendaAModificar.localidad = viviendaNueva.localidad
            viviendaAModificar.provincia = viviendaNueva.provincia
            viviendaAModificar.coordenadas = viviendaNueva.coordenadas
            viviendaAModificar.categoria = viviendaNueva.categoria
            ResponseEntity.status(HttpStatus.OK).body(viviendaRepo.save(viviendaAModificar).toDto())
        }.orElseThrow {
            SingleEntityNotFoundException(id.toString(),Vivienda::class.java)
        }

    fun addViviendaToFav(vId: Long, token:String): ResponseEntity<ListadoViviendaDto> {
        var idUsuario = jwt.getUserIdFromJWT(token.split(" ").toTypedArray()[1])
        var usuario = usuarioRepo.findById(idUsuario).orElseThrow {
            SingleEntityNotFoundException(idUsuario.toString(),Usuario::class.java)
        }
        var v = viviendaRepo.findById(vId).orElseThrow {
            SingleEntityNotFoundException(vId.toString(),Vivienda::class.java)
        }
        var meGusta = viviendaRepo.findByLikesContains(usuario)
        var likes = usuarioRepo.findByMeGustaContains(v)
        var imagenes = imagenRepo.findByVivienda(v)
        v.likes = likes
        v.imagenes = imagenes
        usuario.meGusta = meGusta
        usuario.addMeGusta(v)
        usuarioRepo.save(usuario)
        viviendaRepo.save(v)
        return ResponseEntity.status(HttpStatus.OK).body(v.toDto())
    }

    fun deleteVivienda(id: Long): ResponseEntity<Any>{
        if(viviendaRepo.existsById(id)){
            var vivienda = viviendaRepo.findById(id).orElseThrow {
                SingleEntityNotFoundException(id.toString(),Vivienda::class.java)
            }
            var usuario = usuarioRepo.findByPropiedadesContains(vivienda).orElseThrow {
                SingleEntityNotFoundExceptionSinId(Usuario::class.java)
            }
            usuario.removePropiedad(vivienda)
            usuarioRepo.save(usuario)
            viviendaRepo.deleteById(id)
        }
        return ResponseEntity.noContent().build()
    }

}