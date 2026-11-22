
package com.streamzone.app

import android.app.Activity
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
        findViewById<Button>(buttonId).setOnClickListener {
            Toast.makeText(
                this,
                "$section coming soon!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
