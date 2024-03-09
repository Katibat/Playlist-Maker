package com.example.playlistmaker.playlist.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistsAdapterBottomSheet(
    private val playlistsList: MutableList<Playlist>,
    private val clickListener: Listener
) : RecyclerView.Adapter<PlaylistsAdapterBottomSheet.PlaylistHolderSmall>() {

    inner class PlaylistHolderSmall(private val binding: PlaylistViewBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(playlist: Playlist) {
            binding.apply {
                tvPlaylistNameBS.text = playlist.name
                countTracksBS.text = "${playlist.countTracks} " +
                        getTrackWordForm(playlist.countTracks ?: 0)
                Glide.with(root.context)
                    .load(playlist.imageUrl)
                    .transform(RoundedCorners(2))
                    .placeholder(R.drawable.placeholder)
                    .into(playlistImageBS)
                root.setOnClickListener { clickListener.onClick(playlist) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolderSmall {
        val binding =
            PlaylistViewBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistHolderSmall(binding)
    }

    override fun getItemCount(): Int = playlistsList.size

    override fun onBindViewHolder(holder: PlaylistHolderSmall, position: Int) {
        holder.bind(playlistsList[position])
    }

    fun updateData(newPlaylistsList: List<Playlist>) {
        playlistsList.clear()
        playlistsList.addAll(newPlaylistsList)
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