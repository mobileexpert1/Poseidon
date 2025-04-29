package com.poseidonapp.inspector.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FormDataListBinding
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder

class FormDataListAdapter(
    var items: MutableList<String>,
    private var listener: ClickListeners,
    var dbHelper: DatabaseHelper,
    var id:String,
    var question:String

) : BaseAdapter() {

    companion object {
        var formDataListAdapter: FormDataListAdapter? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<FormDataListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.form_data_list,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = items.count()

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        formDataListAdapter = this
        val binding = holder.binding as FormDataListBinding
        var data = items.get(position)
        binding.tvData.text = data
        binding.tvData.setOnClickListener {
            listener.onclick(position, formDataListAdapter!!)
        }
    }

    fun addItem(item: String) {
        items.add(item)
        notifyItemInserted(items.size - 1)
        val result = items.joinToString(separator = ",")
        dbHelper.updateQuestionAnswer(id,question,result)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.count());
        val result = items.joinToString(separator = ",")
        dbHelper.updateQuestionAnswer(id,question,result)
    }

    interface ClickListeners {
        fun onclick(position: Int, formDataListAdapter: FormDataListAdapter)
    }


}