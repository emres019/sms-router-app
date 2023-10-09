package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.emresarincioglu.smsrouter.core.database.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao : BaseDao<SearchHistoryEntity> {

    @Query("SELECT search FROM search_history WHERE search LIKE '%' || :search || '%' LIMIT :limit")
    fun getLikeSearch(search: String, limit: Int): List<String>

    @Query("DELETE FROM search_history WHERE search = :search")
    fun deleteBySearch(search: String)

    @Query("DELETE FROM search_history")
    fun deleteAll()
}