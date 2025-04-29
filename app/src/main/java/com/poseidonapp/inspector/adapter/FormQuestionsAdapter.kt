package com.poseidonapp.inspector.adapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.poseidonapp.FlowTestFragment
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FormQuestionsListBinding
import com.poseidonapp.inspector.formquestions.FormQuestionsFragment
import com.poseidonapp.model.inspectionHeadsQuestions.QuestionJsonItem
import com.poseidonapp.model.questions.QuestionJson
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.utils.Const
import java.io.ByteArrayOutputStream
import java.util.*


class FormQuestionsAdapter(
    var baseActivity: BaseActivity,
    var context: BaseFragment,
    var formQuestionsFragment: FormQuestionsFragment,
    private val mList: List<QuestionJson>,
//    private val mList: List<QuestionJsonItem>,
    var dbHelper: DatabaseHelper,
    var id:String,
    var sectionName:String
) : BaseAdapter(), FormDataListAdapter.ClickListeners {
    val width = 200
    val height = 200
    var items = mutableListOf<String>()
    var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    var imagePosition:Int=0

    var base64EncodedString = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<FormQuestionsListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.form_questions_list,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }




    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as FormQuestionsListBinding
        val data = mList[position]

        if (data.type == "Boolean") {
            binding.llRadioButtons.visibility = View.VISIBLE
            val radioButtons = listOf(binding.radioOption1, binding.radioOption2, binding.radioOption3)
            if (data.answer=="Yes"){
                binding.radioOption1.isChecked=true
            }else if (data.answer=="No"){
                binding.radioOption2.isChecked=true
            }else if (data.answer=="N/A"){
                binding.radioOption3.isChecked=true
            }
            // Set a listener for each radio button
            for (radioButton in radioButtons) {
                radioButton.setOnClickListener {
                    formQuestionsFragment.onRadioButtonClicked(radioButton, binding.radioOption1,binding.radioOption2,binding.radioOption3,data.question)
//                onRadioButtonClicked(radioButton, )
                    binding.rvNestedQuestionList.visibility=View.VISIBLE
                    val getQuestionFromDB = dbHelper.getQuestionsForSection("1")
                    val adapter = NestedAdapter(baseActivity = baseActivity!!, context = BaseFragment.baseFragment!!, mList = getQuestionFromDB,
                        formQuestionsFragment = FormQuestionsFragment.formQuestionsFragment!!, dbHelper = dbHelper, id = "1", sectionName = sectionName)
                    val linearLayoutManager = LinearLayoutManager(baseActivity)
                    binding.rvNestedQuestionList.adapter = adapter
                    binding.rvNestedQuestionList.layoutManager = linearLayoutManager
                }
            }

//            val getQuestionFromDB = dbHelper.getQuestionsForSection("1")
//            val adapter = NestedAdapter(baseActivity = baseActivity!!, context = BaseFragment.baseFragment!!, mList = getQuestionFromDB,
//            formQuestionsFragment = FormQuestionsFragment.formQuestionsFragment!!, dbHelper = dbHelper, id = "1", sectionName = sectionName)
//            val linearLayoutManager = LinearLayoutManager(baseActivity)
//            binding.rvNestedQuestionList.adapter = adapter
//            binding.rvNestedQuestionList.layoutManager = linearLayoutManager

        } else if (data.type == "Text") {
            binding.llSingleLine.visibility = View.VISIBLE
            binding.edtText.setText(data.answer)
            binding.edtText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    dbHelper.updateQuestionAnswer(id,data.question,binding.edtText.getText().toString())
                }
                override fun afterTextChanged(s: Editable) {}
            })

        } else if (data.type == "MultiLineText") {
            binding.llMultiLine.visibility = View.VISIBLE
            binding.edtMultiple.setText(data.answer)
            binding.edtMultiple.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    dbHelper.updateQuestionAnswer(id,data.question,binding.edtMultiple.getText().toString())
                }
                override fun afterTextChanged(s: Editable) {}
            })

        } else if (data.type == "MultiSelect") {
            binding.llMultiSelect.visibility = View.VISIBLE
            val str = data.values
            val list = str.split(",").map { it.replace("#", "") }.toMutableList()

            val answerstr = data.answer
//            val answerlist = answerstr.split(",").map { it.replace("#", "") }.toMutableList()

            val answerlist = answerstr.split(",").map { it.replace("#", "") }
                .filter { it.isNotBlank() } // Filter out empty or blank items
                .toMutableList()

            val innerAdapter = FormDataListAdapter(answerlist, this,dbHelper,id,data.question)
            binding.rvData.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
            binding.tvAdd.setOnClickListener {
                formQuestionsFragment.showDataDialog(list,data.question, innerAdapter)
            }

        } else if (data.type == "Year") {
            binding.llYear.visibility = View.VISIBLE
            binding.tvYearPicker.setText(data.answer)

            binding.tvYearPicker.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Do something after the text has changed
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Do something before the text has changed
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when the text has changed
                    dbHelper.updateQuestionAnswer(id,data.question,binding.tvYearPicker.getText().toString())
                }
            })

            binding.llYear.setOnClickListener {
                formQuestionsFragment.showYearPickerDialog(binding.tvYearPicker)
            }
        } else if (data.type == "Date") {
            binding.llDate.visibility = View.VISIBLE
            binding.assignDateSelectedTv.setText(data.answer)
            binding.assignDateSelectedTv.setOnClickListener {
                formQuestionsFragment.selectDate(binding.assignDateSelectedTv)
            }
            binding.assignDateSelectedTv.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Do something after the text has changed
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Do something before the text has changed
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when the text has changed
                    dbHelper.updateQuestionAnswer(id,data.question,binding.assignDateSelectedTv.getText().toString())
                }
            })
        } else if (data.type == "Image") {
            binding.tvUploadImage.setOnClickListener {
                formQuestionsFragment.showCameraGalleryDialog(position,data.question)
            }
            binding.llImageSelect.visibility = View.VISIBLE
            val bitmap = formQuestionsFragment.base64ToBitmap(base64EncodedString)
            if (!data.answer.equals("")) {
                binding.rlImageview.visibility=View.VISIBLE
//                val imageResource = BitmapDrawable(binding.ivUploadedImage.resources, bitmap)
//                binding.ivUploadedImage.setImageDrawable(imageResource)
                Glide
                    .with(formQuestionsFragment.requireActivity())
                    .load(Const.IMG_URL2+data.answer)
                    .into( binding.ivUploadedImage)
                binding.ivDeleteImage.setOnClickListener {
                    binding.ivUploadedImage.setImageBitmap(null)
                    binding.rlImageview.visibility=View.GONE
                    dbHelper.updateQuestionAnswer(id,data.question,"")
                    var key=sectionName+data.question
//                    formQuestionsFragment.deleteValueFromHashMap(key)
                    dbHelper.deleteKeyValue(key)
                }
            }else{
                binding.rlImageview.visibility=View.GONE
            }

            /*base64EncodedString=data.answer
            val bitmap = formQuestionsFragment.base64ToBitmap(base64EncodedString)
            if (bitmap != null) {
                binding.rlImageview.visibility=View.VISIBLE
                val imageResource = BitmapDrawable(binding.ivUploadedImage.resources, bitmap)

                binding.ivUploadedImage.setImageDrawable(imageResource)
               *//* Glide
                    .with(formQuestionsFragment.requireActivity())
                    .load(imageResource)
                    .into( binding.ivUploadedImage)*//*
                binding.ivDeleteImage.setOnClickListener {
                    binding.ivUploadedImage.setImageBitmap(null)
                    binding.rlImageview.visibility=View.GONE
                    dbHelper.updateQuestionAnswer(id,data.question,"")
                    var key=sectionName+data.question
                    formQuestionsFragment.deleteValueFromHashMap(key)
                }
            }else{
                binding.rlImageview.visibility=View.GONE
            }*/

            binding.ivDeleteImage.setOnClickListener {
                binding.ivUploadedImage.setImageBitmap(null)
                binding.rlImageview.visibility=View.GONE
                dbHelper.updateQuestionAnswer(id,data.question,"")
            }
        } else if (data.type == "Graph") {
            binding.llGraph.visibility = View.VISIBLE
            binding.tvQuestion.visibility = View.GONE
            binding.llGraph.setOnClickListener {
                baseActivity.replaceFragment(FlowTestFragment(),R.id.frame_container)
            }
        }

        binding.tvQuestion.text = data.question

    }

    /* fun showImage(bitmap: Bitmap, question: String) {
         for (item in mList) {
             if (item.type == "Image" && item.question == question) {
                 item.answer = encodeImage(bitmap)
                 notifyDataSetChanged()
                 break  // Exit loop after finding the matching question
             }
         }
     }*/


    fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun showImage(bitmap: Bitmap,base64EncodedString: String,position: Int){
        imagePosition=position
        this.bitmap=bitmap
        this.base64EncodedString=base64EncodedString
        notifyDataSetChanged()
    }


    fun areAllFieldsFilled(): Boolean {
        for (item in mList) {
            if (item.type == "Boolean" || item.type == "Text" || item.type == "MultiLineText") {
                // Check if the answer field is empty for Boolean, Text, and MultiLineText types
                if (item.answer.isNullOrBlank()) {
                    return false
                }
            } else if (item.type == "MultiSelect") {
                // Check if the answer field is empty for MultiSelect type (assuming it's a comma-separated answer)
                if (item.answer.isNullOrBlank() || item.answer == "N/A") {
                    return false
                }
            } else if (item.type == "Year" || item.type == "Date") {
                // Check if the answer field is empty for Year and Date types
                if (item.answer.isNullOrBlank()) {
                    return false
                }
            }
            // You can add more conditions for other field types as needed
        }
        // All fields are filled
        return true
    }




    // return the number of the items in the list

    override fun getItemCount(): Int = mList.size

    fun onRadioButtonClicked(
        selectedRadioButton: RadioButton,
        binding: FormQuestionsListBinding
    ) {
        // Uncheck all radio buttons except the selected one
        val radioOption1 = binding.radioOption1
        val radioOption2 = binding.radioOption2
        val radioOption3 = binding.radioOption3

        if (selectedRadioButton != radioOption1) {
            radioOption1.isChecked = false
        }
        if (selectedRadioButton != radioOption2) {
            radioOption2.isChecked = false
        }
        if (selectedRadioButton != radioOption3) {
            radioOption3.isChecked = false
        }
        // Check the selected radio button
        selectedRadioButton.isChecked = true

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onclick(position: Int, formDataListAdapter: FormDataListAdapter) {
        formQuestionsFragment.showDeletePopup(position,formDataListAdapter)

    }

}