
package com.streamzone.app

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class XtreamCredentials(
    val serverUrl: String,
    val username: String,
    val password: String
)

data class XtreamCategory(
    val id: String,
    val name: String
)

data class XtreamItem(
    val id: String,
    val name: String,
    val categoryId: String,
    val icon: String,
    val extension: String,
    val streamType: String
)

class XtreamApi(private val credentials: XtreamCredentials) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl: HttpUrl

    init {
        val address = credentials.serverUrl.trim().trimEnd('/')

        val normalized = if (
            address.startsWith("https://") ||
            address.startsWith("http://")
        ) address else "https://$address"

        baseUrl = requireNotNull(
            normalized.toHttpUrlOrNull()
        ) {
            "Invalid provider server URL"
        }
    }

    private fun buildApiUrl(action: String? = null): HttpUrl {
        val builder = baseUrl.newBuilder()
            .addPathSegment("player_api.php")
            .addQueryParameter("username", credentials.username)
            .addQueryParameter("password", credentials.password)

        if (action != null) {
            builder.addQueryParameter("action", action)
        }

        return builder.build()
    }

    private fun request(url: HttpUrl): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Provider server error: ${response.code}")
            }

            return response.body?.string()
                ?: throw Exception("Provider returned an empty response")
        }
    }

    fun authenticate(): JSONObject {
        val response = JSONObject(request(buildApiUrl()))

        val userInfo = response.optJSONObject("user_info")
            ?: throw Exception("Invalid provider response")

        val auth = userInfo.optString("auth", "0")

        if (auth != "1" && auth != "true") {
            throw Exception("Login rejected. Check your credentials.")
        }

        val status = userInfo.optString("status", "Active")

        if (!status.equals("Active", ignoreCase = true)) {
            throw Exception("Provider account status: $status")
        }

        return userInfo
    }

    private fun getArray(action: String): JSONArray {
        val response = request(buildApiUrl(action))

        return try {
            JSONArray(response)
        } catch (e: Exception) {
            throw Exception("Invalid response for $action")
        }
    }

    private fun readCategories(action: String): List<XtreamCategory> {
        val array = getArray(action)
        val results = mutableListOf<XtreamCategory>()

        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue

            results.add(
                XtreamCategory(
                    id = item.optString("category_id"),
                    name = item.optString("category_name", "Unknown")
                )
            )
        }

        return results
    }

    private fun readItems(
        action: String,
        streamType: String
    ): List<XtreamItem> {
        val array = getArray(action)
        val results = mutableListOf<XtreamItem>()

        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue

            results.add(
                XtreamItem(
                    id = item.optString("stream_id",
                        item.optString("series_id")),
                    name = item.optString("name", "Unknown"),
                    categoryId = item.optString("category_id"),
                    icon = item.optString("stream_icon"),
                    extension = item.optString("container_extension"),
                    streamType = streamType
                )
            )
        }

        return results
    }

    fun getLiveCategories(): List<XtreamCategory> =
        readCategories("get_live_categories")

    fun getLiveChannels(): List<XtreamItem> =
        readItems("get_live_streams", "live")

    fun getMovieCategories(): List<XtreamCategory> =
        readCategories("get_vod_categories")

    fun getMovies(): List<XtreamItem> =
        readItems("get_vod_streams", "movie")

    fun getSeriesCategories(): List<XtreamCategory> =
        readCategories("get_series_categories")

    fun getSeries(): List<XtreamItem> =
        readItems("get_series", "series")

    fun getSeriesInfo(seriesId: String): JSONObject {
        val url = baseUrl.newBuilder()
            .addPathSegment("player_api.php")
            .addQueryParameter("username", credentials.username)
            .addQueryParameter("password", credentials.password)
            .addQueryParameter("action", "get_series_info")
            .addQueryParameter("series_id", seriesId)
            .build()

        return JSONObject(request(url))
    }

    fun getMovieInfo(movieId: String): JSONObject {
        val url = baseUrl.newBuilder()
            .addPathSegment("player_api.php")
            .addQueryParameter("username", credentials.username)
            .addQueryParameter("password", credentials.password)
            .addQueryParameter("action", "get_vod_info")
            .addQueryParameter("vod_id", movieId)
            .build()

        return JSONObject(request(url))
    }

    fun buildLiveStreamUrl(streamId: String): String =
        buildStreamUrl("live", streamId, "")

    fun buildMovieStreamUrl(
        streamId: String,
        extension: String
    ): String =
        buildStreamUrl("movie", streamId, extension)

    fun buildSeriesStreamUrl(
        streamId: String,
        extension: String
    ): String =
        buildStreamUrl("series", streamId, extension)

    private fun buildStreamUrl(
        type: String,
        streamId: String,
        extension: String
    ): String {
        val path = when (type) {
            "live" -> "live"
            "movie" -> "movie"
            else -> "series"
        }

        val suffix = if (extension.isBlank()) {
            ""
        } else {
            ".$extension"
        }

        return baseUrl.newBuilder()
            .addPathSegment(path)
            .addPathSegment(credentials.username)
            .addPathSegment(credentials.password)
            .addPathSegment("$streamId$suffix")
            .build()
            .toString()
    }
}
