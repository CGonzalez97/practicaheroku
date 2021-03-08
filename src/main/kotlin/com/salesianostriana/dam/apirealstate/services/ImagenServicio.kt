package com.salesianostriana.dam.apirealstate.services

import com.salesianostriana.dam.apirealstate.errors.ImageNotFoundByHashException
import com.salesianostriana.dam.apirealstate.errors.SingleEntityNotFoundException
import com.salesianostriana.dam.apirealstate.imageupload.ImgurImageAttribute
import com.salesianostriana.dam.apirealstate.imageupload.ImgurStorageService
import com.salesianostriana.dam.apirealstate.imageupload.ImgurStorageServiceImpl
import com.salesianostriana.dam.apirealstate.models.ImagenVivienda
import com.salesianostriana.dam.apirealstate.repositories.ImagenRepository
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Service
class ImagenServicio(
        private val imageStorageService: ImgurStorageService,
        private val imagenRepo: ImagenRepository,
        private val entityManager: EntityManager
) : BaseService<ImagenVivienda, Long, ImagenRepository>() {

    val logger: Logger = LoggerFactory.getLogger(ImagenServicio::class.java)



    fun save(e: ImagenVivienda, file: MultipartFile) : ImagenVivienda {
        var imageAttribute : Optional<ImgurImageAttribute> = Optional.empty()
        if (!file.isEmpty) {
            imageAttribute = imageStorageService.store(file)
        }
        if(imageAttribute != null){
            e.img!!.id = imageStorageService.loadAsResource(imageAttribute.get().id!!).get().uri.toString()
            //e.img!!.id = imageAttribute.get().id
            e.img!!.deletehash = imageAttribute.get().deletehash
        }
        return save(e)
    }

    override fun delete(e : ImagenVivienda) {
        logger.debug("Eliminando la entidad $e")
        e.img?.let { it.deletehash?.let { it1 -> imageStorageService.delete(it1) } }
        super.delete(e)
    }

}