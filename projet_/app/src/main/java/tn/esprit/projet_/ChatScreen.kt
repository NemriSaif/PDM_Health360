package tn.esprit.projet_.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import tn.esprit.projet_.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Data class for Doctor
data class Doctor(
    val id: Int,
    val name: String,
    val avatarResId: Int,
    val lastMessage: String,
    val lastMessageTime: LocalDateTime
)

val sampleDoctors = listOf(
    Doctor(1, "Dr. Ali", R.drawable.doctor_ali, "Your test results look good!", LocalDateTime.now().minusHours(1)),
    Doctor(2, "Dr. Mohamed", R.drawable.doctor_mohamed, "Please remember to take your medication", LocalDateTime.now().minusHours(3)),
    Doctor(3, "Dr. Lina", R.drawable.doctor_lina, "See you at your next appointment", LocalDateTime.now().minusDays(1)),
    Doctor(4, "Dr. Sarah", R.drawable.doctor_sarah, "How are you feeling today?", LocalDateTime.now().minusDays(2)),
)

val sampleChatMessages = mutableListOf(
    "Hello, how are you?",
    "I'm good, thank you! How about you?",
    "I'm doing great, thanks for asking!"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCameraClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onChatClick: () -> Unit,
    onChatSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat List") })
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = onHomeClick) { Icon(Icons.Default.Home, contentDescription = "Home") }
                IconButton(onClick = onChatClick) { Icon(Icons.Default.Chat, contentDescription = "Chat") }
                IconButton(onClick = onCameraClick) { Icon(Icons.Default.Camera, contentDescription = "Camera") }
                IconButton(onClick = onNotificationClick) { Icon(Icons.Default.Notifications, contentDescription = "Notifications") }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(sampleDoctors) { doctor ->
                ChatListItem(doctor = doctor, onClick = { onChatSelected(doctor.id) })
            }
        }
    }
}

@Composable
fun ChatListItem(
    doctor: Doctor,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = doctor.avatarResId),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = doctor.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = doctor.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = doctor.lastMessageTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    doctor: Doctor,
    chatMessages: List<String>,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat with ${doctor.name}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Type a message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (message.text.isNotBlank()) {
                                onSendMessage(message.text)
                                message = TextFieldValue("") // Clear input field after sending
                            }
                        }
                    )
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Display the chat history in a LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .weight(1f),
                reverseLayout = true // to display the most recent messages at the bottom
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(message = message, isDoctor = false) // Assuming this is the user's message
                    // To display the doctor's message, you can pass 'isDoctor' as true.
                    ChatMessageItem(message = message, isDoctor = true) // Sample doctor's message
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: String, isDoctor: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isDoctor) Arrangement.Start else Arrangement.End
    ) {
        // Display Doctor's avatar or User's Avatar (circle)
        if (isDoctor) {
            Image(
                painter = painterResource(id = R.drawable.doctor_ali), // Use the doctor's avatar
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Message Bubble
        Box(
            modifier = Modifier
                .padding(8.dp)
                .background(
                    color = if (isDoctor) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDoctor) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (!isDoctor) {
            // User's avatar on the right side
            Image(
                painter = painterResource(id = R.drawable.doctor_ali), // Use user's avatar
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

