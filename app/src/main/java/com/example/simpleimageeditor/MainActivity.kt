package com.example.simpleimageeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleImageEditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageEditorScreen()
                }
            }
        }
    }
}

@Composable
fun ImageEditorScreen() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var editedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        selectedImageUri = it
        editedBitmap = null // Reset edited bitmap when a new image is picked
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
            AsyncImage(
                model = imageToDisplay,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .weight(1f) // Take available vertical space
                    .fillMaxWidth() // Fill width
                    .padding(bottom = 16.dp) // Add padding below the image
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
                selectedImageUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    editedBitmap = toGrayscale(bitmap)
                }
            }) {
                Text("Apply Grayscale Filter")
            }
            Button(onClick = {
                editedBitmap?.let { bitmap ->
                    saveImageToGallery(context, bitmap, "edited_image_${System.currentTimeMillis()}")
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
        ImageEditorScreen()
    }
}




