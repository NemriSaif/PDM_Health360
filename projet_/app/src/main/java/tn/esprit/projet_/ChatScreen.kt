package tn.esprit.projet_.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import tn.esprit.projet_.R

// Data class for Doctor
data class Doctor(
    val id: Int,
    val name: String,
    val avatarResId: Int,
    val lastMessage: String,
    val lastMessageTime: LocalDateTime
)

// Sample data for doctors
val sampleDoctors = listOf(
    Doctor(1, "Dr. Ali", R.drawable.health_med, "Your test results look good!", LocalDateTime.now().minusHours(1)),
    Doctor(2, "Dr. Mohamed", R.drawable.health_med, "Please remember to take your medication", LocalDateTime.now().minusHours(3)),
    Doctor(3, "Dr. Lina", R.drawable.health_med, "See you at your next appointment", LocalDateTime.now().minusDays(1)),
    Doctor(4, "Dr. Sarah", R.drawable.health_med, "How are you feeling today?", LocalDateTime.now().minusDays(2)),
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
    onArticlesClick: () -> Unit,
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
