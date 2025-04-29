package com.poseidonapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ScheduledItemsBinding
import com.poseidonapp.model.schedules.MeetingsItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class ScheduledEventsAdapter (val context: Context, val meetings: List<MeetingsItem>, var listener: ClickListeners,
)  : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ScheduledItemsBinding>(LayoutInflater.from(parent.context), R.layout.scheduled_items, parent, false)
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = meetings.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ScheduledItemsBinding
        val data = meetings[position]
        binding.titleTV.text = data.eventType
        binding.messagetTV.text = data.note
        binding.tvDate.text = data.meetingDate.plus(" ").plus(data.meetingTime)

        if (meetings.get(position).attachment.isEmpty()){
            binding.viewFileTV.visibility=View.GONE
        }else{
            binding.viewFileTV.visibility=View.VISIBLE
        }



        binding.viewFileTV.setOnClickListener {
            listener.onclick(position,meetings.get(position).attachment)
        }
    }

    interface ClickListeners {
        fun onclick(position: Int, attachment: String)
    }
}