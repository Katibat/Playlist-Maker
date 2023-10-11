package com.example.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.App.Companion.getTrackTimeMillis

class TrackViewHolder(private val binding: TrackViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.tvTrackName.text = track.trackName
        binding.tvTrackArtist.text = track.artistName
        binding.tvTrackTime.text = getTrackTimeMillis(track.trackTimeMillis)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(binding.ivTrack)
    }
}