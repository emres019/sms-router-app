package com.github.emresarincioglu.smsrouter.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.emresarincioglu.smsrouter.core.database.dao.EmailAccountDao
import com.github.emresarincioglu.smsrouter.core.database.dao.GatewayDao
import com.github.emresarincioglu.smsrouter.core.database.dao.MessageDao
import com.github.emresarincioglu.smsrouter.core.database.dao.RouteDao
import com.github.emresarincioglu.smsrouter.core.database.dao.SearchHistoryDao
import com.github.emresarincioglu.smsrouter.core.database.dao.SourceDao
import com.github.emresarincioglu.smsrouter.core.database.entity.EmailAccountEntity
import com.github.emresarincioglu.smsrouter.core.database.entity.GatewayEntity
import com.github.emresarincioglu.smsrouter.core.database.entity.MessageEntity
import com.github.emresarincioglu.smsrouter.core.database.entity.RouteEntity
import com.github.emresarincioglu.smsrouter.core.database.entity.SearchHistoryEntity
import com.github.emresarincioglu.smsrouter.core.database.entity.SourceEntity

@Database(
    entities = [
        SourceEntity::class,
        GatewayEntity::class,
        RouteEntity::class,
        MessageEntity::class,
        EmailAccountEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun gatewayDao(): GatewayDao
    abstract fun routeDao(): RouteDao
    abstract fun messageDao(): MessageDao
    abstract fun emailAccountDao(): EmailAccountDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}