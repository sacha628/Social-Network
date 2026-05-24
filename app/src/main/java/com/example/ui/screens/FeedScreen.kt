package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Comment
import com.example.data.Post
import com.example.data.Story
import com.example.ui.SocialViewModel
import com.example.ui.components.AestheticImageContainer
import com.example.ui.components.UserAvatar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val stories by viewModel.allStories.collectAsStateWithLifecycle()
    
    val activeStoryGroup by viewModel.activeStoryGroup.collectAsStateWithLifecycle()
    val activeStoryIndex by viewModel.activeStoryIndex.collectAsStateWithLifecycle()
    val activeCommentsPostId by viewModel.activeCommentsPostId.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("feed_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sleek Interface Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "echo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.75).sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Search */ },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { /* Notification trigger */ },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("top_notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // News Feed List (including Story Row at top)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Stories horizontal row
                item {
                    StoriesRow(
                        stories = stories,
                        onStoryClick = { story ->
                            // Look up story in group to trigger correct index launch
                            val index = stories.indexOfFirst { it.id == story.id }
                            if (index != -1) {
                                viewModel.openStoryGroup(stories, index)
                            }
                        }
                    )
                }

                // Vertical Feed posts
                items(posts, key = { it.id }) { post ->
                    VerticalPostCard(
                        post = post,
                        onLikeClick = { viewModel.toggleLike(post.id) },
                        onCommentClick = { viewModel.openComments(post.id) }
                    )
                }
            }
        }

        // Expanded full-screen story reader overlay using viewModel groups
        if (activeStoryGroup != null && activeStoryIndex >= 0 && activeStoryIndex < activeStoryGroup!!.size) {
            val activeStory = activeStoryGroup!![activeStoryIndex]
            StoryViewOverlay(
                story = activeStory,
                onNext = { viewModel.nextStory() },
                onPrevious = { viewModel.previousStory() },
                onDismiss = { viewModel.closeStory() }
            )
        }

        // Bottom sheet for comments layout
        if (activeCommentsPostId != null) {
            val activePost = posts.find { it.id == activeCommentsPostId }
            if (activePost != null) {
                CommentBottomSheet(
                    post = activePost,
                    comments = activeComments,
                    onAddComment = { text -> viewModel.addComment(activePost.id, text) },
                    onDismiss = { viewModel.closeComments() }
                )
            }
        }
    }
}

@Composable
fun StoriesRow(
    stories: List<Story>,
    onStoryClick: (Story) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stories, key = { it.id }) { story ->
                StoryCircle(
                    story = story,
                    onClick = { onStoryClick(story) }
                )
            }
        }
        Divider(
            modifier = Modifier.padding(top = 10.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            thickness = 0.5.dp
        )
    }
}

@Composable
fun StoryCircle(
    story: Story,
    onClick: () -> Unit
) {
    val ringColor = if (story.isViewed) {
        listOf(
            MaterialTheme.colorScheme.outlineVariant,
            MaterialTheme.colorScheme.outlineVariant
        )
    } else {
        listOf(
            Color(0xFF6366F1), // Indigo 500
            Color(0xFFA855F7), // Purple 500
            Color(0xFFEC4899), // Pink 500
            Color(0xFF6366F1)  // Indigo 500
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("story_circle_${story.id}")
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(ringColor),
                    shape = CircleShape
                )
                .padding(4.5.dp)
        ) {
            UserAvatar(
                presetId = story.userAvatarUrl,
                displayName = story.userDisplayName,
                size = 56.dp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = story.username,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (story.isViewed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(72.dp)
        )
    }
}

@Composable
fun VerticalPostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    var isHeartAnimating by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (isHeartAnimating) 1.25f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "HeartScale"
    )
    val heartAlpha by animateFloatAsState(
        targetValue = if (isHeartAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "HeartAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Post header details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    presetId = post.userAvatarUrl,
                    displayName = post.userDisplayName,
                    size = 40.dp
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = post.userDisplayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!post.location.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location Pin",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = post.location,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { /* More choices */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Image canvas component
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.22f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!post.isLiked) {
                                    onLikeClick()
                                }
                                isHeartAnimating = true
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AestheticImageContainer(
                    presetId = post.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )

                // Elegant overlay heart animation for double taps
                if (isHeartAnimating || heartAlpha > 0.05f) {
                    LaunchedEffect(isHeartAnimating) {
                        if (isHeartAnimating) {
                            delay(650)
                            isHeartAnimating = false
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Double Tap Heart",
                        tint = Color.White.copy(alpha = heartAlpha),
                        modifier = Modifier
                            .size(72.dp)
                            .scale(heartScale)
                            .align(Alignment.Center)
                    )
                }
            }

            // Feed actions bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Heart icon
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("post_like_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Post",
                        tint = if (post.isLiked) Color(0xFFFA3E3E) else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Bubble icon
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("post_comment_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Comment Post",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Share icon
                IconButton(
                    onClick = { /* Demo share click */ },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = "Share Post",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // High aesthetic statistics text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "${post.likesCount} likes",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Beautifully highlight matching hashtag symbols
                val captionHighlight = buildAnnotatedString {
                    val tokens = post.content.split(" ")
                    tokens.forEachIndexed { idx, token ->
                        if (token.startsWith("#")) {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append(token)
                            }
                        } else {
                            append(token)
                        }
                        if (idx < tokens.size - 1) append(" ")
                    }
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("@${post.username} ")
                        }
                        append(captionHighlight)
                    },
                    fontSize = 12.5.sp,
                    lineHeight = 16.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (post.commentsCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "View all ${post.commentsCount} comments",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onCommentClick() }
                            .testTag("view_all_comments_trigger_${post.id}")
                    )
                }
            }
        }
    }
}

@Composable
fun StoryViewOverlay(
    story: Story,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    // Automated linear story timer (5 seconds duration)
    LaunchedEffect(story) {
        progress = 0f
        val stepCount = 100
        val delayTime = 50L
        for (i in 1..stepCount) {
            delay(delayTime)
            progress = i / 100f
        }
        onNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("story_overlay_view")
    ) {
        // Main Story visual canvas
        AestheticImageContainer(
            presetId = story.mediaUrl,
            overlayText = "Story by @${story.username}",
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        // Story player control triggers
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onPrevious() }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onNext() }
            )
        }

        // Header controls (Close, Progress Bars)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Horizontal Timer Bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(CircleShape),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.35f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(
                        presetId = story.userAvatarUrl,
                        displayName = story.userDisplayName,
                        size = 34.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = story.userDisplayName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Story",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    post: Post,
    comments: List<Comment>,
    onAddComment: (text: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .padding(bottom = 16.dp)
                .testTag("comments_bottom_sheet")
        ) {
            // Header
            Text(
                text = "Comments (${comments.size})",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp)
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Scrollable comments sequence
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No comments yet. Write the first one!",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            UserAvatar(
                                presetId = comment.userAvatarUrl,
                                displayName = comment.username,
                                size = 32.dp
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp)
                            ) {
                                Text(
                                    text = comment.username,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = comment.content,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Text(
                                text = "Comment",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Bottom text publisher block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Write comment...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_box"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onAddComment(newCommentText)
                            newCommentText = ""
                        }
                    },
                    enabled = newCommentText.isNotBlank(),
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .testTag("comment_submit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Submit comment icon",
                        tint = if (newCommentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
