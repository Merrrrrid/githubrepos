package com.klymchuk.githubrepos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klymchuk.githubrepos.data.db.entity.History

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History)

    @Query("SELECT * FROM history ORDER BY inputTimeStamp DESC LIMIT 20")
    suspend fun getHistory() : List<History>

    @Query("SELECT id FROM history")
    suspend fun getHistoryIds() : List<Int>

}