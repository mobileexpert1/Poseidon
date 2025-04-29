package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ItemAddressBinding
import com.poseidonapp.model.address.Addres
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class AddressesAdapter  (val mList: List<Addres>,
                         var callback: (Int,String) -> Unit)
    : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemAddressBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_address,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemAddressBinding
        val data = mList[position]
        binding.tvAddressLine.text=data.addressLine
        binding.tvCity.text=data.city
        binding.tvState.text=data.state
        binding.tvPostalCode.text=data.postalCode
        binding.root.setOnClickListener {
            callback.invoke(position,data.id)
        }

    }
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int, report: String)
    }
}