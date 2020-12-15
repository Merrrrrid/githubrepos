package com.klymchuk.githubrepos.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: Int = 0,
    val token: String = "",
)
