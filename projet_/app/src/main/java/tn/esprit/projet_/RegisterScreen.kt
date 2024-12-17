package tn.esprit.projet_

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.projet_.design_system.AuthOption
import tn.esprit.projet_.design_system.MyTextField
import tn.esprit.projet_.model.SignupDto
import tn.esprit.projet_.viewmodel.UserViewModel

@Composable
fun RegisterScreen(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val firstnameState = remember { mutableStateOf("") }
    val lastnameState = remember { mutableStateOf("") }
    val usernameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val userViewModel: UserViewModel = viewModel()
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column (  ){
            Image(
                painter = painterResource(R.drawable.register),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth(0.25f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Register",
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AuthOption(image = R.drawable.google)
            AuthOption(image = R.drawable.facebook)
            AuthOption(
                image = R.drawable.apple,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Or, login with...",
            fontSize = 15.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .alpha(0.5f)
        )

        MyTextField(
            value = firstnameState.value,
            onValueChange = { firstnameState.value = it },
            hint = "First Name",
            leadingIcon = Icons.Outlined.AccountCircle,
            modifier = Modifier.fillMaxWidth()
        )

        MyTextField(
            value = lastnameState.value,
            onValueChange = { lastnameState.value = it },
            hint = "Last Name",
            leadingIcon = Icons.Outlined.AccountCircle,
            modifier = Modifier.fillMaxWidth()
        )

        MyTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            hint = "Username",
            leadingIcon = Icons.Outlined.AccountCircle,
            modifier = Modifier.fillMaxWidth()
        )

        MyTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            hint = "Email",
            leadingIcon = Icons.Outlined.Email,
            trailingIcon = Icons.Outlined.Check,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        MyTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            hint = "Password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        MyTextField(
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            hint = "Confirm Password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val signupData = SignupDto(
                    firstname = firstnameState.value,
                    lastname = lastnameState.value,
                    username = usernameState.value,
                    email = emailState.value,
                    password = passwordState.value,
                    confirmpasSsingup = confirmPasswordState.value
                )
                userViewModel.signUp(signupData,context) { registeredUser ->
                    if (registeredUser != null) {
                        Toast.makeText(context, "Hello from Jetpack Compose!", Toast.LENGTH_SHORT).show()
                    } else {
                        //Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Register",
                fontSize = 17.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 16.sp,
            )
            Text(
                text = "Login",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onLoginClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(1.dp))
    }
}