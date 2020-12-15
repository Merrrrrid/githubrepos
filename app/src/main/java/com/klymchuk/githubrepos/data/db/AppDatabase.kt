package com.klymchuk.githubrepos.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.db.entity.User

@Database(
    entities = [
        User::class,
        History::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun historyDao(): HistoryDao
}