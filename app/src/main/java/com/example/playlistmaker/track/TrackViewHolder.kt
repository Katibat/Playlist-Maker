package com.example.playlistmaker.track

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getTrackTimeMillis
import com.example.playlistmaker.R

class TrackViewHolder(parent: ViewGroup):
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
        .inflate(R.layout.track_view, parent, false)) {
    var trackName: TextView = itemView.findViewById(R.id.tvTrackName)
    var trackArtist: TextView = itemView.findViewById(R.id.tvTrackArtist)
    var trackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    private var trackImage: ImageView = itemView.findViewById(R.id.ivTrack)

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = getTrackTimeMillis(track.trackTimeMillis)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackImage)
    }
}