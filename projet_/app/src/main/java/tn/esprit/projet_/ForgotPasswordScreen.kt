package tn.esprit.projet_

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.projet_.design_system.MyTextField

@Composable
fun ForgotPasswordScreen(onSendCodeClick: (String) -> Unit, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val emailState = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackClick() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Image from drawable
        val image: Painter = painterResource(id = R.drawable.forgot_password_image) // Replace with your image name
        Image(
            painter = image,
            contentDescription = "Forgot Password Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Adjust the size as needed
                .padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your email",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MyTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                hint = "Email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSendCodeClick(emailState.value) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Send Code",
                    fontSize = 17.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
