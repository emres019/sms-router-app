package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert
    fun add(entity: T)

    @Insert
    fun addAll(vararg entities: T)

    @Update
    fun update(entity: T)

    @Update
    fun updateAll(vararg entities: T)

    @Delete
    fun delete(entity: T)

    @Delete
    fun deleteAll(vararg entities: T)
}