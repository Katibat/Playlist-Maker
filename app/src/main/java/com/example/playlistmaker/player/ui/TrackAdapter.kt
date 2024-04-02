package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.utils.App

class TrackAdapter(
    var tracksList: ArrayList<Track>,
    var onItemClickListener: OnItemClickListener,
    val onItemLongClickListener: OnItemLongClickListener
) :
    RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    class TrackHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = TrackViewBinding.bind(item)
        fun bind(
            track: Track,
            onItemClickListener: OnItemClickListener,
            onItemLongClickListener: OnItemLongClickListener
        ) = with(binding) {
            binding.tvTrackName.text = track.trackName
            binding.tvTrackArtist.text = track.artistName
            binding.tvTrackTime.text = App.getTrackTimeMillis(track.trackTimeMillis)
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(2))
                .into(binding.ivTrack)
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(track)
            }
            itemView.setOnLongClickListener {
                onItemLongClickListener.onItemLongClick(track) ?: false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val layoutInspector =
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackHolder(layoutInspector)
    }

    override fun getItemCount(): Int = tracksList.size

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(tracksList[position], onItemClickListener, onItemLongClickListener)
    }

    interface OnItemClickListener {
        fun onItemClick(track: Track)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(track: Track): Boolean
    }

    fun clearTracks() {
        tracksList = ArrayList()
    }
}