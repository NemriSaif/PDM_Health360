package tn.esprit.projet_.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.Image
import tn.esprit.projet_.model.LoginDto
import tn.esprit.projet_.viewmodel.UserViewModel
import tn.esprit.projet_.api.LoginResponse
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import tn.esprit.projet_.R

@Composable

fun LoginScreen(
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: (LoginResponse) -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") } // Username input
    var password by remember { mutableStateOf("") } // Password input
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Image
        val logo: Painter = painterResource(id = R.drawable.logo) // Replace with your logo file
        Image(
            painter = logo,
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp) // Adjust padding as needed
                .height(120.dp) // Adjust logo height
        )

        // Username TextField
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Login Button
        Button(
            onClick = {
                isLoading = true
                val loginDto = LoginDto(username, password)
                userViewModel.login(loginDto) { success, error ->
                    isLoading = false
                    if (success) {
                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                        // Fetch user details after successful login
                        userViewModel.fetchUserDetail(username) { user ->
                            if (user != null) {
                                Toast.makeText(
                                    context,
                                    "Welcome ${user.username}!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onLoginSuccess(LoginResponse("accessToken", "refreshToken", user._id,user.username))
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to fetch user details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, error ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        // Register and Forgot Password Links
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onRegisterClick) {
                Text("Register")
            }
            TextButton(onClick = onForgotPasswordClick) {
                Text("Forgot Password?")
            }
        }
    }
}