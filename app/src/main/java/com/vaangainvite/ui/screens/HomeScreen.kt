package com.vaangainvite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vaangainvite.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vaangainvite.data.model.InvitationCategory
import com.vaangainvite.ui.viewmodel.InviteViewModel

private val HomeSandalwood = Color(0xFFFFF4E6)
private val HomeMaroon = Color(0xFF8B1E3F)
private val HomeGold = Color(0xFFF7C948)
private val HomeLeaf = Color(0xFF2E7D32)
private val CategoryCardGray = Color(0xFF5C5C5C)
private val CategoryCardTitle = Color(0xFFF5F5F5)
private val CategoryCardDescription = Color(0xFFE0E0E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: InviteViewModel,
    onCategorySelected: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = HomeSandalwood,
        topBar = {
            TopAppBar(
                title = { Text(text = "Vaanga Invite") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HomeMaroon,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeSandalwood)
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_traditional_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.80f),
                contentScale = ContentScale.Crop
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Header()
                }
                items(state.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = {
                            viewModel.selectCategory(category.id)
                            onCategorySelected()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = HomeMaroon
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Create beautiful South Indian invitations",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pick a category, choose a local template, add details, then save or share on WhatsApp.",
                style = MaterialTheme.typography.bodyLarge,
                color = HomeGold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DecorativeDot(color = HomeGold)
                DecorativeDot(color = HomeLeaf)
                DecorativeDot(color = Color(0xFFE75A7C))
            }
        }
    }
}

@Composable
private fun DecorativeDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun CategoryCard(
    category: InvitationCategory,
    onClick: () -> Unit
) {
    val accent = Color(category.accentColor)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CategoryCardGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = category.iconResId),
                    contentDescription = category.title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CategoryCardTitle,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CategoryCardDescription
                )
            }
        }
    }
}
