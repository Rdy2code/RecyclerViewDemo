package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter: RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() {

    var data = listOf<SleepNight>()
        set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class ViewHolder private constructor (itemView: View): RecyclerView.ViewHolder(itemView) {
        //Get a reference to the views in the list item layout
        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)
        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        val quality: TextView = itemView.findViewById(R.id.quality_string)

        fun bind(item: SleepNight) {
            val res = itemView.context.resources
            sleepLength.text = convertDurationToFormatted(
                    item.startTimeMilli,
                    item.endTimeMilli,
                    res)
            quality.text = convertNumericQualityToString(item.sleepQuality, res)
            qualityImage.setImageResource(
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
                qualityImage.setBackgroundColor(Color.RED)
            } else {
                qualityImage.setBackgroundColor(Color.BLACK)
            }
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //Get a reference to a LayoutInflater
                val layoutInflater = LayoutInflater.from(parent.context)
                //Inflate the List Item layout
                val view = layoutInflater.inflate(R.layout.list_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}