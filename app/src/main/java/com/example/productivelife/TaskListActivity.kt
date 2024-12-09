package com.example.productivelife

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productivelife.models.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskListActivity : AppCompatActivity() {
    //SharedPreferences and Editor objects
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>
    private lateinit var fabAddTask: FloatingActionButton // Floating action button to add tasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTasks)

        // Initialize the FloatingActionButton
        fabAddTask = findViewById(R.id.fabAddTask)

        // Load tasks from SharedPreferences
        taskList = getTasks() ?: mutableListOf() // If no tasks are found, initialize an empty list

        Log.d("TaskListActivity", "Task list: ${taskList.size} tasks loaded.")


        // Initialize the TaskAdapter with the task list and the listener for delete functionality
        taskAdapter = TaskAdapter(taskList, object : OnTaskItemClickListener {
            override fun onDeleteClick(task: Task) {
                // Remove the task from the list
                taskList.remove(task)
                // Save the updated task list
                saveTasks(taskList)
                // Notify the adapter of data change
                taskAdapter.notifyDataSetChanged()
            }
        })

        // Set the RecyclerView layout manager and adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Add Click Listener to the FloatingActionButton
        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 1) // Start AddTaskActivity with request code 1
        }

        // Save default tasks in SharedPreferences if none exist
        saveDefaultTasks()
    }

    // Override onActivityResult to handle the result from AddTaskActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Retrieve the task details from the returned intent
            val title = data?.getStringExtra("taskTitle")
            val description = data?.getStringExtra("taskDescription")

            // Add the new task to the list and update SharedPreferences
            if (title != null && description != null) {
                val newTask = Task(title, description, false)
                taskList.add(newTask)
                saveTasks(taskList)
                taskAdapter.updateTasks(taskList) // Update adapter

                Log.d("TaskListActivity", "onActivityResult: New task added. Total tasks: ${taskList.size}")
            }
        }
    }

    // Method to save tasks in SharedPreferences
    private fun saveTasks(tasks: List<Task>) {
        val tasksString = tasksToJsonString(tasks)
        editor.putString("taskList", tasksString)
        editor.apply()
        Log.d("TaskListActivity", "saveTasks: ${tasks.size} tasks saved to SharedPreferences")
    }

    // Method to save default tasks in SharedPreferences if none exist
    private fun saveDefaultTasks() {
        if (!sharedPreferences.contains("taskList")) {
            // Create a sample task list manually in a simple string format
            val defaultTasks = listOf(
                Task("Task 1", "Sample task 1", false),
                Task("Task 2", "Sample task 2", true)
            )
            val defaultTasksString = tasksToJsonString(defaultTasks)
            // Save the default task list to SharedPreferences
            editor.putString("taskList", defaultTasksString)
            editor.apply()
            Log.d("TaskListActivity", "saveDefaultTasks: Default tasks saved")
        }else {
            Log.d("TaskListActivity", "saveDefaultTasks: Task list already exists, skipping default task save")
        }
    }

    // Method to retrieve tasks from SharedPreferences
    private fun getTasks(): MutableList<Task>? {
        val jsonString = sharedPreferences.getString("taskList", null)
        if (jsonString != null) {
            val tasks = jsonStringToTasks(jsonString).toMutableList()
            Log.d("TaskListActivity", "getTasks: Retrieved ${tasks.size} tasks from SharedPreferences")
            return tasks
        }
        Log.d("TaskListActivity", "getTasks: No tasks found in SharedPreferences")
        return null
    }

    // Method to convert a list of Task objects to a JSON-like string
    private fun tasksToJsonString(tasks: List<Task>): String {
        return tasks.joinToString(separator = "|") { task ->
            "${task.title}~~${task.description}~~${task.isCompleted}"
        }
    }

    // Method to convert a JSON-like string to a list of Task objects
    private fun jsonStringToTasks(jsonString: String): List<Task> {
        return jsonString.split("|").map { taskString ->
            val parts = taskString.split("~~")
            Task(parts[0], parts[1], parts[2].toBoolean())
        }
    }
}

