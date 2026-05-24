package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Currently logged in user defaults to id = 1
    val username: String,
    val displayName: String,
    val avatarUrl: String, // Gradient code or image URL
    val bio: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val userDisplayName: String,
    val userAvatarUrl: String,
    val content: String,
    val imageUrl: String, // Can be a local gradient preset ID or a remote URL
    val location: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val username: String,
    val userAvatarUrl: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val userDisplayName: String,
    val userAvatarUrl: String,
    val mediaUrl: String, // Image placeholder or preset
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false
)

@Dao
interface SocialDao {
    // Profiling queries
    @Query("SELECT * FROM user_profiles WHERE id = :id")
    fun getUserProfile(id: Int): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Posts queries
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Update
    suspend fun updatePost(post: Post)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: Int): Post?

    // Comments queries
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Int)

    // Stories queries
    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getAllStories(): Flow<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story)

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :storyId")
    suspend fun markStoryAsViewed(storyId: Int)
}

@Database(
    entities = [UserProfile::class, Post::class, Comment::class, Story::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun socialDao(): SocialDao
}
