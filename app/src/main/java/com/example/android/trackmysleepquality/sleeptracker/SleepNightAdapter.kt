package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemBinding

//Change this class definition to extend ListAdapter. With ListAdapter we no longer need
//getItemCount, because ListAdapter calculates this for us.
class SleepNightAdapter: androidx.recyclerview.widget.ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * This class calculates what items in the list have changed, This class is part of RecyclerView.
     */
    class SleepNightDiffCallback: DiffUtil.ItemCallback<SleepNight>() {

        override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
            return oldItem.nightId == newItem.nightId
        }

        override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
            //We can write the statement this way to check for equality since SleepNight is
            //is a data class, which automatically defines equals. This tells DiffUtil if the
            //item has been updated.
            return oldItem == newItem
        }
    }

    class ViewHolder private constructor (
            val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SleepNight) {
            val res = binding.root.resources
            binding.sleepLength.text = convertDurationToFormatted(
                    item.startTimeMilli,
                    item.endTimeMilli,
                    res)
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
            binding.qualityImage.setImageResource(
                    when (item.sleepQuality) {
                        0 -> R.drawable.ic_sleep_0
                        1 -> R.drawable.ic_sleep_1
                        2 -> R.drawable.ic_sleep_2
                        3 -> R.drawable.ic_sleep_3
                        4 -> R.drawable.ic_sleep_4
                        5 -> R.drawable.ic_sleep_5
                        else -> R.drawable.ic_sleep_active
                    })
            if (item.sleepQuality <= 1) {
                binding.qualityImage.setBackgroundColor(Color.RED)
            } else {
                binding.qualityImage.setBackgroundColor(Color.BLACK)
            }
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //Get a reference to a LayoutInflater
                val layoutInflater = LayoutInflater.from(parent.context)
                //Inflate the List Item layout
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}