package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ItemAddressBinding
import com.poseidonapp.databinding.ItemSystemmanagementBinding
import com.poseidonapp.model.address.Addres
import com.poseidonapp.model.systemManagement.SystemLocation
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class SystemManagmentAdapter (val mList: List<SystemLocation>,
                              var callback: (Int,SystemLocation) -> Unit)
    : BaseAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemSystemmanagementBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_systemmanagement,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemSystemmanagementBinding
        val data = mList[position]
        binding.tvSystemName.text=data.sectionName
        binding.tvaddSystem.text=data.systemId
        binding.tvsystemLoction.text=data.systemDescription

        binding.root.setOnClickListener {
            callback.invoke(position,data)
        }

    }
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int, report: String)
    }



}