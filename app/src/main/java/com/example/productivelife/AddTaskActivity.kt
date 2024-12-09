package com.example.productivelife

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val buttonSaveTask = findViewById<Button>(R.id.buttonSaveTask)

        buttonSaveTask.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                // Create the task and pass it back to TaskListActivity
                val resultIntent = Intent()
                resultIntent.putExtra("taskTitle", title)
                resultIntent.putExtra("taskDescription", description)
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Close the activity and return to TaskListActivity
            }
        }
    }
}
