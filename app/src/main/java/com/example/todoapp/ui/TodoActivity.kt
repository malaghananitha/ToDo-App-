package com.example.todoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityTodoBinding
import com.example.todoapp.viewmodel.TodoViewModel
import com.example.todoapp.adapter.TodoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private val todoViewModel: TodoViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo)
        binding.lifecycleOwner = this

        setupToolbar()
        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        getAllTodo()
    }

    private fun getAllTodo() {
        todoViewModel.fetchAllTodos(this)
    }

    private fun setupToolbar() {
        with(binding.inclTodoAppToolbar) {
            toolbarTitle.text = getString(R.string.todo_title)
            calendarIcon.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onEditClick = { todo ->
                val intent = Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("TODO", todo)

                }
                startActivity(intent)

            },
            onDeleteClick = { position ->
                lifecycleScope.launch(Dispatchers.IO) {
                    todoViewModel.delete(position)
                    withContext(Dispatchers.Main) { todoAdapter.notifyItemRemoved(position) }
                }

            },
            onCompleteClick = { todo -> /* Handle complete click */ }
        )
        binding.recyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(this@TodoActivity)
        }
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            addItem()
        }

    }

    private fun setupBottomNavigation() {
        with(binding.bottomNavigationTodo) {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_all -> {
                        // Show all the tasks
                        true
                    }

                    R.id.navigation_completed -> {
                        // Start CompletedTaskActivity
                        val intent = Intent(this@TodoActivity, CompletedTasksActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    private fun observeViewModel() {
        todoViewModel.allTodos.observe(this, Observer { todos ->
            todos?.let {
                todoAdapter.submitList(it)
            }
        })
    }

    private fun addItem() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivity(intent)
    }
}