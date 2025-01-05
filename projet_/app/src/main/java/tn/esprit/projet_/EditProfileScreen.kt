package tn.esprit.projet_

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.projet_.design_system.MyTextField
import tn.esprit.projet_.model.User
@Composable
fun EditProfileScreen(
    user: User,
    onSaveClick: (User) -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Initialize states with data from the 'user' parameter
    val firstnameState = remember { mutableStateOf(user.firstname) }
    val lastnameState = remember { mutableStateOf(user.lastname) }
    val usernameState = remember { mutableStateOf(user.username) }
    val emailState = remember { mutableStateOf(user.email) }

    // When the 'user' changes, update the state values
    LaunchedEffect(user) {
        firstnameState.value = user.firstname
        lastnameState.value = user.lastname
        usernameState.value = user.username
        emailState.value = user.email
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyTextField(
                value = firstnameState.value,
                onValueChange = { firstnameState.value = it },
                hint = "First Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyTextField(
                value = lastnameState.value,
                onValueChange = { lastnameState.value = it },
                hint = "Last Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyTextField(
                value = usernameState.value,
                onValueChange = { usernameState.value = it },
                hint = "Username",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                hint = "Email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedUser = user.copy(
                        firstname = firstnameState.value,
                        lastname = lastnameState.value,
                        username = usernameState.value,
                        email = emailState.value
                    )
                    onSaveClick(updatedUser)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Save",
                    fontSize = 17.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onDeleteClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete",
                    fontSize = 17.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
