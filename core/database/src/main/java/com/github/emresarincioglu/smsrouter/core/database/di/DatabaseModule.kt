package com.github.emresarincioglu.smsrouter.core.database.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.emresarincioglu.smsrouter.core.database.MainDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MainDatabase::class.java, "main-database")
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    db.insert("gateway", CONFLICT_ABORT, ContentValues().apply {
                        put("name", "SMS")
                    })
                    db.insert("gateway", CONFLICT_ABORT, ContentValues().apply {
                        put("name", "Email")
                    })
                    db.insert("gateway", CONFLICT_ABORT, ContentValues().apply {
                        put("name", "In-App")
                    })
                }
            }).build()
}