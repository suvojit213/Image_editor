package com.example.simpleimageeditor

import android.Manifest
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.simpleimageeditor.ui.theme.LightText
import com.example.simpleimageeditor.ui.theme.DarkBackground
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImageContent
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.foundation.background
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch 

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val media = remember { mutableStateListOf<Uri>() }

    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            loadMedia(context, media, scope)
        } else {
            // Handle permission denied
        }
    }

    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Photos", color = MaterialTheme.colorScheme.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background, // Set dark background for Scaffold
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for floating effect
                    .clip(RoundedCornerShape(16.dp)), // Rounded corners
                containerColor = MaterialTheme.colorScheme.surface // Use surface color for the bar
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.PhotoLibrary, "Photos", tint = MaterialTheme.colorScheme.onSurface)
                        Text("Photos", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Collections, "Collections", tint = MaterialTheme.colorScheme.onSurface)
                        Text("Collections", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { navController.navigate("search_screen") }
                    ) {
                        Icon(Icons.Filled.Search, "Search", tint = MaterialTheme.colorScheme.onSurface)
                        Text("Search", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (media.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No images found or permission denied.", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(media, key = { uri -> uri.toString() }) { uri ->
                    SubcomposeAsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                // Navigate to ImageDetailScreen
                                navController.navigate("image_detail/${Uri.encode(uri.toString())}")
                            }
                            .clip(RoundedCornerShape(8.dp)), // Added rounded corners to images
                        contentScale = ContentScale.Crop
                    ) {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage, // Or a placeholder icon
                                    contentDescription = "Loading/Error",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        } else {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }
        }
    }
}

private fun loadMedia(context: android.content.Context, media: SnapshotStateList<Uri>, scope: CoroutineScope) {
    media.clear()
    ImageTextRecognizer.clearRecognizedTexts() // Clear previous recognized texts

    scope.launch(Dispatchers.IO) { // Launch a new coroutine on IO dispatcher
        val collectionImages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val collectionVideos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATE_ADDED
        )

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        val mediaList = mutableListOf<Pair<Uri, Long>>()

        // Query images
        context.contentResolver.query(
            collectionImages,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    collectionImages,
                    id
                )
                mediaList.add(Pair(contentUri, dateAdded))
                ImageTextRecognizer.recognizeTextFromImage(context, contentUri) // Uncommented
            }
        }

        // Query videos
        context.contentResolver.query(
            collectionVideos,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    collectionVideos,
                    id
                )
                mediaList.add(Pair(contentUri, dateAdded))
            }
        }

        // Sort and add to the list
        mediaList.sortByDescending { it.second }
        withContext(Dispatchers.Main) { // Switch to Main dispatcher for UI update
            media.addAll(mediaList.map { it.first })
        }
    }
}
