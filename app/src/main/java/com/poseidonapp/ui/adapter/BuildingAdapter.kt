package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ItemBuildingBinding
import com.poseidonapp.model.building.Building
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.ui.fragments.BuildingFragment

class BuildingAdapter(val mList: List<Building>,
                      var callback: (Int,Building) -> Unit)
    : BaseAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemBuildingBinding>(LayoutInflater.from(parent.context), R.layout.item_building, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemBuildingBinding
        val data = mList[position]
        binding.tvBuildingName.text = data.name
        binding.root.setOnClickListener {
            callback.invoke(position,data)
        }

    }
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int, report: String)

    }
}