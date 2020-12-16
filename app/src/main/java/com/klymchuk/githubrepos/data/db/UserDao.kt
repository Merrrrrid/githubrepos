package com.klymchuk.githubrepos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klymchuk.githubrepos.data.db.entity.User
import io.reactivex.Single

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Single<User?>

    @Query("SELECT token FROM user LIMIT 1")
    fun getUserToken(): Single<String?>

}