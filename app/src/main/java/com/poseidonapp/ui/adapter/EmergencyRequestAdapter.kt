package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.EmergencyRequestListItemBinding
import com.poseidonapp.model.emergencyrequests.EmergencyRequestItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class EmergencyRequestAdapter (private val mList: List<EmergencyRequestItem>,
                               private var listener: ClickListeners
) : BaseAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<EmergencyRequestListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.emergency_request_list_item,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as EmergencyRequestListItemBinding
        val data = mList[position]
        binding.tvHead.text=data.name
        binding.tvDescription.text=data.dateOfOrder

        binding.ivforward.setOnClickListener {
            listener.onclick(position,
                data.id,
                data.name,
                data.terms,
                data.orderTakenBy,
                data.orderNumber,
                data.dateOfOrder,
                data.phone,
                data.jobName,
                data.jobLocation,
                data.jobPhone,
                data.startingDate)
        }

        binding.root.setOnClickListener {
            listener.onclick(position,
                data.id,
                data.name,
                data.terms,
                data.orderTakenBy,
                data.orderNumber,
                data.dateOfOrder,
                data.phone,
                data.jobName,
                data.jobLocation,
                data.jobPhone,
                data.startingDate)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {

        fun onclick(position: Int,
                    id:String,
                    name:String,
                    terms:String,
                    orderTakenBy:String,
                    orderNumber:String,
                    dateOfOrder:String,
                    phone:String,
                    jobName:String,
                    jobLocation:String,
                    jobPhone:String,
                    startingDate:String
        )

    }

}