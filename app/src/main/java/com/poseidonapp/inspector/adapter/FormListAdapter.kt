package com.poseidonapp.inspector.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.FormListBinding
import com.poseidonapp.model.inspectionHeadsQuestions.DataItem
import com.poseidonapp.model.questions.Data
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.utils.Const

class FormListAdapter(private var baseActivity: BaseActivity,
//                      var listener:ClickListener,
//                      val items: List<DataItem>
                      val items: List<Data>
) : BaseAdapter() {

    private val sectionStatusMap = mutableMapOf<String, Boolean>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<FormListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.form_list,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    // Function to check if all fields in a section are filled
    // Function to check if all fields in a section are filled
    private fun isSectionFilled(section: Data): Boolean {
        val fields = section.questionJson
        return fields.all { !it.answer.equals("")}
//        return fields.all { it.answer.isNotBlank()|| it.answer.isNotEmpty()}

//        isNotEmpty
    }

    // Function to get the status of a specific section by section ID
    fun isSectionFilledById(sectionId: String): Boolean {
        return sectionStatusMap[sectionId] ?: false
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as FormListBinding
        val data = items[position]
        binding.tvText.text=data.sectionName
        binding.tvDescription.text=data.systemLocationText

        // ImageView for the tick mark
        val tickImageView = binding.ivTick // Assuming you have added this ImageView
        // Check if all fields in this section are filled
        /* val isSectionFilled = isSectionFilled(data)

         if (isSectionFilled) {
             // Display the tick mark
             tickImageView.visibility = View.VISIBLE
         } else {
             // Hide the tick mark
             tickImageView.visibility = View.GONE
         }*/

        // Check if all fields in this section are filled
        val isSectionFilled = isSectionFilled(data)

        // Update the section status map
        sectionStatusMap[data.id.toString()] = isSectionFilled

//        if (isSectionFilled) {
//            // Display the tick mark
//            tickImageView.visibility = View.VISIBLE
//        } else {
//            // Hide the tick mark
//            tickImageView.visibility = View.GONE
//        }

//        if (isSectionFilled) {
//            // Display the tick mark
//            tickImageView.visibility = View.VISIBLE
//        } else {
//            // Hide the tick mark
//            tickImageView.visibility = View.GONE
//        }

        binding.root.setOnClickListener {
//           listener.onClick(position)
            onItemClick(
                position,
                Const.Item_Click
            )
        }
    }

    interface ClickListener{
        fun onClick(position:Int)
    }

}