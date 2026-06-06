package com.vaangainvite.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.vaangainvite.core.image.TemplatePreviewCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SubsampledResourceImage(
    @DrawableRes resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    maxDimension: Int = TemplatePreviewCache.LIST_THUMBNAIL_MAX_PX,
    showLoadingIndicator: Boolean = false
) {
    val context = LocalContext.current
    var bitmap by remember(resId, maxDimension) {
        mutableStateOf<ImageBitmap?>(TemplatePreviewCache.peek(resId, maxDimension))
    }

    LaunchedEffect(resId, maxDimension) {
        if (bitmap == null) {
            bitmap = withContext(Dispatchers.IO) {
                TemplatePreviewCache.load(context, resId, maxDimension)
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (showLoadingIndicator) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        }
    }
}
