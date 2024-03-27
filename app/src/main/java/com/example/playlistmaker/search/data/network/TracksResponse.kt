package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.player.data.dto.Response
import com.example.playlistmaker.player.domain.models.Track

class TracksResponse(val results: List<Track>) : Response()