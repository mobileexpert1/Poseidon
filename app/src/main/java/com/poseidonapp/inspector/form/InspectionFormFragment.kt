package com.poseidonapp.inspector.form

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentInspectionFormBinding
import com.poseidonapp.inspector.adapter.FormListAdapter
import com.poseidonapp.inspector.formquestions.FormQuestionsFragment
import com.poseidonapp.inspector.signature.SignatureFragment
import com.poseidonapp.model.questions.Data
import com.poseidonapp.model.questions.QuestionJson
//import com.poseidonapp.model.inspectionHeadsQuestions.DataItem
//import com.poseidonapp.model.inspectionHeadsQuestions.QuestionJsonItem
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.addSystemfromReport.AddSystemFromReportViewModel
import com.poseidonapp.viewmodel.inspectionquestions.QuestionsViewModel

class InspectionFormFragment : BaseFragment(), HandleClickListener,
    BaseAdapter.OnItemClickListener {

    var binding: FragmentInspectionFormBinding? = null
    private var adapter: FormListAdapter? = null
    lateinit var questionsViewModel: QuestionsViewModel
    lateinit var addSystemFromReportViewModel: AddSystemFromReportViewModel
    var list = ArrayList<Data>()
    var list2 = ArrayList<Data>()
    var questionlist = ArrayList<QuestionJson>()
//    var requestId: String = ""

    companion object {
        var requestId: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            requestId = it.getString("requestId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentInspectionFormBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        val sectionsFromDb = dbHelper.getAllSections()
//        dbHelper.clearDatabase()

        baseActivity!!.progressBarPB!!.dismiss()
        list = ArrayList()
        list2 = ArrayList()
        questionlist = ArrayList()
        questionsViewModel = ViewModelProvider(this)[QuestionsViewModel::class.java]
        addSystemFromReportViewModel = ViewModelProvider(this)[AddSystemFromReportViewModel::class.java]

        if (sectionsFromDb.isNotEmpty()) {
            setAdapter()
        } else {
            questionsViewModel.questionsListRequest(requestId)
            observer()
        }



    }


    fun parseJsonResponse(jsonString: String): List<Data> {
        val jsonString = jsonString // Your JSON response string
        val gson = Gson()
        val type = object : TypeToken<List<Data>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun setAdapter() {
        val sectionsFromDb = dbHelper.getAllSections()
        list2.clear()
        list2.addAll(sectionsFromDb)
        adapter = FormListAdapter(baseActivity!!, list2)
        binding!!.rvFormList.adapter = adapter
        baseActivity!!.progressBarPB!!.dismiss()
        adapter!!.setOnItemClickListener(this)
        adapter?.notifyDataSetChanged()
    }

    fun addSystemReportObserver() {
        addSystemFromReportViewModel.addSystemFromReportSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    dbHelper.clearDatabase()
                    questionsViewModel.questionsListRequest(requestId)
                    observer()
                }
            })

        addSystemFromReportViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        addSystemFromReportViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }


    fun observer() {
        questionsViewModel.questionsListSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    dbHelper.clearDatabase()
                    list.clear()
                    list.addAll(it.data)
                    val gson = Gson()
                    val responseJson = gson.toJson(it.data)
                    // Assuming you have your JSON response parsed into a List<Section>
                    val sectionsFromJson: List<Data> = parseJsonResponse(responseJson)
                    // Insert the data into the database
                    dbHelper.insertSections(sectionsFromJson)
                    baseActivity!!.progressBarPB!!.show()
                    if (!list.isNullOrEmpty()) {
                        setAdapter()
                    }
                }
            })

        questionsViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        questionsViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


    }


    private fun sessionExpiredPopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_token_expired)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView

        tvOk.setOnClickListener {
            dialog.dismiss()
            context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)
            sharedPref.clearPref()
            baseActivity!!.gotoLoginSignUpActivity()
        }
        dialog.show()
    }


    val inspectorTypeList = arrayOf(
        "Fire pump",
        "Extinguishers",
        "Fire Hydrants",
        "Flow Test",
        "Tanks",
        "Wet System",
        "Dry Systen",
        "Anti- Freeze",
        "Standpipe",
        "Sprinkler Heads",
        "Pump",
        "While Pump Is Running",
        "Fire Department Connection",
        "Trip system"
    )

    var type = 0

    @SuppressLint("MissingInflatedId")
    private fun showaddFieldDialog() {
        var organizationdialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.system_allocation, null)
        organizationdialog.dismiss()
        organizationdialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        organizationdialog.setContentView(dialogView)
        val tvSubmit = dialogView.findViewById<TextView>(R.id.tvSubmitAloc)
        val ivaddCircle = dialogView.findViewById<ImageView>(R.id.ivaddCircle)
