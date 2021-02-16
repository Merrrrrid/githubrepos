package com.klymchuk.githubrepos.data.repositories

import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.data.network.GitHubApiService
import com.klymchuk.githubrepos.data.network.GitHubAuthService
import com.klymchuk.githubrepos.data.network.model.AccessToken
import com.klymchuk.githubrepos.data.network.model.repos.ReposResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(
    private val gitHubApiService: GitHubApiService,
    private val gitHubAuthService: GitHubAuthService,
) {
    private val perPage = 30
    private val sort = "stars"
    private val order = "desc"
    private var page: Int = 0

    suspend fun getAccessToken(code: String): AccessToken {
        return gitHubAuthService.getAccessToken(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, code)
    }

    suspend fun getSearchReposResult(token: String, queryString: String): ReposResult {
        return gitHubApiService.searchRepos(
            token = token,
            page = ++this.page,
            perPage = perPage,
            queryString = queryString,
            sortString = sort,
            orderString = order
        )
    }

    fun initPageToDefValue(){
        page = 0
    }

}