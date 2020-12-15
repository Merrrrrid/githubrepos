package com.klymchuk.githubrepos.data.network

import com.google.gson.GsonBuilder
import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.data.network.model.repos.ReposResult
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface GitHubApiService {

    @GET("/search/repositories")
    fun searchRepos(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int, //15
        @Query("q") queryString: String,
        @Query("sort") sortString: String, //
        @Query("order") orderString: String, //desc
    ): Observable<ReposResult>

    companion object {
        fun create(): GitHubApiService {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .client(createOkHttpClient())
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()

            return retrofit.create(GitHubApiService::class.java)
        }

        private fun createOkHttpClient(
        ): OkHttpClient {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        }
    }
}