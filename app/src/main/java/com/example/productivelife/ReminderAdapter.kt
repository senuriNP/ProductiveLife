package com.example.productivelife


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productivelife.models.Reminder

class ReminderAdapter(
    private var reminders: List<Reminder>,
    private val onDelete: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.reminder_title)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.title.text = reminder.title
        holder.deleteButton.setOnClickListener {
            onDelete(reminder)
        }
    }

    override fun getItemCount(): Int = reminders.size

    fun updateData(newReminders: List<Reminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }
}
