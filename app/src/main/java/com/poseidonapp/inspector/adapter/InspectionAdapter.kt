package com.poseidonapp.inspector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.poseidonapp.R
import com.poseidonapp.databinding.RequestListingItemBinding
import com.poseidonapp.inspector.home.InspectorHomeFragment
import com.poseidonapp.model.inspection.Request
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.utils.Const


class InspectionAdapter(private val inspectorHomeFragment: InspectorHomeFragment,
                        private val mList: List<Request>,
                        private var listener: ClickListeners,
) : BaseAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.request_listing_item, parent, false)
//        return ViewHolder(view)
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

//        binding.tvHead.setText(mList.get(position).inspectionType)
        binding.tvHead.text=data.inspectionType
//        binding.tvName.setText(mList.get(position).fullName)
        binding.tvName.text=data.fullName
//        binding.tvDate.setText(mList.get(position).assignedDate+" "+mList.get(position).assignedTime)
        binding.tvDate.text=data.assignedDate+" "+data.assignedTime
//        binding.tvLocation.setText(mList.get(position).addressLine1+", "+mList.get(position).city+", "+mList.get(position).state)
        binding.tvLocation.text=data.addressLine1+", "+data.city+", "+data.state

       var location=data.addressLine1+", "+data.city+", "+data.state
       var image=Const.IMG_URL+data.profileImage
        Glide
            .with(inspectorHomeFragment)
            .load(Const.IMG_URL+mList.get(position).profileImage)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivUser);

        binding.root.setOnClickListener {
           listener.onclick(position,data.inspectorInspectionStatus,data.inspectionType,data.fullName,data.mobileNumber,location,image,data.id)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int,inspectorInspectionStatus:String,inspectionType:String,userName:String,mobileNumber:String,location:String,image:String,id:String)

    }

}