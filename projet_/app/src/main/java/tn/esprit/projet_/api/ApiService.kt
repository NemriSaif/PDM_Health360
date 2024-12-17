package tn.esprit.projet_.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import tn.esprit.projet_.model.LoginDto
import tn.esprit.projet_.model.SignupDto
import tn.esprit.projet_.model.User

// Add this data class to match the backend response
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)

interface ApiService {
    @POST("auth/signup")
     fun signUp(@Body signupData: SignupDto): Call<User>

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginDto): Response<LoginResponse>

    @GET("users/{userId}")
    fun getUserById(@Path("userId") userId: String): Call<User>

    @GET("auth/profile")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<User>

    @GET("user/{id}")
    suspend fun getUserDetails(@Path("id") userId: String): Response<User>
    @GET("users/{id}") // Replace with the actual endpoint
    suspend fun fetchUserDetails(@Path("id") userId: String): Response<User>
}