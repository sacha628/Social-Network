package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Post
import com.example.ui.SocialViewModel
import com.example.ui.components.AestheticImageContainer
import com.example.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    
    // Filter posts published by this user
    val userPosts = remember(posts, profile) {
        val currUsername = profile?.username ?: "sveta_dev"
        posts.filter { it.username == currUsername }
    }

    var isGridView by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Profile Info Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .border(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .padding(5.dp)
                        ) {
                            UserAvatar(
                                presetId = profile?.avatarUrl ?: "aurora",
                                displayName = profile?.displayName ?: "Sveta",
                                size = 76.dp
                            )
                        }

                        // Stats Dashboard Columns
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(count = userPosts.size, label = "Posts")
                            ProfileStatItem(count = profile?.followersCount ?: 2840, label = "Followers")
                            ProfileStatItem(count = profile?.followingCount ?: 312, label = "Following")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bio Details
                    Text(
                        text = profile?.displayName ?: "Sveta",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "@${profile?.username ?: "sveta_dev"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                    Text(
                        text = profile?.bio ?: "Kotlin Designer & Developer",
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit Profile Trigger
                    Button(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("edit_profile_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile icon",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Dual Tab View Toggle (Grid vs. List)
            item {
                Column {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { isGridView = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Outlined.GridView,
                                contentDescription = "Grid View",
                                tint = if (isGridView) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(
                            onClick = { isGridView = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Outlined.List,
                                contentDescription = "List View",
                                tint = if (!isGridView) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }

            // Conditional view of user posts
            if (userPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.GridView,
                                contentDescription = "No local posts",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "No Posts Recorded",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                "Share some moments and they will appear in your profile.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            } else if (isGridView) {
                // Renting 3-column items inside manual lists or rows inside LazyColumn to avoid nesting scrollable widgets
                val chunkedList = userPosts.chunked(3)
                items(chunkedList) { rowPosts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPosts.forEach { post ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        // View details / swap views
                                        isGridView = false
                                    }
                            ) {
                                AestheticImageContainer(
                                    presetId = post.imageUrl,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        // Pad missing grid nodes for rows with < 3 items
                        val paddingItemsCount = 3 - rowPosts.size
                        if (paddingItemsCount > 0) {
                            repeat(paddingItemsCount) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // Vertical cards list
                items(userPosts, key = { it.id }) { post ->
                    VerticalPostCard(
                        post = post,
                        onLikeClick = { viewModel.toggleLike(post.id) },
                        onCommentClick = { viewModel.openComments(post.id) }
                    )
                }
            }
        }

        // Custom Profile Edit dialog
        if (showEditDialog) {
            EditProfileDialog(
                currentDisplayName = profile?.displayName ?: "Sveta",
                currentUsername = profile?.username ?: "sveta_dev",
                currentBio = profile?.bio ?: "",
                onSave = { u, d, b ->
                    viewModel.updateProfile(u, d, b)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false }
            )
        }
    }
}

@Composable
fun ProfileStatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatCount(count),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun EditProfileDialog(
    currentDisplayName: String,
    currentUsername: String,
    currentBio: String,
    onSave: (username: String, displayName: String, bio: String) -> Unit,
    onDismiss: () -> Unit
) {
    var displayName by remember { mutableStateOf(currentDisplayName) }
    var username by remember { mutableStateOf(currentUsername) }
    var bio by remember { mutableStateOf(currentBio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_display_name_field")
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_username_field")
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_bio_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(username, displayName, bio) },
                enabled = displayName.isNotBlank() && username.isNotBlank(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatCount(num: Int): String {
    return if (num >= 1000) {
        val k = num / 1000.0
        String.format("%.1fk", k)
    } else {
        num.toString()
    }
}
