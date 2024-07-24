package com.example.todoapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityEditTaskBinding
import com.example.todoapp.entity.Todo
import com.example.todoapp.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private val todoViewModel: TodoViewModel by viewModels()
    lateinit var todo: Todo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_task)

        with(binding.inclEditTaskToolbar) {
            toolbarTitle.text = getString(R.string.edit_task)
            backpageButtonIcon.visibility = View.VISIBLE
            backpageButtonIcon.setOnClickListener {
                finish()
            }
        }

        // Retrieve the task details passed through the intent
        todo = intent.getSerializableExtra("TODO") as Todo


        binding.editTextField1.setText(todo.title  )
        binding.editTextField2.setText(todo.subtitle )

        binding.buttonUpdate.setOnClickListener {
            confirmUpdateTask()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun confirmUpdateTask() {
        val todoTitle = binding.editTextField1.text.toString()
        val todoSubTitle = binding.editTextField2.text.toString()

        if (todoTitle.isEmpty() || todoSubTitle.isEmpty()) {
            // Show an alert dialog for empty fields
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Input Error")
            builder.setMessage("Title and Detail cannot be empty")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        } else {
            // Show confirmation dialog before updating
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want to update this task?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                updateTask(todoTitle, todoSubTitle)
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun updateTask(todoTitle: String, todoSubTitle: String) {
                val updatedTodo = Todo(
                    id = (todo.id),
                    title = todoTitle,
                    subtitle = todoSubTitle,
                    isCompleted = todo.isCompleted, // Adjust this based on your requirements
                    timestamp = todo.timestamp // Adjust this based on your requirements
                )

                lifecycleScope.launch {
                    if (todoViewModel.update(updatedTodo) == 1) {
                        Toast.makeText(
                            this@EditTaskActivity,
                            "Task updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        finish() // Close the activity after updating the task
                    } else {
                        Toast.makeText(
                            this@EditTaskActivity,
                            "Task update failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }




    }

}