package com.example.simpleimageeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpleimageeditor.ui.theme.SimpleImageEditorTheme

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.io.OutputStream
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleImageEditorTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ImageEditorScreen(snackbarHostState = snackbarHostState, coroutineScope = scope)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageEditorScreen(snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var editedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        editedBitmap = null // Reset edited bitmap when a new image is picked
        if (uri != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Image selected!")
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("No image selected.")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.SpaceBetween // Distribute space vertically
    ) {
        // Top section: Pick Image button
        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            modifier = Modifier.padding(bottom = 16.dp) // Add padding below the button
        ) {
            Text("Pick Image")
        }

        // Middle section: Image display
        val imageToDisplay = editedBitmap ?: selectedImageUri

        if (imageToDisplay != null) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            AsyncImage(
                model = imageToDisplay,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .weight(1f) // Take available vertical space
                    .fillMaxWidth() // Fill width
                    .padding(bottom = 16.dp) // Add padding below the image
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            scale *= zoom
                            offset += pan
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        } else {
            Text(
                text = "No image selected",
                modifier = Modifier
                    .weight(1f) // Take available vertical space
                    .fillMaxWidth() // Fill width
                    .padding(bottom = 16.dp) // Add padding below the text
            )
        }

        // Bottom section: Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Distribute buttons horizontally
        ) {
            Button(onClick = {
                coroutineScope.launch { snackbarHostState.showSnackbar("Applying filter...") }
                selectedImageUri?.let { uri ->
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        editedBitmap = toGrayscale(bitmap)
                        coroutineScope.launch { snackbarHostState.showSnackbar("Filter applied!") }
                    } catch (e: Exception) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Failed to apply filter: ${e.localizedMessage}") }
                    }
                } ?: run {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Please select an image first.") }
                }
            }) {
                Text("Apply Grayscale Filter")
            }
            Button(onClick = {
                coroutineScope.launch { snackbarHostState.showSnackbar("Saving image...") }
                editedBitmap?.let { bitmap ->
                    try {
                        saveImageToGallery(context, bitmap, "edited_image_${System.currentTimeMillis()}")
                        coroutineScope.launch { snackbarHostState.showSnackbar("Image saved successfully!") }
                    } catch (e: Exception) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Failed to save image: ${e.localizedMessage}") }
                    }
                } ?: run {
                    coroutineScope.launch { snackbarHostState.showSnackbar("No image to save.") }
                }
            }) {
                Text("Save Image")
            }
        }
    }
}

fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
    val height = bmpOriginal.height
    val width = bmpOriginal.width
    val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmpGrayscale)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvas.drawBitmap(bmpOriginal, 0f, 0f, paint)
    return bmpGrayscale
}

fun saveImageToGallery(context: android.content.Context, bitmap: Bitmap, title: String) {
    val contentValues = android.content.ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$title.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let { 
        var outputStream: OutputStream? = null
        try {
            outputStream = context.contentResolver.openOutputStream(it)
            outputStream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewImageEditorScreen() {
    SimpleImageEditorTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        ImageEditorScreen(snackbarHostState = snackbarHostState, coroutineScope = scope)
    }
}




