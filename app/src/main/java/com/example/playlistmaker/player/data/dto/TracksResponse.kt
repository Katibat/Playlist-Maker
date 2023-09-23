package com.example.playlistmaker.player.data.dto

import com.example.playlistmaker.player.domain.models.Track

class TracksResponse(val results: List<Track>) : Response()