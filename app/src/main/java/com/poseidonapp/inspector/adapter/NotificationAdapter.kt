package com.poseidonapp.inspector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.InspectionNotificationListBinding
import com.poseidonapp.model.inspectionnotification.RequestsItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class NotificationAdapter (private val mList: List<RequestsItem>) : BaseAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val binding = DataBindingUtil.inflate<InspectionNotificationListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.inspection_notification_list,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as InspectionNotificationListBinding
        val data = mList[position]
        binding.tvHead.text=data.title
        binding.tvDescription.setText(mList.get(position).description)

    }

    // return the number of the items in the list
    override fun getItemCount(): Int = mList.size



}