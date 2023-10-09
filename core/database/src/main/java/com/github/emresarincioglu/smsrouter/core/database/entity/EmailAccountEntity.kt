package com.github.emresarincioglu.smsrouter.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "email_account",
    indices = [Index(value = ["email"], unique = true), Index(value = ["iv"], unique = true)]
)
data class EmailAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val iv: ByteArray
)