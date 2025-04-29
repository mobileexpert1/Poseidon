package com.poseidonapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.TimesheetListitemBinding
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class TimeSheetAdapter( var context: Context,
var itemList: Array<String>,
private var listener: ClickListeners,
)  : BaseAdapter() {
    var selectedPosition = 0
    var lastSelectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<TimesheetListitemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.timesheet_listitem,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as TimesheetListitemBinding
        val data = itemList[position]
        binding.tvDay.text = data




        binding.tvDay.setOnClickListener{
            listener.onclick(position)
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)
        }

        if (selectedPosition == position) {
            binding.tvDay.setTextColor(Color.WHITE)
            binding.tvDay.setBackgroundResource(R.drawable.timesheet_blue_rect)
        } else {
            binding.tvDay.setTextColor(Color.BLACK)
            binding.tvDay.setBackgroundResource(R.color.rect_shape_grey_two)
        }

    }


    interface ClickListeners {
        fun onclick(position: Int)
    }

}