package com.example.playlistmaker.player.data.dto

import com.example.playlistmaker.search.domain.models.Track

class TracksResponse(val results: List<Track>) : Response()