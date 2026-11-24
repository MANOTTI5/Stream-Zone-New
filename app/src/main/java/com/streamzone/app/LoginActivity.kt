package com.streamzone.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {

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
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Login details entered. Provider connection comes next.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
