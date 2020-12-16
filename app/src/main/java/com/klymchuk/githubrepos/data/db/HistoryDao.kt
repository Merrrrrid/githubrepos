package com.klymchuk.githubrepos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klymchuk.githubrepos.data.db.entity.History
import io.reactivex.Single

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(history: History)

    @Query("SELECT * FROM history ORDER BY inputTimeStamp DESC LIMIT 20")
    fun getHistory() : Single<List<History>>

    @Query("SELECT id FROM history")
    fun getHistoryIds() : Single<List<Int>>

}