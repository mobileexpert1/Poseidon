package com.poseidonapp.inspector.signature

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentSignatureBinding
import com.poseidonapp.inspector.report.ReportFragment
import com.poseidonapp.model.questions.Data
import com.poseidonapp.model.questions.Error
import com.poseidonapp.model.questions.QuestionsResponse
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.inspectorReportClone.InspectorReportCloneViewModel

class SignatureFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentSignatureBinding? = null
    lateinit var inspectorReportCloneViewModel: InspectorReportCloneViewModel
    var requestId: String = ""

    //    var questionJsonlist: String = ""
//    var questionJsonlist= ArrayList<DataItem>()
    var questionJsonlist = ArrayList<Data>()
    var customerSign: String = ""
    var inspectorSign: String = ""
    var timeTake: String = ""
    var wintrizationSwitch : String = ""
    var fireExtinguishrSwitch  : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            requestId = it.getString("requestId")!!
//            questionJsonlist=it.getString("questionJsonlist")!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentSignatureBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        questionJsonlist = ArrayList()
        inspectorReportCloneViewModel =
            ViewModelProvider(this)[InspectorReportCloneViewModel::class.java]

        binding.apply {
            this!!.Clientsignaturepad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {

                }

                override fun onSigned() {
                    tvClearClientSign.setEnabled(true)
                }

                override fun onClear() {
                    tvClearClientSign.setEnabled(false)
                }
            })

            this.Inspectorsignaturepad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {

                }

                override fun onSigned() {
                    tvClearInspectorSign.setEnabled(true)
                }

                override fun onClear() {
                    tvClearInspectorSign.setEnabled(false)
                }
            })

            winterizationSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Switch is ON
                    wintrizationSwitch = "Yes"
//                    Toast.makeText(requireContext(), "Switch is ON", Toast.LENGTH_SHORT).show()
                } else {
                    // Switch is OFF
                    wintrizationSwitch = "No"

//                    Toast.makeText(requireContext(), "Switch is OFF", Toast.LENGTH_SHORT).show()
                }
            }

            fireExtinguisherSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Switch is ON
                    fireExtinguishrSwitch = "Yes"
//                    Toast.makeText(requireContext(), "Switch is ON", Toast.LENGTH_SHORT).show()
                } else {
                    // Switch is OFF
                    fireExtinguishrSwitch = "No"
//                    Toast.makeText(requireContext(), "Switch is OFF", Toast.LENGTH_SHORT).show()
                }
            }

        }

//        observers()

    }

    companion object {

    }

    private fun inspectionTakePopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_how_long_inspection_take)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        val edtEnterTime = dialog.findViewById(R.id.edtEnterTime) as EditText
        val tvDone = dialog.findViewById(R.id.tvDone) as TextView

        tvDone.setOnClickListener {
            if (edtEnterTime.text.toString().isEmpty()) {
                showToast("Please enter time")
            } else {
                val signatureClient: Bitmap = binding!!.Clientsignaturepad.getSignatureBitmap()
                customerSign = convertImageToBase64String(signatureClient)
                val signatureInspector: Bitmap =
                    binding!!.Inspectorsignaturepad.getSignatureBitmap()
                inspectorSign = convertImageToBase64String(signatureInspector)

                timeTake = edtEnterTime.text.toString()

                questionJsonlist.addAll(dbHelper.getAllSections1()) // Replace with your actual database retrieval logic


//                initialHashMap = getHashMapFromPrefs()

                Log.d("HashMap", "$initialHashMap")

                val questionJson = createQuestionJsonString(questionJsonlist)
                Log.d("HashMap", "$questionJson")

//                initialHashMap = getHashMapFromPrefs()
                initialHashMap = dbHelper.getAllKeyValuePairs()


//                "sessionToken": sessionToken!,
//                "requestId": SingleTon.shared.reqesedtUserId!,
//                "customerSign": customerSignStr!,
//                "inspectorSign": inspectorSignStr!,
//                "questionJson": questionJson!,
//                "flowTestTableValues": flowTestTableValues!,
//                "graphBase64": graphBase64!,
//                "timeTake": timeTake!,
//                "winterization": winterization!,
//                "fireExtinguisher" : fireExtinguisher!,
//                "systemLocationText": SingleTon.shared.systemLocationText,
//                "chart1": chart1Values!,
//                "chart2": chart2Values!,

                inspectorReportCloneViewModel.inspectorReportCloneRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN), requestId = requestId,
                    customerSign = customerSign,
                    inspectorSign = inspectorSign, questionJson = questionJson, flowTestTableValues = "",
                    graphBase64 = "",
                    timeTake = timeTake, winterization = wintrizationSwitch,
                    fireExtinguisher = fireExtinguishrSwitch, systemLocationText = "", chart1 = "",
                    chart2 = "",
                    additionalFields = initialHashMap)
                observers()

                /* inspectorReportCloneViewModel.inspectorReportCloneRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    requestId = requestId,
                    customerSign = customerSign,
                    inspectorSign = inspectorSign,
                    timeTake = timeTake,
                    questionJson = questionJson,
                    graphBase64 = "",
                    flowTestTableValues = "",
                    additionalFields = initialHashMap
                )*/

                dialog.dismiss()
            }
        }

        dialog.show()

    }


    fun createQuestionJsonString(questionJsonlist1: List<Data>): String {
        val gson = Gson()
        val questionJson = QuestionsResponse(
            data = questionJsonlist1,
            error = Error(), message = "Questions List", status = true
        )
        /* val questionJson = QuestionsResponse(
            status = true,
            message = "Questions List",
            data = questionJsonlist1,
            ""
            )*/
        return gson.toJson(questionJson)
    }

    /*   fun createQuestionJsonString(questionJsonlist1: List<DataItem>): String {
        val gson = Gson()
        val questionJson = QuestionJson(
            status = true,
            message = "Questions List",
            data = questionJsonlist1
            )
        return gson.toJson(questionJson)
    }*/


    private fun sessionExpiredPopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
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

    fun observers() {

        inspectorReportCloneViewModel.inspectorReportCloneSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    dbHelper.clearDatabase()
                    val bundle = Bundle()
                    bundle.putString("attachment", it.data.pdfName)
//                    baseActivity!!.replaceFragWithArgs(
//                        ReportFragment(),
//                        R.id.frame_container,
//                        bundle
//                    )
                    findNavController().navigate(R.id.reportFragment,bundle)

                }
            })

        inspectorReportCloneViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        inspectorReportCloneViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            }else{
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }


    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.tvClearClientSign -> {
                binding!!.Clientsignaturepad.clear()
            }

            R.id.tvClearInspectorSign -> {
                binding!!.Inspectorsignaturepad.clear()
            }

            R.id.submitTV -> {
                if (binding!!.Clientsignaturepad.isEmpty()) {
                    showToast("Please add client signature")
                } else if (binding!!.Inspectorsignaturepad.isEmpty()) {
                    showToast("Please add inspector signature")
                } else {
                    inspectionTakePopup()
                }
            }

            R.id.ivBack -> {
                baseActivity!!.onBackPressed()
            }
        }
    }


}