package com.example.playlistmaker.search.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.search.domain.models.Track

class TracksListDiffCallback(private val oldItem: List<Track>,
                             private val newItem: List<Track>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItem.size
    override fun getNewListSize() = newItem.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTrack = oldItem[oldItemPosition]
        val newTrack = newItem[oldItemPosition]
        return oldTrack.trackId == newTrack.trackId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTrack = oldItem[oldItemPosition]
        val newTrack = newItem[oldItemPosition]
        return oldTrack == newTrack
    }
}