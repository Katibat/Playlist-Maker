package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackHolder>() {
    class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trackName: TextView = itemView.findViewById(R.id.track_name)
        var trackArtist: TextView = itemView.findViewById(R.id.track_artist)
        var trackTime: TextView = itemView.findViewById(R.id.track_time)
        var trackImage: ImageView = itemView.findViewById(R.id.track_image)

        fun bind(track: Track) {
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(2))
                .into(trackImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.trackName.text = trackList[position].trackName
        holder.trackArtist.text = trackList[position].artistName
        holder.trackTime.text = trackList[position].trackTime
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}