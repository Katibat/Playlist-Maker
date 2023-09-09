package com.example.playlistmaker.track

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.AudioPlayerActivity
import com.example.playlistmaker.track.Track.Companion.TRACK

class TrackAdapter
//    (private val clickListener: MovieClickListener)
    :
    RecyclerView.Adapter<TrackViewHolder>() {
    var tracksList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracksList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
//            addTrackInHistoryList(track)
//            clickListener.onMovieClick(track)
//        }
            val intent = Intent(it.context, AudioPlayerActivity::class.java)
            intent.putExtra(TRACK, track)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = tracksList.size

//    fun interface MovieClickListener {
//        fun onMovieClick(track: Track)
//    }
}