package com.example.playlistmaker.track

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.SearchHistory.addTrackInHistoryList

class TrackAdapter(private val clickListener: TrackClickListener) :
        RecyclerView.Adapter<TrackViewHolder>() {
    var tracksList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracksList[position])
        holder.itemView.setOnClickListener {
            addTrackInHistoryList(tracksList[position])
            clickListener.onTrackClick(tracksList[position])
        }
    }

    override fun getItemCount(): Int = tracksList.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}