package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.RequestListingItemBinding
import com.poseidonapp.model.installationAssigned.AssignedRequestItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.ui.fragments.InstallationFragment

class InstallationAdapter(private val installationFragment: InstallationFragment,
                          private val mList: List<AssignedRequestItem>,
                          private var listener: ClickListeners,
) : BaseAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<RequestListingItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.request_listing_item,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as RequestListingItemBinding
        val data = mList[position]
        binding.tvName.text=data.propertyName
        binding.tvDate.text=data.date
        binding.tvLocation.text=data.address
        binding.tvHead.visibility= View.GONE

        binding.root.setOnClickListener {
            listener.onclick(position,data.id)
        }

    }

    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int,id:String)
    }}