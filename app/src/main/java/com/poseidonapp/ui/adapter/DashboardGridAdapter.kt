package com.poseidonapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.GridItemBinding
import com.poseidonapp.model.addexpense.GridItem
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.utils.Const

class DashboardGridAdapter(private var baseActivity: BaseActivity,
//                           private val list: MutableLiveData<ArrayList<ServicesData>>?,
//                           private val list: ArrayList<String>
                           val items: List<GridItem>
) : BaseAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<GridItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.grid_item,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as GridItemBinding
        val data = items[position]
        binding.ivImage.setImageResource(data.image)
        binding.tvText.text=data.text

        binding.root.setOnClickListener {
            onItemClick(
                position,
                Const.Item_Click
            )
        }
    }

}