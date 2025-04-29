package com.poseidonapp.inspector.adapter

import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ItemLayoutBinding
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class ArrayAdapterRecyclerViewAdapter(
    private val arrayAdapter: ArrayAdapter<String>,
    private val onItemClicked: (String) -> Unit
): BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ItemLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_layout,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = arrayAdapter.count

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemLayoutBinding
        val item = arrayAdapter.getItem(position)
        binding.tvText.text = item
        holder.itemView.setOnClickListener {
            onItemClicked(item!!)
        }

    }


}
/*
class ArrayAdapterRecyclerViewAdapter(
    private val arrayAdapter: ArrayAdapter<String>,
    private val onItemClick: (String) -> Unit
) :
    RecyclerView.Adapter<ArrayAdapterRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayAdapter.getItem(position)
        holder.textView.text = item
        holder.itemView.setOnClickListener {
            onItemClick(item!!)
        }
    }

    override fun getItemCount(): Int {
        return arrayAdapter.count
    }
}
*/
