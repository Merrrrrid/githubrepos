package com.klymchuk.githubrepos.data.network

import com.google.gson.GsonBuilder
import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.data.network.model.AccessToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface GitHubAuthService {

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): AccessToken

    companion object {
        fun create(): GitHubAuthService {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .client(createOkHttpClient())
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .baseUrl(BuildConfig.TOKEN_BASE_URL)
                .build()

            return retrofit.create(GitHubAuthService::class.java)
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