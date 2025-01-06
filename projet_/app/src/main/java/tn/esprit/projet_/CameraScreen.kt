package tn.esprit.projet_

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val coroutineScope = rememberCoroutineScope()

    // State for recognized text
    var recognizedText by remember { mutableStateOf("Point the Scanner at Your Document") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Point the Scanner at Your Document",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF6A1B9A) // Purple background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .background(Color.DarkGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = recognizedText,
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }

            // Action buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A1B9A)) // Purple footer background
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                recognizedText = "Scanning..."
                                captureAndProcessText(
                                    context = context,
                                    previewView = previewView,
                                    onTextRecognized = { text ->
                                        recognizedText = text
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)), // Light Purple
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Scan", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // Initialize Camera
    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner, cameraSelector, preview)
    }
}

// Capture and process text using ML Kit
suspend fun captureAndProcessText(
    context: Context,
    previewView: PreviewView,
    onTextRecognized: (String) -> Unit
) {
    val bitmap = withContext(Dispatchers.Main) {
        previewView.bitmap // Access bitmap on the main thread
    } ?: return onTextRecognized("Failed to capture image.")

    // Convert bitmap to ML Kit InputImage
    val image = InputImage.fromBitmap(bitmap, 0)

    // Initialize ML Kit Text Recognizer
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    try {
        // Process the image
        val result = recognizer.process(image).await()
        val extractedText = extractTextFromResult(result)
        onTextRecognized(extractedText)
    } catch (e: Exception) {
        e.printStackTrace()
        onTextRecognized("Error recognizing text.")
    }
}


// Helper to extract text from ML Kit result
fun extractTextFromResult(result: Text): String {
    return result.textBlocks.joinToString("\n") { block -> block.text }
}
