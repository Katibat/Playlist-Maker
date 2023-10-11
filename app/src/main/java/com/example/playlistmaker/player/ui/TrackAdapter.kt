package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.SearchHistory.addTrackInHistoryList
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(private val clickListener: TrackClickListener) :
        RecyclerView.Adapter<TrackViewHolder>() {
    var tracksList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackViewBinding.inflate(layoutInspector, parent, false))
    }

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