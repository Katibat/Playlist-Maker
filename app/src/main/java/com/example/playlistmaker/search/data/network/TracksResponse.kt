package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.player.data.dto.Response
import com.example.playlistmaker.search.domain.models.Track

class TracksResponse(val results: List<Track>) : Response()