package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentRequestDetailBinding
import com.poseidonapp.model.clockInlistscreen.ProjectsItem
import com.poseidonapp.model.pendingrequest.MeetingsItem
import com.poseidonapp.ui.adapter.RequestMeetingAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.pendingrequest.PendingRequestViewModel
import com.poseidonapp.viewmodel.respondmeetingrequest.RespondMeetingRequestViewModel

class RequestDetailFragment : BaseFragment(),HandleClickListener,RequestMeetingAdapter.ClickListeners {

    var binding: FragmentRequestDetailBinding?=null
    private var requestMeetingAdapter: RequestMeetingAdapter? = null
    lateinit var pendingRequestViewModel: PendingRequestViewModel
    lateinit var respondMeetingRequestViewModel: RespondMeetingRequestViewModel
    var meetingsItemList: List<MeetingsItem>? = null

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
            binding = FragmentRequestDetailBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        pendingRequestViewModel = ViewModelProvider(this)[PendingRequestViewModel::class.java]
        respondMeetingRequestViewModel = ViewModelProvider(this)[RespondMeetingRequestViewModel::class.java]
        pendingRequestViewModel.pendingrequestRequest(sharedPref.getString(Const.TOKEN))
        observers()
    }

    companion object {
    }

    fun setAdapter() {

        if (meetingsItemList!!.size > 0) {
            binding!!.rvMeeting.visibleView(true)
            binding!!.noDataFoundTV.visibleView(false)
            requestMeetingAdapter = RequestMeetingAdapter(baseActivity!!, meetingsItemList!!,this)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.rvMeeting.adapter = requestMeetingAdapter
            binding!!.rvMeeting.layoutManager = linearLayoutManager
//            shiftTimerAdapter!!.setOnItemClickListener(this)
        } else {
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.rvMeeting.visibleView(false)
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
            sharedPref.clearPref()
            context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)
            baseActivity!!.gotoLoginSignUpActivity()
        }

        dialog.show()

    }

    private fun observers() {

        pendingRequestViewModel.pendingrequestSuccess.observe(viewLifecycleOwner, Observer {
            meetingsItemList = it.pendingrequestData.meetings
            setAdapter()
        })

        pendingRequestViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        pendingRequestViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

        respondMeetingRequestViewModel.respondMeetingRequestSuccess.observe(viewLifecycleOwner, Observer {
            showToast(it.message)
        })

        respondMeetingRequestViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        respondMeetingRequestViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

    }


    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                baseActivity!!.onBackPressed()

//                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onclick(position: Int, isChecked: Boolean, meetingID:String) {
        if (isChecked==false){
            respondMeetingRequestViewModel.respondMeetingRequest(sharedPref.getString(Const.TOKEN),meetingID,"2")
            pendingRequestViewModel.pendingrequestRequest(sharedPref.getString(Const.TOKEN))
        }else {
            respondMeetingRequestViewModel.respondMeetingRequest(sharedPref.getString(Const.TOKEN),meetingID,"1")
            pendingRequestViewModel.pendingrequestRequest(sharedPref.getString(Const.TOKEN))
        }
    }
}