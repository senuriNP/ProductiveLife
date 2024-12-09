package com.example.productivelife

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productivelife.models.Task

// Interface for click events, specifically for deleting tasks
interface OnTaskItemClickListener {
    fun onDeleteClick(task: Task)
}

class TaskAdapter(private var taskList: MutableList<Task>, private val listener: OnTaskItemClickListener) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder class for task item views
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val taskCompleted: CheckBox = itemView.findViewById(R.id.taskCompleted)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete) // Add a delete button to the item layout

        // Bind the task details to the ViewHolder
        fun bind(task: Task) {
            taskTitle.text = task.title
            taskDescription.text = task.description
            taskCompleted.isChecked = task.isCompleted

            // Handle delete button click
            deleteButton.setOnClickListener {
                listener.onDeleteClick(task) // Notify the listener to delete the task
            }

            // Handle checkbox change for task completion status
            taskCompleted.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                // Optionally, update the data source if needed
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Inflate the task item layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_task_adapter, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Bind data to the ViewHolder
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks.toMutableList()
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}

