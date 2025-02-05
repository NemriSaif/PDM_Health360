package tn.esprit.projet_.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.projet_.api.ApiService
import tn.esprit.projet_.api.ForgotPasswordRequest
import tn.esprit.projet_.api.LoginResponse
import tn.esprit.projet_.api.RetrofitInstance
import tn.esprit.projet_.model.CreateRecommendationDto
import tn.esprit.projet_.model.LoginDto
import tn.esprit.projet_.model.Recommendation
import tn.esprit.projet_.model.User
import tn.esprit.projet_.model.SignupDto

class UserViewModel : ViewModel() {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private val _recommendations = MutableLiveData<List<Recommendation>>()
    val recommendations: LiveData<List<Recommendation>> = _recommendations
    fun initialize(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
    }

    fun login(loginDto: LoginDto, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = RetrofitInstance.getRetrofit(context).create(ApiService::class.java)
                val response = apiService.login(loginDto)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveTokens(it.accessToken, it.refreshToken)
                        onResult(true, null) // Success
                    } ?: onResult(false, "Login response is empty")
                } else {
                    onResult(false, "Login Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    fun fetchUserDetail(userId: String, onResult: (User?) -> Unit) {
        println("fetchUserDetails called with username: $userId")

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



    fun getRecommendations(onResult: (List<Recommendation>?) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = RetrofitInstance.getRetrofit(context).create(ApiService::class.java)
                val response = apiService.getRecommendations()

                if (response.isSuccessful) {
                    onResult(response.body()) // Pass the recommendations to the result callback
                } else {
                    onResult(null) // Failure case
                }
            } catch (e: Exception) {
                onResult(null) // Error case
            }
        }
    }

    // Create a new recommendation
    fun createRecommendation(createRecommendationDto: CreateRecommendationDto, onResult: (Recommendation?) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = RetrofitInstance.getRetrofit(context).create(ApiService::class.java)
                val response = apiService.createRecommendation(createRecommendationDto)

                if (response.isSuccessful) {
                    onResult(response.body()) // Pass the created recommendation to the result callback
                } else {
                    onResult(null) // Failure case
                }
            } catch (e: Exception) {
                onResult(null) // Error case
            }
        }
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
    fun sendForgotPasswordEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = RetrofitInstance.getRetrofit(context).create(ApiService::class.java)
                val response = apiService.forgotPassword(ForgotPasswordRequest(email))
                if (response.isSuccessful) {
                    onResult(true) // Success
                } else {
                    onResult(false) // Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false) // Error
            }
        }
    }


    private fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
        }.apply()
    }
}