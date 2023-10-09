package com.github.emresarincioglu.smsrouter.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "route",
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
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "source_id") val sourceId: Int,
    val destination: String,
    @ColumnInfo(name = "gateway_id") val gatewayId: Int,
    @ColumnInfo("message_prefix") val messagePrefix: String,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean
)
