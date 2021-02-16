package com.klymchuk.githubrepos.data.repositories

import com.klymchuk.githubrepos.data.db.AppDatabase
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.db.entity.User
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val mDatabase: AppDatabase,
) {

    suspend fun insertUser(user: User) {
        mDatabase.userDao().insertUser(user)
    }

    suspend fun getUser(): User? {
        return mDatabase.userDao().getUser()
    }

    suspend fun getUserToken(): String?{
        return mDatabase.userDao().getUserToken()
    }

    suspend fun insertHistoryItem(history: History) {
            mDatabase.historyDao().insertHistory(history)
    }

    suspend fun getHistory(): List<History> {
        return mDatabase.historyDao().getHistory()
    }

    suspend fun getHistoryIds(): List<Int>{
        return mDatabase.historyDao().getHistoryIds()
    }

}