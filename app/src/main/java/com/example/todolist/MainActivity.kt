package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewTasks: RecyclerView
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter
    private var editedTaskPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)

        adapter = TaskAdapter(tasks)
        recyclerViewTasks.adapter = adapter
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        buttonAdd.setOnClickListener {
            addOrUpdateTask()
        }

        editTextTask.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                addOrUpdateTask()
                true
            } else {
                false
            }
        }
    }

    private fun addOrUpdateTask() {
        val taskName = editTextTask.text.toString().trim()
        editTextTask.error = null
        if (taskName.isEmpty()) {
            editTextTask.error = "Task cannot be empty!"
            return
        }
        if (tasks.any { it.name.equals(taskName, ignoreCase = true) }) {
            editTextTask.error = "Task already exists!"
            return
        }
        val task = Task(taskName)
        if (editedTaskPosition != null) {
            tasks[editedTaskPosition!!] = task
            editedTaskPosition = null
            buttonAdd.text = "Add Task"
        } else {
            // Add new task
            tasks.add(task)
        }
        adapter.notifyDataSetChanged()
        editTextTask.text.clear()
    }

    private fun editTask(position: Int) {
        editTextTask.setText(tasks[position].name)
        editedTaskPosition = position
        buttonAdd.text = "Update Task"
    }

    private fun deleteTask(position: Int) {
        val taskName = tasks[position].name

        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                tasks.removeAt(position)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    inner class TaskAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTask: TextView = itemView.findViewById(R.id.textViewTask)
            val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
            val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

            init {
                buttonEdit.setOnClickListener {
                    editTask(adapterPosition)
                }

                buttonDelete.setOnClickListener {
                    deleteTask(adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.textViewTask.text = task.name
        }

        override fun getItemCount(): Int {
            return tasks.size
        }
    }
}

data class Task(val name: String)
