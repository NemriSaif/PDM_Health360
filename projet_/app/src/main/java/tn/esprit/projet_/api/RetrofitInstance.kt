package tn.esprit.projet_.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://192.168.1.21:3000/"

object RetrofitInstance {
    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
                val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

                val authorizedRequest = accessToken?.let { token ->
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } ?: originalRequest

                chain.proceed(authorizedRequest)
            }
            .build()
    }

    fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}