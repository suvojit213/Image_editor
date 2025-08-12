package com.example.simpleimageeditor

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.simpleimageeditor.ui.theme.DarkBackground
import com.example.simpleimageeditor.ui.theme.LightText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(navController: NavController, imageUri: String?) {
    val uri = imageUri?.let { Uri.decode(it) }?.let { Uri.parse(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Detail", color = LightText) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = LightText)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        uri?.let {
                            navController.navigate("image_editor/${Uri.encode(it.toString())}")
                        }
                    }) {
                        Icon(Icons.Filled.Edit, "Edit", tint = LightText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground // Set dark background for Scaffold
    ) { paddingValues ->
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black), // Black background for image viewer
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Image not found.", color = LightText)
            }
        }
    }
}
