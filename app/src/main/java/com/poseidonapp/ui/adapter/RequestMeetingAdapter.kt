package com.poseidonapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.RequestMeetingItemBinding
import com.poseidonapp.model.pendingrequest.MeetingsItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class RequestMeetingAdapter (val context: Context,
                             val meetingsItemList: List<MeetingsItem>,
                             var listener: ClickListeners,
)  : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<RequestMeetingItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.request_meeting_item,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = meetingsItemList.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as RequestMeetingItemBinding
        val data = meetingsItemList[position]
        binding.titleTV.text = data.eventType
        binding.messagetTV.text = data.note
        binding.tvDate.text = data.meetingDate.plus(" ").plus(data.meetingTime)

        binding.tvAccept.setOnClickListener {
            listener.onclick(position,true,data.id)
        }
        binding.tvReject.setOnClickListener {
            listener.onclick(position,false,data.id)
        }


    }

    interface ClickListeners {
        fun onclick(position: Int,isChecked:Boolean, meetingID:String)
    }


}