// GalleryScreen.kt
package com.example.trainingapp.screens.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(
    imageUrls: List<String>,
    onImageClick: (String) -> Unit
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(imageUrls) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onImageClick(url) }
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}


