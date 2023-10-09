package com.github.emresarincioglu.smsrouter.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "gateway", indices = [Index(value = ["name"], unique = true)])
data class GatewayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)