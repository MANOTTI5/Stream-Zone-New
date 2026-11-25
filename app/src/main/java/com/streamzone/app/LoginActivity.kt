package com.streamzone.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.concurrent.Executors

class LoginActivity : Activity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val serverUrl = findViewById<EditText>(R.id.serverUrl)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val connectButton = findViewById<Button>(R.id.connectButton)

        connectButton.setOnClickListener {
            val server = serverUrl.text.toString().trim()
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

            connectButton.isEnabled = false
            connectButton.text = "Connecting..."

            executor.execute {
                try {
                    val account = XtreamCredentials(
                        serverUrl = server,
                        username = user,
                        password = pass
                    )

                    val api = XtreamApi(account)
                    api.authenticate()

                    runOnUiThread {
                        StreamSession.startSession(server, user, pass)

                        Toast.makeText(
                            this,
                            "Provider connection successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(this, ContentActivity::class.java)
                        )

                        finish()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        connectButton.isEnabled = true
                        connectButton.text = "CONNECT"

                        Toast.makeText(
                            this,
                            e.message ?: "Provider connection failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}
