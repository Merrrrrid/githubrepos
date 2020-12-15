package com.klymchuk.githubrepos.data.repositories

import com.klymchuk.githubrepos.data.db.AppDatabase
import com.klymchuk.githubrepos.data.db.model.User
import io.reactivex.Completable
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val mDatabase: AppDatabase,
) {

    fun insertUser(user: User): Completable {
        return Completable.fromAction {
            mDatabase.userDao().insertUser(user)
        }
    }

}