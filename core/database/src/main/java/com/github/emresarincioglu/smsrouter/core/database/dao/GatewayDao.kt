package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.emresarincioglu.smsrouter.core.database.entity.GatewayEntity

@Dao
interface GatewayDao: BaseDao<GatewayEntity> {

    @Query("SELECT name FROM gateway WHERE id = :typeId")
    fun getById(typeId: Int): String

    @Query("SELECT name FROM gateway")
    fun getAll(): List<String>
}