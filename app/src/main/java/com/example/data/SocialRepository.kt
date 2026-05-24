package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class SocialRepository(private val socialDao: SocialDao) {

    val userProfile: Flow<UserProfile?> = socialDao.getUserProfile(1)
    val allPosts: Flow<List<Post>> = socialDao.getAllPosts()
    val allStories: Flow<List<Story>> = socialDao.getAllStories()

    fun getCommentsForPost(postId: Int): Flow<List<Comment>> = socialDao.getCommentsForPost(postId)

    suspend fun insertPost(post: Post): Long {
        val postId = socialDao.insertPost(post)
        refreshPostCounts()
        return postId
    }

    suspend fun toggleLike(postId: Int) {
        val post = socialDao.getPostById(postId) ?: return
        val newIsLiked = !post.isLiked
        val newLikesCount = if (newIsLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
        socialDao.updatePost(post.copy(isLiked = newIsLiked, likesCount = newLikesCount))
    }

    suspend fun addComment(postId: Int, username: String, userAvatarUrl: String, content: String) {
        val comment = Comment(
            postId = postId,
            username = username,
            userAvatarUrl = userAvatarUrl,
            content = content
        )
        socialDao.insertComment(comment)
        
        // Update comments count in Post
        val post = socialDao.getPostById(postId)
        if (post != null) {
            socialDao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        }
    }

    suspend fun deleteComment(postId: Int, commentId: Int) {
        socialDao.deleteComment(commentId)
        val post = socialDao.getPostById(postId)
        if (post != null) {
            socialDao.updatePost(post.copy(commentsCount = maxOf(0, post.commentsCount - 1)))
        }
    }

    suspend fun insertStory(story: Story) {
        socialDao.insertStory(story)
    }

    suspend fun markStoryAsViewed(storyId: Int) {
        socialDao.markStoryAsViewed(storyId)
    }

    suspend fun updateProfile(username: String, displayName: String, bio: String) {
        val currentProfile = socialDao.getUserProfile(1).first()
        val updatedProfile = currentProfile?.copy(
            username = username,
            displayName = displayName,
            bio = bio
        ) ?: UserProfile(
            id = 1,
            username = username,
            displayName = displayName,
            avatarUrl = "aurora",
            bio = bio,
            followersCount = 1240,
            followingCount = 482,
            postsCount = 0
        )
        socialDao.insertUserProfile(updatedProfile)
    }

    private suspend fun refreshPostCounts() {
        val posts = socialDao.getAllPosts().first()
        val userPostsCount = posts.count { it.username == "sveta_developer" }
        val currentProfile = socialDao.getUserProfile(1).first()
        if (currentProfile != null) {
            socialDao.insertUserProfile(currentProfile.copy(postsCount = userPostsCount))
        }
    }

    suspend fun populateInitialDataIfEmpty() {
        val existingProfile = socialDao.getUserProfile(1).first()
        if (existingProfile == null) {
            // Setup personalized default profile for the user
            val defaultProfile = UserProfile(
                id = 1,
                username = "sveta_dev",
                displayName = "Sveta",
                avatarUrl = "aurora", // Gradient designation
                bio = "📱 Kotlin Developer & Interface Designer\n✨ Crafting beautiful and polished visual experiences.",
                followersCount = 2840,
                followingCount = 312,
                postsCount = 5
            )
            socialDao.insertUserProfile(defaultProfile)
        }

        val existingStories = socialDao.getAllStories().first()
        if (existingStories.isEmpty()) {
            val initialStories = listOf(
                Story(
                    username = "lisa_travels",
                    userDisplayName = "Lisa",
                    userAvatarUrl = "indigo",
                    mediaUrl = "sunset_beach"
                ),
                Story(
                    username = "cooking_pro",
                    userDisplayName = "Chef Gordon",
                    userAvatarUrl = "cherry",
                    mediaUrl = "pasta_feast"
                ),
                Story(
                    username = "tech_insider",
                    userDisplayName = "Tech Insider",
                    userAvatarUrl = "teal",
                    mediaUrl = "futuristic_ai"
                ),
                Story(
                    username = "aesthetic_art",
                    userDisplayName = "Aura Art",
                    userAvatarUrl = "amber",
                    mediaUrl = "oil_painting"
                ),
                Story(
                    username = "fitness_journey",
                    userDisplayName = "Coach Mike",
                    userAvatarUrl = "emerald",
                    mediaUrl = "morning_run"
                )
            )
            for (story in initialStories) {
                socialDao.insertStory(story)
            }
        }

        val existingPosts = socialDao.getAllPosts().first()
        if (existingPosts.isEmpty()) {
            val initialPosts = listOf(
                Post(
                    username = "lisa_travels",
                    userDisplayName = "Lisa",
                    userAvatarUrl = "indigo",
                    content = "Chasing the sun! There's nothing like a gorgeous golden hour in Santorini. Highly recommend visiting during the off-season. 🌅✈️ #travel #wanderlust #sunset",
                    imageUrl = "sunset_beach",
                    location = "Santorini, Greece",
                    likesCount = 342,
                    isLiked = false,
                    commentsCount = 2
                ),
                Post(
                    username = "cooking_pro",
                    userDisplayName = "Chef Gordon",
                    userAvatarUrl = "cherry",
                    content = "Freshly handmade tagliatelle tossed with rich slow-cooked wild mushroom ragu, finished with shaved black truffles. Decadence on a plate! 🍝🔥 #cooking #italian #foodie",
                    imageUrl = "pasta_feast",
                    location = "Milan, Italy",
                    likesCount = 1892,
                    isLiked = false,
                    commentsCount = 1
                ),
                Post(
                    username = "aesthetic_art",
                    userDisplayName = "Aura Art",
                    userAvatarUrl = "amber",
                    content = "Finished this abstract oil piece today! It reflects the chaotic motion and gentle stillness of the sea during storms. What mood does this bring out in you? 🎨🌊 #art #oilpainting #creative",
                    imageUrl = "oil_painting",
                    location = "London Art Studio",
                    likesCount = 89,
                    isLiked = false,
                    commentsCount = 0
                ),
                Post(
                    username = "tech_insider",
                    userDisplayName = "Tech Insider",
                    userAvatarUrl = "teal",
                    content = "Exploring the intersection of generative neural networks, dynamic mobile design systems, and device-side compiler optimizations. The future is bright! 🤖⚙️ #android #ai #future",
                    imageUrl = "futuristic_ai",
                    location = "Silicon Valley",
                    likesCount = 512,
                    isLiked = false,
                    commentsCount = 1
                )
            )

            for (post in initialPosts) {
                val pId = socialDao.insertPost(post)
                if (pId > 0) {
                    // Seed initial comments
                    if (post.username == "lisa_travels") {
                        socialDao.insertComment(Comment(postId = pId.toInt(), username = "alex_v", userAvatarUrl = "violet", content = "Unbelievable view! Frame this immediately!"))
                        socialDao.insertComment(Comment(postId = pId.toInt(), username = "cooking_pro", userAvatarUrl = "cherry", content = "Need to visit this spot next summer!"))
                    } else if (post.username == "cooking_pro") {
                        socialDao.insertComment(Comment(postId = pId.toInt(), username = "food_fanatic", userAvatarUrl = "rose", content = "Recipe please chef! Simply magnificent!"))
                    } else if (post.username == "tech_insider") {
                        socialDao.insertComment(Comment(postId = pId.toInt(), username = "sveta_dev", userAvatarUrl = "aurora", content = "This Kotlin/Compose setup is incredibly smooth!"))
                    }
                }
            }
        }
    }
}
