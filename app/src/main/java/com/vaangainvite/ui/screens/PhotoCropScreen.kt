package com.vaangainvite.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vaangainvite.core.image.PhotoBitmapLoader
import com.vaangainvite.core.image.PhotoCropExporter
import com.vaangainvite.core.image.PhotoCropTransform
import com.vaangainvite.data.model.InvitationLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCropScreen(
    sourceUri: Uri,
    language: InvitationLanguage,
    initialTransform: PhotoCropTransform?,
    onConfirm: (Uri, PhotoCropTransform) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var transform by remember { mutableStateOf<PhotoCropTransform?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sourceUri, initialTransform) {
        runCatching {
            withContext(Dispatchers.Default) {
                val loaded = PhotoBitmapLoader.load(context, sourceUri)
                val initial = initialTransform ?: PhotoCropTransform.autoDetect(loaded)
                loaded to initial
            }
        }.onSuccess { (loadedBitmap, initialCrop) ->
            bitmap = loadedBitmap
            transform = initialCrop
        }.onFailure { error ->
            loadError = error.message ?: language.editorPhotoCropError
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = language.editorPhotoCropTitle) },
                navigationIcon = {
                    TextButton(onClick = onCancel, enabled = !isSaving) {
                        Text(text = language.editorDialogCancel)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = language.editorPhotoCropHint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when {
                loadError != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = loadError ?: language.editorPhotoCropError)
                    }
                }
                bitmap == null || transform == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    val imageBitmap = bitmap!!.asImageBitmap()
                    val cropTransform = transform!!
                    val frameAspect = PhotoCropTransform.targetAspectRatio
                    val currentBitmap by rememberUpdatedState(bitmap)
                    val currentTransform by rememberUpdatedState(transform)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .aspectRatio(frameAspect)
                                .pointerInput(sourceUri) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        val bmp = currentBitmap ?: return@detectTransformGestures
                                        val current = currentTransform ?: return@detectTransformGestures
                                        val displayScale = size.width / current.width
                                        transform = current
                                            .pan(
                                                deltaX = -pan.x / displayScale,
                                                deltaY = -pan.y / displayScale,
                                                bitmapWidth = bmp.width,
                                                bitmapHeight = bmp.height
                                            )
                                            .zoom(
                                                scaleFactor = zoom,
                                                bitmapWidth = bmp.width,
                                                bitmapHeight = bmp.height,
                                                targetAspect = frameAspect
                                            )
                                    }
                                }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val cropWidth = cropTransform.width
                                val scale = size.width / cropWidth
                                val imageLeft = -cropTransform.srcLeft * scale
                                val imageTop = -cropTransform.srcTop * scale

                                withTransform({
                                    translate(imageLeft, imageTop)
                                    scale(scale, scale, pivot = Offset.Zero)
                                }) {
                                    drawImage(image = imageBitmap, topLeft = Offset.Zero)
                                }

                                val overlayPath = Path().apply {
                                    addRect(Rect(Offset.Zero, size))
                                    addRoundRect(
                                        androidx.compose.ui.geometry.RoundRect(
                                            rect = Rect(Offset.Zero, size),
                                            cornerRadius = CornerRadius(size.minDimension * 0.08f)
                                        )
                                    )
                                    fillType = PathFillType.EvenOdd
                                }
                                drawPath(
                                    path = overlayPath,
                                    color = Color.Black.copy(alpha = 0.45f)
                                )
                                drawRoundRect(
                                    color = Color(0xFFF7C948),
                                    topLeft = Offset.Zero,
                                    size = size,
                                    cornerRadius = CornerRadius(size.minDimension * 0.08f),
                                    style = Stroke(width = 4f)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving
                ) {
                    Text(text = language.editorDialogCancel)
                }
                Button(
                    onClick = {
                        val currentBitmap = bitmap ?: return@Button
                        val currentTransform = transform ?: return@Button
                        isSaving = true
                        scope.launch {
                            runCatching {
                                withContext(Dispatchers.Default) {
                                    PhotoCropExporter.exportCroppedUriFromBitmap(
                                        context = context,
                                        source = currentBitmap,
                                        transform = currentTransform
                                    )
                                }
                            }.onSuccess { croppedUri ->
                                onConfirm(croppedUri, currentTransform)
                            }.onFailure {
                                loadError = language.editorPhotoCropError
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = bitmap != null && transform != null && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(4.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = language.editorPhotoCropConfirm)
                    }
                }
            }
        }
    }
}
