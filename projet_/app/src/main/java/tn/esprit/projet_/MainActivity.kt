package tn.esprit.projet_

import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import tn.esprit.projet_.model.Article
import tn.esprit.projet_.model.User
import tn.esprit.projet_.ui.theme.Projet_Theme
import tn.esprit.projet_.viewmodel.UserViewModel
import tn.esprit.projet_.screens.*
import tn.esprit.projet_.ui.screens.ChatDetailScreen
import tn.esprit.projet_.ui.screens.ChatListScreen
import tn.esprit.projet_.ui.screens.sampleChatMessages
import tn.esprit.projet_.ui.screens.sampleDoctors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.initialize(this)

        setContent {
            Projet_Theme {
                var currentScreen by remember { mutableStateOf("home") }
                var currentUser by remember { mutableStateOf(User("", "", "", "", "", "", 1)) }
                var selectedArticle by remember { mutableStateOf<Article?>(null) }

                // Sample articles data
                val sampleArticles = listOf(
                    Article(1, "Article 1", "Short description of Article 1", "Full content of Article 1.", R.drawable.health_meddd, null),
                    Article(2, "Article 2", "Short description of Article 2", "Full content of Article 2.", R.drawable.health_medd, null),
                    Article(3, "Article 3", "Short description of Article 3", "Full content of Article 3.", R.drawable.health_med, null),
                    Article(4, "Article 4", "Short description of Article 4", "Full content of Article 1.", R.drawable.health_meddd, null),
                    Article(5, "Article 5", "Short description of Article 5", "Full content of Article 2.", R.drawable.health_medd, null),
                    Article(6, "Article 6", "Short description of Article 6", "Full content of Article 3.", R.drawable.health_med, null),
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onRegisterClick = { currentScreen = "register" },
                            onForgotPasswordClick = { currentScreen = "forgot_password" },
                            onLoginSuccess = { loginResponse ->
                                currentScreen = "home"
                                userViewModel.fetchUserDetails(loginResponse.userId) { user ->
                                    if (user != null) {
                                        currentUser = user
                                    }
                                }
                            },
                            userViewModel = userViewModel
                        )

                        "home" -> {
                            if (selectedArticle == null) {
                                HomeScreen(
                                    articles = sampleArticles,
                                    onProfileClick = { currentScreen = "profile" },
                                    onLogoutClick = { currentScreen = "login" },
                                    onCameraClick = { currentScreen = "camera" },
                                    onNotificationClick = { currentScreen = "notification" },
                                    onChatClick = { currentScreen = "chat" },
                                    onArticleClick = { article -> selectedArticle = article },
                                    onHomeClick = { currentScreen = "home" },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                ArticleDetailScreen(
                                    article = selectedArticle!!,
                                    onBackClick = { selectedArticle = null },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }

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

                        "edit_profile" -> EditProfileScreen(
                            user = currentUser,
                            onSaveClick = { updatedUser -> currentUser = updatedUser; currentScreen = "profile" },
                            onDeleteClick = { /* Handle delete action */ },
                            onBackClick = { currentScreen = "profile" },
                            modifier = Modifier.padding(innerPadding)
                        )

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

                        "conversation_1" -> ChatDetailScreen(
                            doctor = sampleDoctors[0], // Update with actual doctor data
                            chatMessages = sampleChatMessages,
                            onBackClick = { currentScreen = "chat" },
                            onSendMessage = { message -> sampleChatMessages.add(message) },
                            modifier = Modifier.padding(innerPadding)
                        )

                        else -> Text(text = "Page not found!", modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
