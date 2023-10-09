package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.emresarincioglu.smsrouter.core.database.entity.MessageEntity

@Dao
interface MessageDao: BaseDao<MessageEntity> {

    @Query("SELECT MAX(timestamp) FROM message WHERE id = :sourceId")
    fun getLastMessageTimeBySourceId(sourceId: Int): Long

    @Query("SELECT * FROM message")
    fun getAll(): List<MessageEntity>

    @Query("SELECT * FROM message WHERE id IN (:messageIds)")
    fun getAllByIds(messageIds: IntArray): List<MessageEntity>

    @Query("SELECT * FROM message WHERE source_id = :sourceId ORDER BY timestamp DESC")
    fun getAllBySourceId(sourceId: Int): List<MessageEntity>

    @Query("SELECT * FROM message WHERE gateway_id = :gatewayId ORDER BY timestamp DESC")
    fun getAllByGatewayId(gatewayId: Int): List<MessageEntity>

    @Query("SELECT * FROM message WHERE is_success = :isSuccess ORDER BY timestamp DESC")
    fun getAllBySuccess(isSuccess: Boolean): List<MessageEntity>

    @Query("SELECT * FROM message WHERE timestamp >= :timestamp ORDER BY timestamp DESC")
    fun getAllAfterTime(timestamp: Long): List<MessageEntity>
}