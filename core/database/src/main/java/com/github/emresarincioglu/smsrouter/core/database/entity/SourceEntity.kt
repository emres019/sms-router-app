package com.github.emresarincioglu.smsrouter.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "source",
    indices = [Index(value = ["name"], unique = true), Index(value = ["address"], unique = true)]
)
data class SourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val pattern: String
)