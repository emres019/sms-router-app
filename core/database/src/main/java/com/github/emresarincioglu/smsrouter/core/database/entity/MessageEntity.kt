package com.github.emresarincioglu.smsrouter.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = SourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GatewayEntity::class,
            parentColumns = ["id"],
            childColumns = ["gateway_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "source_id") val sourceId: Int,
    @ColumnInfo(name = "gateway_id") val gatewayId: Int,
    @ColumnInfo(name = "is_success") val isSuccess: Boolean,
    val timestamp: Long
)