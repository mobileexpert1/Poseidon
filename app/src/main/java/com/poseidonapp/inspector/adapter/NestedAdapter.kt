package com.poseidonapp.inspector.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.NestedQuestionListBinding
import com.poseidonapp.inspector.formquestions.FormQuestionsFragment
import com.poseidonapp.model.questions.QuestionJson
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.base.BaseViewHolder

class NestedAdapter(
    var baseActivity: BaseActivity,
    var context: BaseFragment,
    var formQuestionsFragment: FormQuestionsFragment,
    private val mList: List<QuestionJson>,
    var dbHelper: DatabaseHelper,
    var id: String,
    var sectionName: String
) : BaseAdapter() {
    val width = 200
    val height = 200
    var items = mutableListOf<String>()
    var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<NestedQuestionListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.nested_question_list,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as NestedQuestionListBinding
        var data = mList[position]
        binding.apply {
//            tvNestedQuestion.text=""
        }
    }

//    override fun getItemCount(): Int = mList.size
    override fun getItemCount(): Int = 4

}