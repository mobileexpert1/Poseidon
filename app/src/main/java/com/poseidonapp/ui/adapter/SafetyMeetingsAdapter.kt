package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ItemSafetyMeetingsBinding
import com.poseidonapp.model.safetymeetings.SafetyItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.utils.Const

class SafetyMeetingsAdapter(private val mList: List<SafetyItem>,
                            private var listener: ClickListeners
) : BaseAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemSafetyMeetingsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_safety_meetings,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemSafetyMeetingsBinding
        val data = mList[position]
        binding.tvHead.text=data.name
        binding.tvDescription.setText(mList.get(position).createdAt)


        var attachment=Const.IMG_URL+data.attachment
        var signature=data.signature

        binding.root.setOnClickListener {
            listener.onclick(position,data.id,attachment,signature)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int = mList.size

    interface ClickListeners {

        fun onclick(position: Int,id:String,attachment:String,signature:String)

    }

}