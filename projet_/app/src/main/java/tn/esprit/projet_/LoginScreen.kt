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
import tn.esprit.projet_.model.LoginDto
import tn.esprit.projet_.viewmodel.UserViewModel
import tn.esprit.projet_.api.LoginResponse

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: (LoginResponse) -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
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
                val loginDto = LoginDto(email, password)
                userViewModel.login(
                    loginDto = loginDto,
                    context = context,
                    onLoginComplete = { response, error ->
                        isLoading = false
                        error?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                        response?.let {
                            onLoginSuccess(it)
                        }
                    },
                    onUserFetched = { user ->
                        user?.let {
                            Toast.makeText(
                                context,
                                "Welcome ${user.username}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } ?: run {
                            Toast.makeText(
                                context,
                                "Failed to fetch user details",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
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