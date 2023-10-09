package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.emresarincioglu.smsrouter.core.database.entity.SourceEntity

@Dao
interface SourceDao : BaseDao<SourceEntity> {

    @Query("SELECT * FROM source WHERE id = :sourceId LIMIT 1")
    fun getById(sourceId: Int): SourceEntity

    @Query("SELECT * FROM source ORDER BY id ASC")
    fun getAll(): List<SourceEntity>

    @Query("SELECT * FROM source WHERE name LIKE '%' || :name || '%'")
    fun getAllByName(name: String): List<SourceEntity>
}