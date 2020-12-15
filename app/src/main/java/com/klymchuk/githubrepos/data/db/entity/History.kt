package com.klymchuk.githubrepos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey
    val id: Int = 0,
    val htmlUrl: String = "",
    val fullName: String = "",
    val description: String = "",
    val stargazersCount: Int = 0,
    val inputTimeStamp: String,
)