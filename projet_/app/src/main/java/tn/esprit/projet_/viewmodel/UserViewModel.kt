package tn.esprit.projet_.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.projet_.api.ApiService
import tn.esprit.projet_.api.LoginResponse
import tn.esprit.projet_.api.RetrofitInstance
import tn.esprit.projet_.model.LoginDto
import tn.esprit.projet_.model.User
import tn.esprit.projet_.model.SignupDto

class UserViewModel : ViewModel() {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
    }

    fun login(
        loginDto: LoginDto,
        context: Context,
        onLoginComplete: (LoginResponse?, String?) -> Unit,
        onUserFetched: (User?) -> Unit
    ) {
        if (!::context.isInitialized) {
            initialize(context)
        }

        viewModelScope.launch {
            try {
                val apiService = RetrofitInstance.getRetrofit(context).create(ApiService::class.java)
                val response = apiService.login(loginDto)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveTokens(it.accessToken, it.refreshToken)
                        onLoginComplete(it, null)

                        fetchUserDetails(it.userId) { user ->
                            onUserFetched(user)
                        }
                    } ?: run {
                        onLoginComplete(null, "Login Failed: No response body")
                    }
                } else {
                    onLoginComplete(null, "Login Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                onLoginComplete(null, "Connection Error: ${e.message}")
            }
        }
    }

    fun fetchUserDetails(userId: String, onResult: (User?) -> Unit) {
        println("fetchUserDetails called with userId: $userId")

        val apiService = RetrofitInstance.getRetrofit(context).create(ApiService::class.java)
        val call = apiService.getUserById(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    println("User details fetched successfully: $user")
                    onResult(user)
                } else {
                    println("Error fetching user details: ${response.code()} - ${response.message()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println("Network error: ${t.localizedMessage}")
                onResult(null)
            }
        })
    }
    fun signUp(signupData: SignupDto, context: Context, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            RetrofitInstance.getRetrofit(context)
                .create(ApiService::class.java)
                .signUp(signupData)
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val registeredUser = response.body()
                            registeredUser?.let {
                                // Notify success
                                Toast.makeText(context, "User registered successfully!", Toast.LENGTH_SHORT).show()
                                onResult(it)
                            } ?: run {
                                // Handle unexpected null response
                                Toast.makeText(context, "Unexpected error: No user returned", Toast.LENGTH_SHORT).show()
                                onResult(null)
                            }
                        } else {
                            // Handle API errors
                            Toast.makeText(context, "Registration failed: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                            onResult(null)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        // Handle network failures
                        Toast.makeText(context, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                        onResult(null)
                    }
                })
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
        }.apply()
    }
}