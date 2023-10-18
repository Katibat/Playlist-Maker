package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val tracksApi: TracksApi) : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(IMDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(TracksApi::class.java)

    override fun doRequest(query: String,
                           onSuccess: (List<Track>) -> Unit,
                           onError: (NetworkError) -> Unit) {
        imdbService.search(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: retrofit2.Response<TracksResponse>,
            ) {
                when (response.code()) {
                    OK_HTTP_CODE -> {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            onSuccess.invoke(response.body()?.results!!)
                        } else {
                            onError.invoke(NetworkError.NOTHING_FOUND)
                        }
                    }
                    else ->
                        onError.invoke(NetworkError.NO_CONNECTION)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                onError.invoke(NetworkError.NO_CONNECTION)
            }
        })
    }

    companion object {
        const val IMDB_BASE_URL = "https://itunes.apple.com"
        const val OK_HTTP_CODE = 200
    }
}