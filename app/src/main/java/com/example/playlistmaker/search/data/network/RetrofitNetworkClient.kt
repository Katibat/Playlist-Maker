package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.player.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val tracksApi: TracksApi,
                            private val context: Context) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is TracksRequest) {
            return Response().apply { resultCode = BAD_REQUEST_HTTP_CODE }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = tracksApi.search(dto.expression)
                response.apply { resultCode = OK_HTTP_CODE }
            } catch (e: Throwable) {
                Response().apply { resultCode = SERVER_ERROR_HTTP_CODE }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        const val IMDB_BASE_URL = "https://itunes.apple.com"
        const val OK_HTTP_CODE = 200
        const val BAD_REQUEST_HTTP_CODE = 400
        const val SERVER_ERROR_HTTP_CODE = 500
    }
}