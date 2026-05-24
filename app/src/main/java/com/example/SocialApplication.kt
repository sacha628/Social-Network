package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.SocialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SocialApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "social_network_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    val repository by lazy {
        SocialRepository(database.socialDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Prefill database on first run
        CoroutineScope(SupervisorJob()).launch {
            repository.populateInitialDataIfEmpty()
        }
    }
}
