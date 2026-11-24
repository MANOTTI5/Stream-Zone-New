package com.streamzone.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButton(R.id.liveTvButton, "Live TV")
        setupButton(R.id.channelsButton, "24/7 Channels")
        setupButton(R.id.moviesButton, "Movies")
        setupButton(R.id.seriesButton, "TV Series")
        setupButton(R.id.favoritesButton, "My Favorites")
        setupButton(R.id.settingsButton, "Settings")
    }

    private fun setupButton(buttonId: Int, section: String) {
        findViewById<Button>(buttonId)?.setOnClickListener {
            if (section == "Settings") {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "$section section selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
