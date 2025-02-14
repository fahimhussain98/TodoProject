package com.example.projectnew.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectnew.model.User
import com.example.projectnew.databinding.ItemUserBinding

class UserAdapter(
    private val onUpdateClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit,
    private val onCompletedChanged: (User, Boolean) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var users = emptyList<User>()

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.binding.apply {
            tvTitle.text = currentUser.Title
            tvDescription.text = currentUser.Description
            checkBox.isChecked = currentUser.isCompleted


            update.setOnClickListener {
                onUpdateClick(currentUser)
            }

            delete.setOnClickListener {
                onDeleteClick(currentUser)
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCompletedChanged(currentUser, isChecked)
            }
        }
    }

    override fun getItemCount() = users.size

    fun setData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}