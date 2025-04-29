package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.poseidonapp.databinding.FragmentSafetyMeetingsBinding
import com.poseidonapp.inspector.adapter.NotificationAdapter
import com.poseidonapp.model.safetymeetings.SafetyItem
import com.poseidonapp.ui.adapter.SafetyMeetingsAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.*
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.inspectionnotification.InpectionNotificationViewModel
import com.poseidonapp.viewmodel.safetymeetings.SafetyMeetingsViewModel
import java.time.format.DateTimeFormatter

class SafetyMeetingsFragment : BaseFragment(), HandleClickListener, SafetyMeetingsAdapter.ClickListeners {

    var binding: FragmentSafetyMeetingsBinding? = null
    private var adapter: SafetyMeetingsAdapter? = null
    lateinit var safetyItemLists: ArrayList<SafetyItem>
    lateinit var safetyMeetingViewModel: SafetyMeetingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentSafetyMeetingsBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        safetyItemLists = ArrayList()
        safetyMeetingViewModel = ViewModelProvider(this)[SafetyMeetingsViewModel::class.java]
        safetyMeetingViewModel.safetyMeetingRequest(sharedPref.getString(Const.TOKEN))
        observer()

    }

    fun setAdapter() {
        if (safetyItemLists.size > 0) {
            binding!!.rvSafetyMeetings.visibleView(true)
            binding!!.noDataFoundTV.visibility=View.GONE
            adapter = SafetyMeetingsAdapter(safetyItemLists,this)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.rvSafetyMeetings.adapter = adapter
            binding!!.rvSafetyMeetings.layoutManager = linearLayoutManager
        } else {
            binding!!.noDataFoundTV.visibility=View.VISIBLE
            binding!!.rvSafetyMeetings.visibleView(false)
        }

    }

    companion object {

    }

    fun observer() {
        safetyMeetingViewModel.safetyMeetingSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    safetyItemLists.clear()
                    safetyItemLists.addAll(it.data.safety)
                    if(safetyItemLists!=null){
                        binding!!.noDataFoundTV.visibility=View.GONE
                        setAdapter()
                    }else{
                        binding!!.noDataFoundTV.visibility=View.VISIBLE
                    }
                }
            })

        safetyMeetingViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        safetyMeetingViewModel.isLoading.observe(viewLifecycleOwner) {
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
            R.id.icBack->{
//                findNavController().popBackStack()
                findNavController().navigate(R.id.action_safetyMeetingsFragment_to_workOrderFragment)
            }
        }

    }

   /* fun onBackPressed() {
        val fragmentCount = baseActivity!!.supportFragmentManager.backStackEntryCount
        if (fragmentCount > 0) {
            baseActivity!!.supportFragmentManager.popBackStack()
        } else {
            super.baseActivity!!.onBackPressed()
        }
    }*/

    override fun onclick(position: Int,id:String,attachment:String,signature:String) {
        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("attachment", attachment)
//        bundle.putString("attachment",Const.pdfBaseUrl+attachment)
        bundle.putString("signature", signature)
        Log.d("team","hsdb"+attachment+signature)
        findNavController().navigate(R.id.action_safetyMeetingsFragment_to_twenty47Fragment,bundle)
//        findNavController().navigate(R.id.action_safetyMeetingsFragment_to_twenty47Fragment,bundle)
//        baseActivity!!.replaceFragWithArgs(Twenty47Fragment(),R.id.frame_container,bundle)
    }
}