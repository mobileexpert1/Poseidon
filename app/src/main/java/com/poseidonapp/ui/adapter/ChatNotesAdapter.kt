package com.poseidonapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.SubjectstodiscussItemsBinding
import com.poseidonapp.model.chatNotes.NotesListItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.ui.fragments.SubjectToDiscussFragment


class ChatNotesAdapter(
    val context: Context,
    var listener: SubjectToDiscussFragment,
    var notesList: ArrayList<NotesListItem>,
) : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<SubjectstodiscussItemsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.subjectstodiscuss_items,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = notesList!!.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as SubjectstodiscussItemsBinding
        binding.noteTV.setText(notesList.get(position).note)
        holder.itemView.setOnClickListener {
            listener.onclick(position,notesList.get(position).id)
        }
    }

    interface ClickListeners {
        fun onclick(position: Int, id: String)
    }
}