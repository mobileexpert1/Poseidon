package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentEmergencyRequestBinding
import com.poseidonapp.inspector.report.ReportFragment
import com.poseidonapp.model.emergencyrequests.EmergencyRequestItem
import com.poseidonapp.model.safetymeetings.SafetyItem
import com.poseidonapp.ui.adapter.EmergencyRequestAdapter
import com.poseidonapp.ui.adapter.SafetyMeetingsAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.emergencyrequest.EmergencyRequestViewModel
import com.poseidonapp.viewmodel.safetymeetings.SafetyMeetingsViewModel
import com.poseidonapp.workorder.activities.WorkInputFragment

class EmergencyRequestFragment : BaseFragment(),HandleClickListener, EmergencyRequestAdapter.ClickListeners {

    var binding: FragmentEmergencyRequestBinding? = null
    lateinit var emergencyRequestLists: ArrayList<EmergencyRequestItem>
    lateinit var emergencyRequestViewModel: EmergencyRequestViewModel
    private var adapter: EmergencyRequestAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        if (binding == null)
            binding = FragmentEmergencyRequestBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }


    private fun initUI() {
        binding!!.handleClick = this
        /*if (sharedPref.getLanguageSelected() == "DARK") {
            val color = Color.WHITE // Replace with the color you want
            binding!!.ivBack.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        if (sharedPref.getLanguageSelected() == "LIGHT") {
            val color = R.color.dark_blue
            binding!!.ivBack.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }*/
        emergencyRequestLists = ArrayList()
        emergencyRequestViewModel = ViewModelProvider(this)[EmergencyRequestViewModel::class.java]
        emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN))
        observer()

    }

    fun setAdapter() {
        if (emergencyRequestLists.size > 0) {
            binding!!.rvEmergencyRequests.visibleView(true)
            binding!!.noDataFoundTV.visibility=View.GONE
            adapter = EmergencyRequestAdapter(emergencyRequestLists,this)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.rvEmergencyRequests.adapter = adapter
            binding!!.rvEmergencyRequests.layoutManager = linearLayoutManager
        } else {
            binding!!.noDataFoundTV.visibility=View.VISIBLE
            binding!!.rvEmergencyRequests.visibleView(false)
        }

    }




    fun observer() {
        emergencyRequestViewModel.emergencyRequestSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    if(emergencyRequestLists !=null){
                        binding!!.noDataFoundTV.visibility=View.GONE
                        emergencyRequestLists.clear()
                        emergencyRequestLists.addAll(it.data.emergencyRequest)
                        setAdapter()
                    }else{
                        binding!!.noDataFoundTV.visibility=View.VISIBLE
                    }


                }
            })

        emergencyRequestViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        emergencyRequestViewModel.isLoading.observe(viewLifecycleOwner) {
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




    override fun onViewClick(view: View) {
        when(view.id){
            R.id.ivBack -> {
               findNavController().popBackStack()
            }

            R.id.addTV ->{
//                findNavController().navigate(R.id.workInputFragment)
                baseActivity!!.replaceFragment(
                    WorkInputFragment(),
                    R.id.frame_container
                )

            }
        }

    }
    companion object {

    }

    override fun onclick(
        position: Int,
        id: String,
        name: String,
        terms: String,
        orderTakenBy: String,
        orderNumber: String,
        dateOfOrder: String,
        phone: String,
        jobName: String,
        jobLocation: String,
        jobPhone: String,
        startingDate: String
    ) {


        val bundle = Bundle()
        bundle.putString("id",id)
        bundle.putString("name",name)
        bundle.putString("terms",terms)
        bundle.putString("orderTakenBy",orderTakenBy)
        bundle.putString("orderNumber",orderNumber)
        bundle.putString("dateOfOrder",dateOfOrder)
        bundle.putString("phone",phone)
        bundle.putString("jobName",jobName)
        bundle.putString("jobLocation",jobLocation)
        bundle.putString("jobPhone",jobPhone)
        bundle.putString("startingDate",startingDate)
        baseActivity!!.replaceFragWithArgs(WorkInputFragment(), R.id.frame_container, bundle)

    }


}