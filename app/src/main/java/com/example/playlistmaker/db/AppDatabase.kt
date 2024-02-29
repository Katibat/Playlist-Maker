package com.example.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.db.dao.PlaylistDao
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.db.dao.TrackDao
import com.example.playlistmaker.db.entity.TrackEntity

@Database(version = 5,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE playlist_table (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "name TEXT NOT NULL," +
                            "description TEXT," +
                            "image_url TEXT," +
                            "tracks_ids TEXT," +
                            "count_tracks INTEGER)"
                )
                database.execSQL(
                    "INSERT INTO playlist_table (id, name, description, image_url, tracks_ids, count_tracks) " +
                            "SELECT id, name, description, image_url, tracks_ids, count_tracks " +
                            "FROM favorite_playlists"
                )
                database.execSQL("DROP TABLE favorite_playlists")
            }
        }
    }
}