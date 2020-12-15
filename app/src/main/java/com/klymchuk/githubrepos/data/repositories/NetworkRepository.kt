package com.klymchuk.githubrepos.data.repositories

import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.data.network.GitHubApiService
import com.klymchuk.githubrepos.data.network.GitHubAuthService
import com.klymchuk.githubrepos.data.network.model.AccessToken
import com.klymchuk.githubrepos.data.network.model.repos.ReposResult
import com.klymchuk.githubrepos.utils.logTag
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(
    private val gitHubApiService: GitHubApiService,
    private val gitHubAuthService: GitHubAuthService,
) {

    init {

    }

    private val logTag = logTag()

    private val perPage = 15
    private val sort = "stars"
    private val order = "desc"

    fun getAccessToken(code: String): Observable<AccessToken> {
        return gitHubAuthService.getAccessToken(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, code)
    }

    fun getSearchReposResult(page: Int, queryString: String): Observable<ReposResult> {
        return gitHubApiService.searchRepos(
            token = "bearer 03d00b3204afabada7ad5a821c93972c9b8f4d3c",
            page = page,
            perPage = perPage,
            queryString = queryString,
            sortString = sort,
            orderString = order
        )
    }

}