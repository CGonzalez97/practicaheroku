package com.salesianostriana.dam.apirealstate.imageupload

import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.*

@Service
class ImgurStorageServiceImpl(private val imgurService: ImgurService): ImgurStorageService {
    val logger: Logger = LoggerFactory.getLogger(ImgurStorageService::class.java)

    override fun store(file: MultipartFile) : Optional<ImgurImageAttribute> {

        if (!file.isEmpty) {
            var imgReq =
                    NewImageReq(Base64.getEncoder().encodeToString(file.bytes),
                            file.originalFilename.toString())
            var imgRes = imgurService.upload(imgReq)
            if(imgRes.isPresent)
                return Optional.of(ImgurImageAttribute(imgRes.get().data.id, imgRes.get().data.deletehash))
        }
        return Optional.empty()
    }

    override fun loadAsResource(id: String) : Optional<MediaTypeUrlResource> {
        var response = imgurService.get(id)
        if (response.isPresent) {
            var resource = MediaTypeUrlResource(response.get().data.type, URI.create(response.get().data.link))
            if (resource.exists() || resource.isReadable)
                return Optional.of(resource)
        }
        return Optional.empty()
    }

    override fun delete(deletehash: String) : Unit {
        logger.debug("Eliminando la imagen $deletehash")
        imgurService.delete(deletehash)
    }


}