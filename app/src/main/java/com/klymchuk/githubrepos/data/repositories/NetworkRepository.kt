package com.klymchuk.githubrepos.data.repositories

import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.data.network.GitHubApiService
import com.klymchuk.githubrepos.data.network.model.AccessToken
import com.klymchuk.githubrepos.utils.logTag
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(
    private val gitHubApiService: GitHubApiService
) {

    private val logTag = logTag()

    fun getAccessToken(code: String) : Observable<AccessToken> {
        return gitHubApiService.getAccessToken(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, code)
    }

}