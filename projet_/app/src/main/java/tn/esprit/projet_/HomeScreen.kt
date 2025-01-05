package tn.esprit.projet_

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import tn.esprit.projet_.model.Article
import tn.esprit.projet_.model.CreateRecommendationDto
import tn.esprit.projet_.model.Recommendation
import tn.esprit.projet_.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    recommendations: List<Recommendation>,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCameraClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onChatClick: () -> Unit,
    onRecommendationClick: (Recommendation) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showAddRecommendationDialog by remember { mutableStateOf(false) } // State for showing dialog
    var recommendationName by remember { mutableStateOf("") }
    var recommendationDescription by remember { mutableStateOf("") }
    var selectedImageResId by remember { mutableStateOf(R.drawable.doctor_ali) } // Default image resource
    val viewModel: UserViewModel = viewModel()
    val localRecommendations = remember { mutableStateListOf<Recommendation>() }

    fun handleCreateRecommendation(name: String, description: String, imageResId: Int) {
        val createRecommendationDto = CreateRecommendationDto(name, description, "/Uploads/img1.png")

        // Call the viewModel function to create a recommendation
        viewModel.createRecommendation(createRecommendationDto) { newRecommendation ->
            if (newRecommendation != null) {
                // Update the local recommendations list with the new recommendation
                localRecommendations.add(newRecommendation) // Update the list

                println("New recommendation created: $newRecommendation")
                // You can update your recommendations list here
            } else {
                // Handle failure (e.g., show an error message)
                println("Failed to create recommendation")
            }
        }
    }


    // Open dialog
    if (showAddRecommendationDialog) {
        AddRecommendationDialog(
            onDismiss = { showAddRecommendationDialog = false },
            onSave = { name, description, imageResId ->
                handleCreateRecommendation(name, description, imageResId)
                showAddRecommendationDialog = false
            },
            currentName = "",
            currentDescription = "",
            currentImageResId = R.drawable.doctor_ali
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Profile", color = Color.Black) },
                            onClick = {
                                showMenu = false
                                onProfileClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onLogoutClick()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = onHomeClick) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onChatClick) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onCameraClick) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddRecommendationDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Recommendation")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(localRecommendations) { recommendation ->
                        RecommendationItem(recommendation = recommendation, onClick = { onRecommendationClick(recommendation) })
                    }
                }
            }
        }
    )
}

@Composable
fun AddRecommendationDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit,
    currentName: String,
    currentDescription: String,
    currentImageResId: Int
) {
    var name by remember { mutableStateOf(currentName) }
    var description by remember { mutableStateOf(currentDescription) }
    var selectedImageResId by remember { mutableStateOf(currentImageResId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Recommendation") },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                // Name field
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recommendation Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Recommendation Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Image selection (can be a placeholder or a list of images)
                Image(
                    painter = painterResource(id = selectedImageResId),
                    contentDescription = "Recommendation Image",
                    modifier = Modifier.size(100.dp).padding(bottom = 8.dp)
                )

                // Allow user to select a different image (static for simplicity)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { selectedImageResId = R.drawable.doctor_ali }) {
                        Text("Image 1")
                    }
                    TextButton(onClick = { selectedImageResId = R.drawable.doctor_ali }) {
                        Text("Image 2")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, description, 2) }) {
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


@Composable
fun RecommendationItem(recommendation: Recommendation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Display a static image (use Coil to load from URL or a placeholder)
            Image(
                painter = painterResource(id = R.drawable.doctor_ali),
                contentDescription = "Recommendation Image",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Use the updated Material 3 typography styles
            Text(
                text = recommendation.name,
                style = MaterialTheme.typography.bodyLarge // Adjusted for Material 3 typography
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.recommendation,
                style = MaterialTheme.typography.bodyMedium // Adjusted for Material 3 typography
            )
        }
    }
}
