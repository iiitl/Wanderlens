package com.example.wanderlens.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.databinding.ItemJournalCardBinding

class JournalAdapter(private val onItemClick: (JournalEntry) -> Unit) : 
    ListAdapter<JournalEntry, JournalAdapter.JournalViewHolder>(JournalDiffCallback()) {

    inner class JournalViewHolder(val binding: ItemJournalCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: JournalEntry) {
            binding.tvLocation.text = entry.location
            binding.tvDate.text = "${entry.country} • ${entry.dateText}"
            
            Glide.with(binding.ivJournalPhoto.context)
                .load(entry.imageUrl)
                .centerCrop()
                .into(binding.ivJournalPhoto)

            binding.root.setOnClickListener {
                onItemClick(entry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val binding = ItemJournalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JournalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class JournalDiffCallback : DiffUtil.ItemCallback<JournalEntry>() {
    override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
        return oldItem == newItem
    }
}
