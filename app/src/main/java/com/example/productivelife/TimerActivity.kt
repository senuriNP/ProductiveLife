package com.example.productivelife

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import java.util.Locale

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button

    // Timer variables
    private var startTime = 0L
    private var elapsedTime = 0L
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // Initialize UI components
        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        // Set button click listeners
        startButton.setOnClickListener { startTimer() }
        pauseButton.setOnClickListener { pauseTimer() }
        resetButton.setOnClickListener { resetTimer() }

        // Initially, pause and reset buttons should be disabled
        pauseButton.isEnabled = false
        resetButton.isEnabled = false
    }

    private fun startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            handler.post(timerRunnable)
            isRunning = true

            // Enable pause button and disable start button
            pauseButton.isEnabled = true
            startButton.isEnabled = false
            resetButton.isEnabled = false
        }
    }

    private fun pauseTimer() {
        if (isRunning) {
            handler.removeCallbacks(timerRunnable)
            elapsedTime = System.currentTimeMillis() - startTime
            isRunning = false

            // Enable start and reset buttons
            startButton.isEnabled = true
            resetButton.isEnabled = true
            pauseButton.isEnabled = false
        }
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        isRunning = false
        elapsedTime = 0L

        // Reset the timer display
        timerTextView.text = getFormattedTime(0)

        // Enable start button and disable pause and reset buttons
        startButton.isEnabled = true
        pauseButton.isEnabled = false
        resetButton.isEnabled = false
    }

    // Runnable to update the timer display
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val runningTime = now - startTime
            timerTextView.text = getFormattedTime(runningTime)
            handler.postDelayed(this, 1000)
        }
    }

    // Format time in hours, minutes, and seconds
    private fun getFormattedTime(timeInMillis: Long): String {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60
        val hours = (timeInMillis / (1000 * 60 * 60)) % 24
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}