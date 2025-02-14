package com.example.projectnew.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectnew.databinding.ActivityMainBinding
import com.example.projectnew.databinding.DialogAddUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import com.example.projectnew.Adapter.UserAdapter
import com.example.projectnew.model.User
import com.example.projectnew.model.UserViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    private var originalUserList: List<User> = listOf()
    private var filteredUserList: List<User> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        loadUsers()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onUpdateClick = { user -> showUpdateUserDialog(user) },
            onDeleteClick = { user -> showDeleteConfirmationDialog(user) },
            onCompletedChanged = { user, isCompleted -> updateUserCompleted(user, isCompleted) }
        )

        binding.recyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddUser.setOnClickListener { showAddUserDialog() }
        binding.fabFilter.setOnClickListener { showFilterDialog() }
    }

    private fun showAddUserDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogAddUserBinding.inflate(layoutInflater)

        dialog.setView(dialogBinding.root)
            .setTitle("Add New User")
            .setPositiveButton("Add") { _, _ ->
                val title = dialogBinding.etTitle.text.toString()
                val description = dialogBinding.etDescription.text.toString()

                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val user = User(Title = title, Description = description)
                    userViewModel.addUser(user)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateUserDialog(user: User) {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogAddUserBinding.inflate(layoutInflater)

        dialogBinding.etTitle.setText(user.Title)
        dialogBinding.etDescription.setText(user.Description)

        dialog.setView(dialogBinding.root)
            .setTitle("Update User")
            .setPositiveButton("Update") { _, _ ->
                val title = dialogBinding.etTitle.text.toString()
                val description = dialogBinding.etDescription.text.toString()

                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val updatedUser = user.copy(Title = title, Description = description)
                    userViewModel.updateUser(updatedUser)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Yes") { _, _ -> userViewModel.deleteUser(user) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateUserCompleted(user: User, isCompleted: Boolean) {
        val updatedUser = user.copy(isCompleted = isCompleted)
        userViewModel.updateUser(updatedUser)
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            userViewModel.getAllUsers().collect { users ->
                originalUserList = users
                filteredUserList = users
                userAdapter.setData(filteredUserList)
            }
        }
    }

    // Search Functionality
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUserList(newText)
                return true
            }
        })
    }

    private fun filterUserList(query: String?) {
        filteredUserList = if (query.isNullOrEmpty()) {
            originalUserList
        } else {
            originalUserList.filter { it.Title.contains(query, ignoreCase = true) }
        }
        userAdapter.setData(filteredUserList)
    }

    // Filter Dialog to select additional filters
    private fun showFilterDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Filter")
        val options = arrayOf("Completed", "Not Completed")
        builder.setItems(options) { _, which ->
            applyFilter(options[which])
        }
        builder.show()
    }

    // Apply filter based on selected option
    private fun applyFilter(filter: String) {
        filteredUserList = when (filter) {
            "Completed" -> originalUserList.filter { it.isCompleted }
            "Not Completed" -> originalUserList.filter { !it.isCompleted }
            else -> originalUserList
        }
        userAdapter.setData(filteredUserList)
    }
}
