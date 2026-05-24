package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SocialApplication
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SocialViewModel(
    application: Application,
    private val repository: SocialRepository
) : AndroidViewModel(application) {

    // Reactive database data
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPosts: StateFlow<List<Post>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStories: StateFlow<List<Story>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI interactive properties
    private val _aiGenerationLoading = MutableStateFlow(false)
    val aiGenerationLoading = _aiGenerationLoading.asStateFlow()

    private val _aiGeneratedCaption = MutableStateFlow("")
    val aiGeneratedCaption = _aiGeneratedCaption.asStateFlow()

    // Active full-screen story viewer state
    private val _activeStoryGroup = MutableStateFlow<List<Story>?>(null)
    val activeStoryGroup = _activeStoryGroup.asStateFlow()

    private val _activeStoryIndex = MutableStateFlow(0)
    val activeStoryIndex = _activeStoryIndex.asStateFlow()

    // Comments bottom-sheet state
    private val _activeCommentsPostId = MutableStateFlow<Int?>(null)
    val activeCommentsPostId = _activeCommentsPostId.asStateFlow()

    val activeComments: StateFlow<List<Comment>> = _activeCommentsPostId
        .flatMapLatest { id ->
            if (id != null) repository.getCommentsForPost(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun openComments(postId: Int) {
        _activeCommentsPostId.value = postId
    }

    fun closeComments() {
        _activeCommentsPostId.value = null
    }

    fun addComment(postId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val profile = userProfile.value
            val authorName = profile?.username ?: "sveta_dev"
            val authorAvatar = profile?.avatarUrl ?: "aurora"
            repository.addComment(postId, authorName, authorAvatar, content)
        }
    }

    fun deleteComment(postId: Int, commentId: Int) {
        viewModelScope.launch {
            repository.deleteComment(postId, commentId)
        }
    }

    fun createPost(content: String, presetThemeId: String, location: String?) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val profile = userProfile.value
            val authorUsername = profile?.username ?: "sveta_dev"
            val authorDisplayName = profile?.displayName ?: "Sveta"
            val authorAvatar = profile?.avatarUrl ?: "aurora"

            val newPost = Post(
                username = authorUsername,
                userDisplayName = authorDisplayName,
                userAvatarUrl = authorAvatar,
                content = content,
                imageUrl = presetThemeId,
                location = location?.ifBlank { null },
                likesCount = 0,
                isLiked = false,
                commentsCount = 0
            )
            repository.insertPost(newPost)
            _aiGeneratedCaption.value = "" // Reset AI cache
        }
    }

    fun createStory(presetThemeId: String, simpleTextOverlay: String?) {
        viewModelScope.launch {
            val profile = userProfile.value
            val authorUsername = profile?.username ?: "sveta_dev"
            val authorDisplayName = profile?.displayName ?: "Sveta"
            val authorAvatar = profile?.avatarUrl ?: "aurora"

            val newStory = Story(
                username = authorUsername,
                userDisplayName = authorDisplayName,
                userAvatarUrl = authorAvatar,
                mediaUrl = presetThemeId,
                timestamp = System.currentTimeMillis()
            )
            repository.insertStory(newStory)
        }
    }

    fun updateProfile(username: String, displayName: String, bio: String) {
        viewModelScope.launch {
            repository.updateProfile(username, displayName, bio)
        }
    }

    fun generateAiCaption(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _aiGenerationLoading.value = true
            _aiGeneratedCaption.value = ""
            val caption = GeminiService.generateCaption(prompt)
            _aiGeneratedCaption.value = caption
            _aiGenerationLoading.value = false
        }
    }

    fun openStoryGroup(stories: List<Story>, index: Int) {
        _activeStoryGroup.value = stories
        _activeStoryIndex.value = index
        viewModelScope.launch {
            repository.markStoryAsViewed(stories[index].id)
        }
    }

    fun nextStory() {
        val group = _activeStoryGroup.value ?: return
        val current = _activeStoryIndex.value
        if (current + 1 < group.size) {
            _activeStoryIndex.value = current + 1
            viewModelScope.launch {
                repository.markStoryAsViewed(group[current + 1].id)
            }
        } else {
            closeStory()
        }
    }

    fun previousStory() {
        val current = _activeStoryIndex.value
        if (current > 0) {
            _activeStoryIndex.value = current - 1
        }
    }

    fun closeStory() {
        _activeStoryGroup.value = null
        _activeStoryIndex.value = 0
    }

    class Factory(private val application: SocialApplication) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SocialViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SocialViewModel(application, application.repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
