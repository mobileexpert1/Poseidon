package com.poseidonapp.inspector.notification

import android.app.Dialog
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
import com.poseidonapp.databinding.FragmentInspectorNotificationBinding
import com.poseidonapp.inspector.adapter.NotificationAdapter
import com.poseidonapp.model.inspectionnotification.RequestsItem
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.inspectionnotification.InpectionNotificationViewModel

class InspectorNotificationFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentInspectorNotificationBinding? = null

    lateinit var inspectionNotificationViewModel: InpectionNotificationViewModel

    private var adapter: NotificationAdapter? = null

    lateinit var requestLists: ArrayList<RequestsItem>

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
            binding = FragmentInspectorNotificationBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        requestLists = ArrayList()
        inspectionNotificationViewModel = ViewModelProvider(this)[InpectionNotificationViewModel::class.java]
        inspectionNotificationViewModel.inspectionNotificationRequest(sharedPref.getString(Const.TOKEN))
        observer()
    }

    fun setAdapter() {
        if (requestLists.size > 0) {
            binding!!.rvNotification.visibleView(true)
            binding!!.noDataFoundTV.visibleView(false)
            adapter = NotificationAdapter(requestLists)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.rvNotification.adapter = adapter
            binding!!.rvNotification.layoutManager = linearLayoutManager
//            shiftTimerAdapter!!.setOnItemClickListener(this)
        } else {
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.rvNotification.visibleView(false)
        }


//        adapter = NotificationAdapter(requestLists)
//        binding!!.rvNotification.layoutManager = LinearLayoutManager(context)
//        binding!!.rvNotification.adapter = adapter
//        adapter!!.notifyDataSetChanged()
    }

    companion object {

    }

    fun observer() {
        inspectionNotificationViewModel.inspectionNotificationSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    requestLists.clear()
                    requestLists.addAll(it.data.requests)
                    if (!requestLists.isNullOrEmpty()) {
                        setAdapter()
                    }

                }
            })

        inspectionNotificationViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        inspectionNotificationViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


    }


    override fun onViewClick(view: View) {
        when(view.id){
            R.id.ivBack->{
//                baseActivity!!.onBackPressed()
                findNavController().popBackStack()
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