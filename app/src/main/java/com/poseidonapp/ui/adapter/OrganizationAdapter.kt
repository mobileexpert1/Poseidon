package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ItemOrganizationBinding
import com.poseidonapp.model.organization.Organization
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.ui.fragments.AddOrganizationFragment
import com.poseidonapp.ui.fragments.InstallationFragment
import java.util.ArrayList

class OrganizationAdapter(val mList: List<Organization>,
                          var callback: (Int,String,String) -> Unit
)
    : BaseAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemOrganizationBinding>(LayoutInflater.from(parent.context), R.layout.item_organization, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemOrganizationBinding
        val data = mList[position]
        binding.tvName.text = data.name
        binding.tvPhoneNumber.text = data.requestPhoneNumber
        binding.tvEmail.text = data.requestEmail
        binding.root.setOnClickListener {
//            listener.onclick(position,data.id)
            callback.invoke(position,data.name,data.id)
        }

    }
    // return the number of the items in the list
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int, report: String)

    }
}