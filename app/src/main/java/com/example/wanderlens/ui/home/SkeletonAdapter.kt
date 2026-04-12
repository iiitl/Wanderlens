package com.example.wanderlens.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.example.wanderlens.databinding.ItemSkeletonCardBinding

data class SkeletonItem(val id: Int = 0)

class SkeletonAdapter :
    ListAdapter<SkeletonItem, SkeletonAdapter.SkeletonViewHolder>(SkeletonDiffCallback()) {

    inner class SkeletonViewHolder(val binding: ItemSkeletonCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SkeletonItem) {
            binding.shimLay.startShimmer()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        val binding = ItemSkeletonCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SkeletonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SkeletonDiffCallback : DiffUtil.ItemCallback<SkeletonItem>() {
    override fun areItemsTheSame(oldItem: SkeletonItem, newItem: SkeletonItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SkeletonItem, newItem: SkeletonItem): Boolean =
        oldItem == newItem
}