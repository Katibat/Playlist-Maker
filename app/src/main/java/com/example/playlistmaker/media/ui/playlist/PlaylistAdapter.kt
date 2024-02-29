package com.example.playlistmaker.media.ui.playlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistsAdapter(
    private val playlists: MutableList<Playlist>,
    private val listener: Listener
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistHolder>() {

    inner class PlaylistHolder(private val binding: PlaylistViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(playlist: Playlist) {
            binding.apply {
                playlistName.text = playlist.name
                numberOfTracks.text = "${playlist.countTracks.toString()} " +
                        getTrackWordForm(playlist.countTracks ?: 0)
                Glide.with(root.context)
                    .load(playlist.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(playlistcover)
                root.setOnClickListener { listener.onClick(playlist) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val binding =
            PlaylistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistHolder(binding)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun updateData(newPlaylistsList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylistsList)
        notifyDataSetChanged()
    }

    private fun getTrackWordForm(count: Int): String {
        return when {
            count % 100 in 11..14 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

    interface Listener {
        fun onClick(playlist: Playlist)
    }
}