package com.streamzone.app

object StreamSession {
    var credentials: XtreamCredentials? = null
    var api: XtreamApi? = null

    fun startSession(
        serverUrl: String,
        username: String,
        password: String
    ) {
        val account = XtreamCredentials(
            serverUrl = serverUrl,
            username = username,
            password = password
        )

        credentials = account
        api = XtreamApi(account)
    }

    fun clearSession() {
        credentials = null
        api = null
    }

    fun isConnected(): Boolean {
        return api != null
    }
}
