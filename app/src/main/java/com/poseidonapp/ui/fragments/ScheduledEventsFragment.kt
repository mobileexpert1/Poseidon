package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentScheduledEventsBinding
import com.poseidonapp.model.schedules.MeetingsItem
import com.poseidonapp.ui.adapter.ScheduledEventsAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.schedules.SchedulesViewModel


class ScheduledEventsFragment : BaseFragment(),HandleClickListener,ScheduledEventsAdapter.ClickListeners {

    var binding: FragmentScheduledEventsBinding?=null
    private var scheduledEventsAdapter: ScheduledEventsAdapter? = null
    lateinit var schedulesViewModel: SchedulesViewModel
    var meetings= java.util.ArrayList<MeetingsItem>()

    companion object{
        var date:String=""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date=it.getString("date").toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentScheduledEventsBinding.inflate(inflater, container, false)
        initUI()
        observers()
        return binding!!.root
    }


    private fun initUI() {
        binding!!.handleClick = this
        schedulesViewModel = ViewModelProvider(this)[SchedulesViewModel::class.java]
        schedulesViewModel.scheduleRequest(sharedPref.getString(Const.TOKEN),date)

    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBacks -> {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observers() {

        //Schedules
        schedulesViewModel.scheduleSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                // showToast(it.message)
                meetings.clear()
                meetings.addAll(it.dataSchedule.meetings)
                if(meetings!=null){
                    scheduledEventsAdapter = ScheduledEventsAdapter(baseActivity!!, meetings,this)
                    val linearLayoutManager = LinearLayoutManager(context)
                    binding!!.rvScheduledEvents.adapter = scheduledEventsAdapter
                    binding!!.rvScheduledEvents.layoutManager = linearLayoutManager
                    binding!!.noDataFoundTV.visibility=View.GONE
                }else{
                    binding!!.noDataFoundTV.visibility=View.VISIBLE
                }

            }
        })

        schedulesViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        schedulesViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }

    override fun onclick(position: Int, attachment: String) {

        val bundle = Bundle()
        val fragment=WebViewOfScheduleFragment()
        bundle.putString("attachment",attachment)
        fragment.setArguments(bundle)
        baseActivity!!.replaceFragment(fragment)

    }
}