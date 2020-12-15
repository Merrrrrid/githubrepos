package com.klymchuk.githubrepos.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klymchuk.githubrepos.data.db.model.User

@Database(
    entities = [
        User::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}