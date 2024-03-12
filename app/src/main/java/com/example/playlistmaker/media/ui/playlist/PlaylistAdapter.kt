package com.example.playlistmaker.media.ui.playlist

import android.annotation.SuppressLint
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistsAdapter(
    private val playlists: MutableList<Playlist>,
    private val clickListener: Listener
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistHolder>() {

    inner class PlaylistHolder(private val binding: PlaylistViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(playlist: Playlist) {
            binding.apply {
                tvPlaylistName.text = playlist.name
                tvCountTracks.text = "${playlist.countTracks} ${
                    itemView.resources.getQuantityString(
                        R.plurals.playlist_count_tracks,
                        playlist.countTracks ?: 0
                    )
                }"
                val filePath = File(
                    itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "playlist"
                )
                Glide.with(itemView)
                    .load(File(filePath, "image_${playlist.name}.jpg"))
                    .placeholder(R.drawable.placeholder)
                    .transform(RoundedCorners(8))
                    .into(ivPlaylist)
                root.setOnClickListener { clickListener.onClick(playlist) }
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

    interface Listener {
        fun onClick(playlist: Playlist)
    }
}