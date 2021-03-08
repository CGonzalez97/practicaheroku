package com.salesianostriana.dam.apirealstate.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository

open class BaseService<T, ID, R : JpaRepository<T, ID>>  {

    @Autowired
    protected lateinit var repository: R

    open fun save(t : T) = repository.save(t)
    open fun findAll() : List<T> = repository.findAll()
    open fun findById(id : ID) = repository.findById(id)
    open fun edit(t : T) = save(t)

    open fun deleteById(id : ID) {
        findById(id).ifPresent { this.delete(it) }
    }

    open fun delete(t : T) = repository.delete(t)

    open fun deleteAll() = repository.deleteAll()

}