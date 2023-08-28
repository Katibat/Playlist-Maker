package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.TRACK
import com.example.playlistmaker.track.Track
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AudioPlayerActivity : AppCompatActivity() {
    private var tittleTrackName: TextView? = null
    private var tittleTrackArtist: TextView? = null
    private var realTimeTrack: TextView? = null
    private var durationTrack: TextView? = null
    private var album: TextView? = null
    private var year: TextView? = null
    private var genre: TextView? = null
    private var country: TextView? = null
    private var tittleAlbum: TextView? = null
    private var imagePlayer: ImageView? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        tittleTrackName = findViewById(R.id.tvTittleTrackName)
        tittleTrackArtist = findViewById(R.id.tvTittleTrackArtist)
        realTimeTrack = findViewById(R.id.tvDurationTrack)
        durationTrack = findViewById(R.id.tvDurationContent)
        album = findViewById(R.id.tvAlbumContent)
        year = findViewById(R.id.tvYearContent)
        genre = findViewById(R.id.tvGenreContent)
        country = findViewById(R.id.tvCountryContent)
        tittleAlbum = findViewById(R.id.tvAlbumTittle)
        imagePlayer = findViewById(R.id.ivImagePlayer)

        // Настройка Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val track = intent.getParcelableExtra<Track>(TRACK)!!
        playTrack(track)
    }

    private fun playTrack(track: Track) {
        tittleTrackName?.text = track.trackName
        tittleTrackArtist?.text = track.artistName
        realTimeTrack?.text = "00:30"
        durationTrack?.text = SimpleDateFormat("mm:ss",
            Locale.getDefault()).format(track.trackTimeMillis)
        if (track.collectionName.isEmpty()) {
            tittleAlbum?.visibility = View.GONE
            album?.visibility = View.GONE
        } else {
            album?.text = track.collectionName
        }

        year?.text = track.releaseDate.take(4)
        genre?.text = track.primaryGenreName
        country?.text = track.country
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(imagePlayer!!)
    }
}