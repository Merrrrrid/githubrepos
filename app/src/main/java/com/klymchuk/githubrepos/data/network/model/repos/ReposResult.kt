package com.klymchuk.githubrepos.data.network.model.repos

import com.google.gson.annotations.SerializedName

data class ReposResult(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val reposItems: List<ReposItem>,
    @SerializedName("total_count")
    val totalCount: Int
)