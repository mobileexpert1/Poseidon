package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentAddProjectBinding
import com.poseidonapp.databinding.FragmentWorkOrderBinding
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.createproject.CreateProjectViewModel
import com.poseidonapp.viewmodel.emergencyrequest.EmergencyRequestViewModel

class AddProjectFragment : BaseFragment(),HandleClickListener {
    var binding: FragmentAddProjectBinding? = null
    lateinit var createProjectViewModel: CreateProjectViewModel
    var selectState: String = ""
    var statesArray = arrayOf(
        "Alabama",
        "Alaska",
        "Arizona",
        "Arkansas",
        "California",
        "Colorado",
        "Connecticut",
        "Delaware",
        "Florida",
        "Georgia",
        "Hawaii",
        "Idaho",
        "Illinois",
        "Indiana",
        "Iowa",
        "Kansas",
        "Kentucky",
        "Louisiana",
        "Maine",
        "Maryland",
        "Massachusetts",
        "Michigan",
        "Minnesota",
        "Mississippi",
        "Missouri",
        "Montana",
        "Nebraska",
        "Nevada",
        "New Hampshire",
        "New Jersey",
        "New Mexico",
        "New York",
        "North Carolina",
        "North Dakota",
        "Ohio",
        "Oklahoma",
        "Oregon",
        "Pennsylvania",
        "Rhode Island",
        "South Carolina",
        "South Dakota",
        "Tennessee",
        "Texas",
        "Utah",
        "Vermont",
        "Virginia",
        "Washington",
        "West Virginia",
        "Wisconsin",
        "Wyoming"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentAddProjectBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        createProjectViewModel = ViewModelProvider(this)[CreateProjectViewModel::class.java]
        observer()

        //State
        binding!!.psSelectState.setItems(statesArray.toList())
        binding!!.psSelectState.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            showToast("$newText selected!")
            selectState = newText
        }
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack ->{
               findNavController().popBackStack()
                binding!!.psSelectState.dismiss()
            }
            R.id.tvSave -> {
                if (binding!!.edtProjectName.text.isNullOrEmpty() || binding!!.edtProjectName.text.isBlank()) {
                    showToast("Please enter project name")
                } else if (binding!!.edtContractorName.text.isNullOrEmpty() || binding!!.edtContractorName.text.isBlank()) {
                    showToast("Please enter address")
                } else if (binding!!.edtAddress.text.isNullOrEmpty() || binding!!.edtAddress.text.isBlank()) {
                    showToast("Please enter address")
                } else if (binding!!.edtCity.text.isNullOrEmpty() || binding!!.edtCity.text.isBlank()) {
                    showToast("Please enter city")
                } else if (binding!!.psSelectState.text.isNullOrEmpty() || binding!!.psSelectState.text.isBlank()) {
                    showToast("Please enter state")
                } else if (binding!!.edtPostalCode.text.isNullOrEmpty() || binding!!.edtPostalCode.text.isBlank()) {
                    showToast("Please enter postal code")
                } else {
                    createProjectViewModel.createProjectRequest(
                        sessionToken = sharedPref.getString(Const.TOKEN),
                        name = binding!!.edtProjectName.text.toString(),
                        contractorName = binding!!.edtContractorName.text.toString(),
                        lat = sharedPref.getString(Const.latitude),
                        long = sharedPref.getString(Const.longitude),
                        address = binding!!.edtAddress.text.toString(),
                        city = binding!!.edtCity.text.toString(),
                        state = selectState,
                        postalCode = binding!!.edtPostalCode.text.toString()
                    )
                }


            }

        }
    }

    private fun observer(){
        createProjectViewModel.createProjectSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                showToast(it.message)
                binding!!.edtProjectName.setText("")
                binding!!.edtContractorName.setText("")
                binding!!.edtAddress.setText("")
                binding!!.edtCity.setText("")
                binding!!.psSelectState.setText("")
                binding!!.edtPostalCode.setText("")

            }
        })

        createProjectViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        createProjectViewModel.isLoading.observe(viewLifecycleOwner) {
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
}

