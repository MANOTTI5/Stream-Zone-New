
package com.streamzone.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoginActivity : Activity() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val serverUrl = findViewById<EditText>(R.id.serverUrl)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val connectButton = findViewById<Button>(R.id.connectButton)

        connectButton.setOnClickListener {
            val server = serverUrl.text.toString().trim().trimEnd('/')
            val user = username.text.toString().trim()
            val pass = password.text.toString()

            if (server.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val address = if (
                server.startsWith("http://") ||
                server.startsWith("https://")
            ) server else "https://$server"

            val baseUrl = address.toHttpUrlOrNull()

            if (baseUrl == null) {
                Toast.makeText(
                    this,
                    "Please enter a valid server URL",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val apiUrl = baseUrl.newBuilder()
                .addPathSegment("player_api.php")
                .addQueryParameter("username", user)
                .addQueryParameter("password", pass)
                .build()

            val request = Request.Builder()
                .url(apiUrl)
                .get()
                .build()

            connectButton.isEnabled = false
            connectButton.text = "Connecting..."

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        connectButton.isEnabled = true
                        connectButton.text = "Connect"

                        Toast.makeText(
                            this@LoginActivity,
                            "Connection failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    val result = response.use {
                        if (!it.isSuccessful) {
                            "Server error: ${it.code}"
                        } else {
                            val body = it.body?.string()

                            try {
                                val data = JSONObject(body ?: "")
                                val userInfo = data.optJSONObject("user_info")
                                val auth = userInfo?.optString("auth", "0")

                                if (auth == "1" || auth == "true") {
                                    "SUCCESS"
                                } else {
                                    "Login rejected. Check your provider details."
                                }
                            } catch (e: Exception) {
                                "Invalid response from provider."
                            }
                        }
                    }

                    runOnUiThread {
                        connectButton.isEnabled = true
                        connectButton.text = "Connect"

                        if (result == "SUCCESS") {
                            Toast.makeText(
                                this@LoginActivity,
                                "Provider connection successful!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                result,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        }
    }
}

