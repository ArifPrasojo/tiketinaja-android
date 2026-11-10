package com.tiketinaja.app.data.api

import android.content.Context
import android.content.SharedPreferences
import com.tiketinaja.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tiketinaja_auth", Context.MODE_PRIVATE)

    fun getToken(): String? = prefs.getString("bearer_token", null)

    fun saveToken(token: String) {
        prefs.edit().putString("bearer_token", token).apply()
    }

    fun clear() {
        prefs.edit().remove("bearer_token").apply()
    }
}

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Accept", "application/json")
        val token = tokenManager.getToken()
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        val response = chain.proceed(builder.build())
        if (response.code == 401) {
            tokenManager.clear()
        }
        return response
    }
}

object ApiClient {
    @Volatile
    private var instance: ApiService? = null

    fun getService(context: Context): ApiService {
        return instance ?: synchronized(this) {
            instance ?: buildService(context.applicationContext).also { instance = it }
        }
    }

    private fun buildService(context: Context): ApiService {
        val tokenManager = TokenManager(context)
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
