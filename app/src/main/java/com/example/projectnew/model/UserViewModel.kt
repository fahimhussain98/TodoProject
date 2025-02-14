package com.example.projectnew.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectnew.model.User
import com.example.projectnew.model.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    // Add this function to get all users as a Flow
    fun getAllUsers() = userDao.getAllUsers()

    fun addUser(user: User) = viewModelScope.launch {
        userDao.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        userDao.updateUser(user)
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        userDao.deleteUser(user)
    }
}