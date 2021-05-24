package com.example.maptimelineapp.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.maptimelineapp.R
import com.example.maptimelineapp.datasource.room.models.UserLocation
import java.time.format.DateTimeFormatter

class UserLocationAdapter (private val itemClickListener: OnItemClickListener): ListAdapter<UserLocation,
        UserLocationAdapter.UserLocationViewHolder>(UserLocationComparator()){

    interface OnItemClickListener {
        fun onItemClick(item: UserLocation?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLocationViewHolder {
        return UserLocationViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: UserLocationViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, itemClickListener)
    }

    class UserLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationName: TextView = itemView.findViewById(R.id.tvName)
        private val locationDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(locationData: UserLocation?, itemClickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(locationData)
            }
            locationName.text = locationData?.date?.format(DateTimeFormatter.ofPattern("dd LLL, yy HH:mm"))
            locationDate.text = locationData?.locationName
        }

        companion object {
            fun create(parent: ViewGroup): UserLocationViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item, parent, false)
                return UserLocationViewHolder(view)
            }
        }
    }

    class UserLocationComparator : DiffUtil.ItemCallback<UserLocation>() {
        override fun areItemsTheSame(oldItem: UserLocation, newItem: UserLocation): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UserLocation, newItem: UserLocation): Boolean {
            return oldItem.date == newItem.date
        }
    }
}