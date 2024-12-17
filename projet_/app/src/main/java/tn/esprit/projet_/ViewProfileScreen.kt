package tn.esprit.projet_

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.projet_.model.User

@Composable
fun ViewProfileScreen(user: User, onEditClick: () -> Unit, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "${user.firstname} ${user.lastname}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "@${user.username}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Email: ${user.email}",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                // Add more user data here if needed
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onEditClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Edit Profile",
                fontSize = 17.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Complete Profile",
                fontSize = 17.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Dialog for completing profile
        if (showDialog) {
            CompleteProfileDialog(
                onDismiss = { showDialog = false },
                onSave = { enteredAge, enteredAllergies ->
                    age = enteredAge
                    allergies = enteredAllergies
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CompleteProfileDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var age by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Complete Your Profile")
        },
        text = {
            Column {
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                TextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies (e.g., peanuts, dust)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(age, allergies)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
