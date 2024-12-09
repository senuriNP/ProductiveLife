package com.example.productivelife

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.productivelife.models.Reminder
import com.example.productivelife.models.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class ReminderListActivity : AppCompatActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var recyclerView: RecyclerView

    private val reminders = mutableListOf<Reminder>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        recyclerView = findViewById(R.id.recycler_view) // Replace with your RecyclerView ID
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize your adapter
        reminderAdapter = ReminderAdapter(reminders) { reminder ->
            deleteReminder(reminder)
        }
        recyclerView.adapter = reminderAdapter

        // Load reminders from SharedPreferences
        loadReminders()

        reminderAdapter = ReminderAdapter(reminders) { reminder ->
            deleteReminder(reminder)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = reminderAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.add_reminder_button).setOnClickListener {
            showAddReminderDialog() // Example: reminder in 1 minute
        }
    }


    private fun showAddReminderDialog() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_reminder, null)

        val editTextReminderName = dialogView.findViewById<EditText>(R.id.editTextReminderName)
        val editTextReminderTime = dialogView.findViewById<EditText>(R.id.editTextReminderTime)

        // Create the dialog
        AlertDialog.Builder(this)
            .setTitle("Add Reminder")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add") { _, _ ->
                val title = editTextReminderName.text.toString()
                val timeInput = editTextReminderTime.text.toString()
                val timeInMillis = timeInput.toLongOrNull() ?: return@setPositiveButton // Handle invalid input

                if (title.isNotBlank()) {
                    addReminder(title, System.currentTimeMillis() + timeInMillis)
                }
            }
            .show()
    }

    private fun loadReminders() {
        val sharedPreferences = getSharedPreferences("ProductiveLife", MODE_PRIVATE)
        reminders.clear()
        nextId = sharedPreferences.getInt("nextId", 0)

        for (i in 0 until nextId) {
            val title = sharedPreferences.getString("reminder_$i", null) ?: continue
            val time = sharedPreferences.getLong("time_$i", 0)
            reminders.add(Reminder(i, title, time))
        }

        reminderAdapter.updateData(reminders)
    }

    private fun addReminder(title: String, time: Long) {
        val reminder = Reminder(nextId++, title, time)
        reminders.add(reminder)
        reminderAdapter.updateData(reminders)
        saveReminders()
        scheduleNotification(reminder)


    }

    private fun deleteReminder(reminder: Reminder) {
        reminders.remove(reminder)
        reminderAdapter.updateData(reminders)
        saveReminders()
    }

    private fun saveReminders() {
        val sharedPreferences = getSharedPreferences("ProductiveLife", MODE_PRIVATE).edit()
        sharedPreferences.putInt("nextId", nextId)

        for (i in reminders.indices) {
            sharedPreferences.putString("reminder_${reminders[i].id}", reminders[i].title)
            sharedPreferences.putLong("time_${reminders[i].id}", reminders[i].time)
        }
        sharedPreferences.apply()
    }

    private fun scheduleNotification(reminder: Reminder) {
        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(reminder.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("reminder_title" to reminder.title))
            .build()

        WorkManager.getInstance(this).enqueue(notificationWork)
    }

}

