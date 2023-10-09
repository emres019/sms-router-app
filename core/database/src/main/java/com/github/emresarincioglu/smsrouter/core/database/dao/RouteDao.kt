package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.emresarincioglu.smsrouter.core.database.entity.RouteEntity

@Dao
interface RouteDao: BaseDao<RouteEntity> {

    @Query("SELECT * FROM route ORDER BY gateway_id ASC")
    fun getAll(): List<RouteEntity>

    @Query("SELECT * FROM route WHERE source_id = :sourceId ORDER BY gateway_id ASC")
    fun getAllBySourceId(sourceId: Int): List<RouteEntity>
}