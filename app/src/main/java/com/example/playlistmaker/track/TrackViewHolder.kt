package com.example.playlistmaker.track

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var trackName: TextView = itemView.findViewById(R.id.track_name)
    var trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    var trackTime: TextView = itemView.findViewById(R.id.track_time)
    private var trackImage: ImageView = itemView.findViewById(R.id.track_image)

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackImage)
    }
}