package com.github.emresarincioglu.smsrouter.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.github.emresarincioglu.smsrouter.core.database.entity.EmailAccountEntity

@Dao
interface EmailAccountDao : BaseDao<EmailAccountEntity> {

    @Query("SELECT * FROM email_account WHERE id = :accountId LIMIT 1")
    fun getById(accountId: Int): EmailAccountEntity

    @Query("SELECT email FROM email_account")
    fun getAllEmails(): List<String>

    @Query("DELETE FROM email_account")
    fun deleteAll()
}