//        val etSystemName = dialogView.findViewById<EditText>(R.id.etSystemName)
        val etSystemLocation = dialogView.findViewById<EditText>(R.id.etSystemLocation)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val autoCompleteTextView = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_view, inspectorTypeList.toList())
        autoCompleteTextView.setAdapter(arrayAdapter)

//      val languages = resources.getStringArray(R.array.inspectorType)
//      val arrayAdapter = ArrayAdapter(
//          requireContext(),
//          R.layout.drop_down_view,
//          inspectorTypeList.toList()
//      )
//      binding!!.autoCompleteTextView.setAdapter(arrayAdapter)

        // Set an OnItemClickListener to capture the selected index
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            // 'position' is the selected index in the list
            val selectedIndex = position
            type = selectedIndex + 1
//            val selectedItem = inspectorTypeList[selectedIndex]
            // Use selectedIndex or selectedItem as needed
//          println("Selected index: $selectedIndex")
//            Log.d("DetailFill", "Selected index: $selectedIndex" )
//            Log.d("DetailFill", "Selected type: $type" )
//            Log.d("DetailFill", "Selected item: $selectedItem" )
//          println("Selected item: $selectedItem")
        }


        tvSubmit.setOnClickListener {
            if (autoCompleteTextView.text.toString().equals("") || etSystemLocation.text.toString().equals("")
            ) {
                Toast.makeText(context, "Please fill both fields", Toast.LENGTH_LONG).show()
            } else {
                addSystemFromReportViewModel.addSystemFromReportRequest(
                    requestId = requestId,
                    systemId = "",
                    systemDescription = etSystemLocation.text.toString(),
                    assignedQuestion = type.toString()
                )
                addSystemReportObserver()
                organizationdialog.dismiss()
//                observer()
            }

        }

        ivaddCircle.setOnClickListener {
            organizationdialog.dismiss()
        }
        organizationdialog.show()
    }


    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }

            R.id.tvAddSection -> {
                showaddFieldDialog()
            }

            R.id.submitTV -> {
                val bundle = Bundle()
                // Convert the list to a comma-separated string
                val gson = Gson()
                val json = gson.toJson(list2)
                val questionJsonlist = list2.joinToString(", ") { it.toString() }
//                bundle.putString("id",list2.get(pos).id)
                bundle.putString("requestId", requestId)
//                bundle.putString("questionJsonlist", questionJsonlist)
//                bundle.putString("id","4")
//                baseActivity!!.replaceFragWithArgs(
//                    SignatureFragment(),
//                    R.id.frame_container,
//                    bundle
//                )

                findNavController().navigate(R.id.signatureFragment,bundle)
            }
        }
    }

    override fun onItemClick(vararg itemData: Any) {
        val pos = itemData[0] as Int
        val type = itemData[1] as Int
        when (type) {
            Const.Item_Click -> {
                val bundle = Bundle()
                bundle.putString("sectionId", list2.get(pos).id.toString())
                bundle.putString("id", (pos + 1).toString())
                bundle.putString("sectionName", list2.get(pos).sectionName)
//                bundle.putString("id","4")
//                baseActivity!!.replaceFragWithArgs(
//                    FormQuestionsFragment(),
//                    R.id.frame_container,
//                    bundle
//                )
                findNavController().navigate(R.id.formQuestionsFragment,bundle)
            }
        }

    }

}