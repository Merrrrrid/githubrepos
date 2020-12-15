package com.klymchuk.githubrepos.data.repositories

import com.klymchuk.githubrepos.data.db.AppDatabase
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.db.entity.User
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val mDatabase: AppDatabase,
) {

    fun insertUser(user: User): Completable {
        return Completable.fromAction {
            mDatabase.userDao().insertUser(user)
        }
    }

    fun insertHistoryItem(history: History): Completable {
        return Completable.fromAction {
            mDatabase.historyDao().insertHistory(history)
        }
    }

    fun getHistory(): Single<List<History>> {
        return mDatabase.historyDao().getHistory()
    }

}