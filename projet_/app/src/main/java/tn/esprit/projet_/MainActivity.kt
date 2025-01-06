package tn.esprit.projet_

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import tn.esprit.projet_.model.Article
import tn.esprit.projet_.model.Recommendation
import tn.esprit.projet_.model.User
import tn.esprit.projet_.ui.theme.Projet_Theme
import tn.esprit.projet_.viewmodel.UserViewModel
import tn.esprit.projet_.screens.*
import tn.esprit.projet_.ui.screens.ChatDetailScreen
import tn.esprit.projet_.ui.screens.ChatListScreen
import android.Manifest

import tn.esprit.projet_.ui.screens.sampleDoctors
class MainActivity : ComponentActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.initialize(this)
        val sampleChatMessages = mutableListOf(
            "Hello, how are you?",
            "I'm good, thank you! How about you?",
            "I'm doing great, thanks for asking!"
        )

        setContent {
            Projet_Theme {
                var currentScreen by remember { mutableStateOf("login") }
                var currentUser by remember { mutableStateOf<User?>(null) }  // Track the current user
                var selectedArticle by remember { mutableStateOf<Article?>(null) }



                var recommendations by remember { mutableStateOf<List<Recommendation>?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf<String?>(null) }


                if (currentScreen == "home" && recommendations == null) {
                    userViewModel.getRecommendations { fetchedRecommendations ->
                        isLoading = false
                        if (fetchedRecommendations != null) {
                            recommendations = fetchedRecommendations
                        } else {
                            errorMessage = "Failed to load recommendations."
                        }
                    }
                }
                val onHomeClick: () -> Unit = { currentScreen = "home" }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onRegisterClick = { currentScreen = "register" },
                            onForgotPasswordClick = { currentScreen = "forgot_password" },
                            onLoginSuccess = { loginResponse ->
                                Log.d("MainActivity", "Login Response: $loginResponse")
                                val username = loginResponse.username ?: "Unknown"
                                Log.d("MainActivity", "Fetched username: $username")
                                userViewModel.fetchUserDetail(username) { user ->
                                    if (user != null) {
                                        currentUser = user  // Update currentUser with the fetched data
                                        Log.d("MainActivity", "User details: $user")

                                    } else {
                                        Toast.makeText(this@MainActivity, "User not found.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                currentScreen = "home"  // Navigate to home screen on successful login

                            },
                            userViewModel = userViewModel
                        )

                        "home" -> {
                            if (selectedArticle == null) {
                                HomeScreen(
                                    recommendations = recommendations ?: emptyList(), // Pass fetched recommendations
                                    onProfileClick = { currentScreen = "profile" },
                                    onLogoutClick = { currentUser = null; currentScreen = "login" },
                                    onCameraClick = { currentScreen = "camera" },
                                    onNotificationClick = { currentScreen = "notification" },
                                    onChatClick = { currentScreen = "chat" },
                                    onRecommendationClick = { recommendation ->
                                        // Handle recommendation click, open detail screen if needed
                                    },
                                    onHomeClick = onHomeClick,
                                    modifier = Modifier.padding(innerPadding)
                                            , // Pass the onHomeClick function

                                )
                            } else {
                                ArticleDetailScreen(
                                    article = selectedArticle!!,
                                    onBackClick = { selectedArticle = null },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }

                        "camera" -> {
                            val cameraPermission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
                                CameraScreen(
                                    onBackClick = { currentScreen = "home" },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(cameraPermission),
                                    CAMERA_PERMISSION_CODE
                                )
                            }
                        }


                        "profile" -> {
                            // Only pass currentUser if it's not null
                            if (currentUser != null) {
                                ViewProfileScreen(
                                    user = currentUser!!,  // Unwrap safely since it's not null
                                    onEditClick = { currentScreen = "edit_profile" },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                // If currentUser is null, navigate to login screen
                                Toast.makeText(this@MainActivity, "Please log in to view profile", Toast.LENGTH_SHORT).show()
                                currentScreen = "login"
                            }
                        }

                        "register" -> RegisterScreen(
                            onLoginClick = { currentScreen = "login" },
                            modifier = Modifier.padding(innerPadding)
                        )

                        "forgot_password" -> ForgotPasswordScreen(
                            onSendCodeClick = { email ->
                                userViewModel.sendForgotPasswordEmail(email) { success ->
                                    if (success) {
                                        Toast.makeText(this@MainActivity, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                                        currentScreen = "login"  // Navigate to login screen
                                    } else {
                                        Toast.makeText(this@MainActivity, "Failed to send reset email. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onBackClick = { currentScreen = "login" },
                            modifier = Modifier.padding(innerPadding)
                        )

                        "edit_profile" -> {
                            // Only pass currentUser if it's not null
                            if (currentUser != null) {
                                EditProfileScreen(
                                    user = currentUser!!,  // Unwrap safely since it's not null
                                    onSaveClick = { updatedUser ->
                                        currentUser = updatedUser // Update the currentUser
                                        currentScreen = "profile" // Navigate back to profile
                                    },
                                    onDeleteClick = { /* Handle delete action */ },
                                    onBackClick = { currentScreen = "profile" },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                // If currentUser is null, navigate to login screen
                                Toast.makeText(this@MainActivity, "Please log in to edit profile", Toast.LENGTH_SHORT).show()
                                currentScreen = "login"
                            }
                        }

                        "chat" -> ChatListScreen(
                            onProfileClick = { currentScreen = "profile" },
                            onLogoutClick = { currentScreen = "login" },
                            onHomeClick = { currentScreen = "home" },
                            onCameraClick = { currentScreen = "camera" },
                            onNotificationClick = { currentScreen = "notification" },
                            onChatClick = { currentScreen = "chat" },
                            onChatSelected = { doctorId -> currentScreen = "conversation_$doctorId" },
                            modifier = Modifier.padding(innerPadding)
                        )



                        else -> Text(text = "Page not found!", modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
     override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions , grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
