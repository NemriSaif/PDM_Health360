package tn.esprit.projet_

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tn.esprit.projet_.api.ApiService
import tn.esprit.projet_.api.RetrofitInstance
import tn.esprit.projet_.model.Article
import tn.esprit.projet_.model.User
import tn.esprit.projet_.ui.screens.*
import tn.esprit.projet_.ui.theme.Projet_Theme
import tn.esprit.projet_.viewmodel.UserViewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tn.esprit.projet_.screens.LoginScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        setContent {
            Projet_Theme {
                var currentScreen by remember { mutableStateOf("login") }
                var currentUser by remember { mutableStateOf(User("", "", "", "", "", "", 1)) }

                // Sample articles data
                val sampleArticles = listOf(
                    Article(1, "Article 1", "Short description of Article 1", "Full content of Article 1.", R.drawable.health_meddd, null),
                    Article(2, "Article 2", "Short description of Article 2", "Full content of Article 2.", null, "https://via.placeholder.com/150")
                )


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onRegisterClick = { currentScreen = "register" },
                            onForgotPasswordClick = { currentScreen = "forgot_password" },
                            onLoginSuccess = { response ->
                                println("Login successful! UserId: ${response.userId}")
                                currentScreen = "home" // Set screen to home immediately after login

                                // Fetch user details
                                userViewModel.fetchUserDetails(response.userId) { user ->
                                    user?.let {
                                        println("User details fetched: $user")
                                        currentUser = it
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Welcome ${it.username}!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } ?: run {
                                        println("Failed to fetch user details")
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Failed to fetch user details.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier.padding(innerPadding),
                            userViewModel = userViewModel
                        )
                        "home" -> HomeScreen(
                            onProfileClick = { currentScreen = "profile" },
                            onLogoutClick = { currentScreen = "login" },
                            onCameraClick = { currentScreen = "camera" },
                            onHomeClick = { currentScreen = "home" },
                            onNotificationClick = { currentScreen = "notification" },
                            onChatClick = { currentScreen = "chat" },
                            onArticlesClick = { currentScreen = "articles" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "camera" -> CameraScreen(
                            onBackClick = { currentScreen = "home" },
                            onScanClick = { /* Handle scan action */ },
                            onUploadClick = { /* Handle upload action */ },
                            onCaptureClick = { /* Handle capture action */ },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "profile" -> ViewProfileScreen(
                            user = currentUser,
                            onEditClick = { currentScreen = "edit_profile" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "register" -> RegisterScreen(
                            onLoginClick = { currentScreen = "login" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "forgot_password" -> ForgotPasswordScreen(
                            onSendCodeClick = { email -> /* Handle send code action */ },
                            onBackClick = { currentScreen = "login" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "view_profile" -> ViewProfileScreen(
                            user = currentUser,
                            onEditClick = { currentScreen = "edit_profile" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "edit_profile" -> EditProfileScreen(
                            user = currentUser,
                            onSaveClick = { updatedUser ->
                                currentUser = updatedUser
                                currentScreen = "view_profile"
                            },
                            onDeleteClick = { /* Handle delete action */ },
                            onBackClick = { currentScreen = "view_profile" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "articles" -> ArticleScreen(
                            articles = sampleArticles,
                            onArticleClick = { article ->
                                currentScreen = "article_${article.id}"
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "chat" -> ChatListScreen(
                            onProfileClick = { currentScreen = "profile" },
                            onLogoutClick = { currentScreen = "login" },
                            onHomeClick = { currentScreen = "home" },
                            onCameraClick = { currentScreen = "camera" },
                            onNotificationClick = { currentScreen = "notification" },
                            onChatClick = { currentScreen = "chat" },
                            onArticlesClick = { currentScreen = "articles" },
                            onChatSelected = { doctorId -> currentScreen = "conversation_$doctorId" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        else -> {
                            val articleId = currentScreen.removePrefix("article_").toIntOrNull()
                            if (articleId != null) {
                                val selectedArticle = sampleArticles.find { it.id == articleId }
                                if (selectedArticle != null) {
                                    ArticleDetailScreen(
                                        article = selectedArticle,
                                        onBackClick = { currentScreen = "articles" },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
