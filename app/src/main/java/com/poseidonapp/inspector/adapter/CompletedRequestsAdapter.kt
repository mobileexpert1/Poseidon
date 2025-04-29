package com.poseidonapp.inspector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.CompletedRequestItemBinding
import com.poseidonapp.inspector.completedrequest.CompletedRequestsFragment
import com.poseidonapp.model.completedrequest.CompletedRequestItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class CompletedRequestsAdapter (private val completedRequestsFragment: CompletedRequestsFragment,
                                private val mList: List<CompletedRequestItem>,
                                private var listener: ClickListeners,
) : BaseAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
       val binding = DataBindingUtil.inflate<CompletedRequestItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.completed_request_item,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as CompletedRequestItemBinding
        val data = mList[position]
        binding.tvHead.text=data.fullName
        binding.tvName.text=data.address

        binding.tvReport.setOnClickListener {
            listener.onclick(position,data.report)

        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {
        fun onclick(position: Int,report:String)

    }

}