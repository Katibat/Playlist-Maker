package com.example.playlistmaker.track

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.AudioPlayerActivity
import com.example.playlistmaker.SearchHistory.addTrackInHistoryList
import com.example.playlistmaker.track.Track.Companion.TRACK

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {
    var tracksList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracksList[position])
        holder.itemView.setOnClickListener {
            addTrackInHistoryList(tracksList[position])
            val intent = Intent(it.context, AudioPlayerActivity::class.java)
            intent.putExtra(TRACK, tracksList[position])
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = tracksList.size
}