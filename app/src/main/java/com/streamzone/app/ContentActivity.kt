package com.streamzone.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.view.Gravity
import android.graphics.Color

class ContentActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!StreamSession.isConnected()) {
            finish()
            return
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(8, 8, 8))
        }

        val title = TextView(this).apply {
            text = "STREAM ZONE"
            textSize = 28f
            setTextColor(Color.rgb(229, 9, 20))
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 24)
        }

        val status = TextView(this).apply {
            text = "Provider connected"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        layout.addView(title)
        layout.addView(status)

        val sections = listOf(
            "Live TV",
            "24/7 Channels",
            "Movies",
            "TV Series",
            "My Favorites"
        )

        for (section in sections) {
            val item = TextView(this).apply {
                text = section
                textSize = 20f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(20, 18, 20, 18)
                setBackgroundColor(Color.rgb(35, 35, 35))
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 8, 0, 8)
            layout.addView(item, params)
        }

        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
    }
}
