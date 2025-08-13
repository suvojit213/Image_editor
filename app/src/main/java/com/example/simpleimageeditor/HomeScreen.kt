package com.example.simpleimageeditor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.simpleimageeditor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = DarkBackground, // Set dark background for Scaffold
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("image_editor") },
                containerColor = Color.Transparent, // Make FAB transparent to show brush
                modifier = Modifier.size(80.dp) // Make FAB larger
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary // Repeat primary for a smoother transition
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "Create new image",
                        tint = Color.White, // White icon for contrast
                        modifier = Modifier.size(40.dp) // Larger icon
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBar(containerColor = DarkBackground) { // Dark background for BottomAppBar
                // Optional: Add navigation items here later
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Image Editor",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = LightText, // Light text for contrast
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Tap the '+' button to start editing!",
                style = MaterialTheme.typography.bodyLarge.copy(color = LightText) // Light text
            )
            // Placeholder for recent images
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "Recent Images (Coming Soon)",
                style = MaterialTheme.typography.bodySmall.copy(color = LightText.copy(alpha = 0.7f)) // Slightly faded light text
            )
        }
    }
}
