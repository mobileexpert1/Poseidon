package com.poseidonapp.inspector.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentInspectorHomeBinding
import com.poseidonapp.inspector.detail.DetailFillFragment
import com.poseidonapp.model.inspection.Request
import com.poseidonapp.inspector.adapter.InspectionAdapter
import com.poseidonapp.inspector.completedrequest.CompletedRequestsFragment
import com.poseidonapp.inspector.form.InspectionFormFragment
import com.poseidonapp.inspector.notification.InspectorNotificationFragment
import com.poseidonapp.ui.adapter.RequestMeetingAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.acceptreject.AcceptRejectViewModel
import com.poseidonapp.viewmodel.inspectionViewModel.InspectionRequestListViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class InspectorHomeFragment : BaseFragment(), HandleClickListener,
    InspectionAdapter.ClickListeners {


    var binding: FragmentInspectorHomeBinding? = null
    lateinit var inspectionRequestListViewModel: InspectionRequestListViewModel
    lateinit var acceptRejectViewModel: AcceptRejectViewModel
    private var adapter: InspectionAdapter? = null
    lateinit var requestLists: ArrayList<Request>
    var dialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentInspectorHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        requestLists = ArrayList()
        inspectionRequestListViewModel = ViewModelProvider(this)[InspectionRequestListViewModel::class.java]
        acceptRejectViewModel = ViewModelProvider(this)[AcceptRejectViewModel::class.java]
        inspectionRequestListViewModel.inspectionRequestList(sharedPref.getString(Const.TOKEN), "")
        observer()
    }

    companion object {
    }

    fun selectDate() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                if (!selectedDate.before(currentDate)) {
                    // Do something with the selected date
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )

                    inspectionRequestListViewModel.inspectionRequestList(sessionToken = sharedPref.getString(Const.TOKEN), filterByDate = formattedDate)

                    // Update your UI or perform any other actions with the formatted date
                }
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.setOnShowListener { dialog: DialogInterface ->
            val positiveColor: Int = ContextCompat.getColor(
                requireView().context,R.color.dark_blue
            )
            val negativeColor: Int = ContextCompat.getColor(requireView().context,R.color.dark_blue)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
        }
        datePickerDialog.show()
    }


    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivRefresh -> {
                inspectionRequestListViewModel.inspectionRequestList(
                    sharedPref.getString(Const.TOKEN),
                    ""
                )
            }
            R.id.addDetails -> {
                baseActivity!!.replaceFragment(DetailFillFragment(), R.id.frame_container)
            }
            R.id.ivBack -> {
                baseActivity!!.onBackPressed()
//                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.ivCalendarR -> {
                selectDate()
            }
            R.id.ivNotifications -> {
                baseActivity!!.replaceFragment(InspectorNotificationFragment(), R.id.frame_container)
            }

            R.id.ivHistory -> {
                baseActivity!!.replaceFragment(CompletedRequestsFragment(), R.id.frame_container)
            }
        }
    }

    fun setAdapter() {
        if (requestLists.size > 0) {
            binding!!.inspectionRecylerView.visibleView(true)
            binding!!.noDataFoundTV.visibleView(false)
            adapter = InspectionAdapter(this, requestLists, this)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.inspectionRecylerView.adapter = adapter
            binding!!.inspectionRecylerView.layoutManager = linearLayoutManager
//            shiftTimerAdapter!!.setOnItemClickListener(this)
        } else {
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.inspectionRecylerView.visibleView(false)
        }

//        adapter = InspectionAdapter(this, requestLists, this)
//        binding!!.inspectionRecylerView.layoutManager = LinearLayoutManager(context)
//        binding!!.inspectionRecylerView.adapter = adapter
//        adapter!!.notifyDataSetChanged()
    }


    fun observer(){
        // Initialize your UI components and view models

        // Observe the inspection request list LiveData

        inspectionRequestListViewModel.inspectionRequestListSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                requestLists.clear()
                requestLists.addAll(response.data.requests)
                if (!requestLists.isNullOrEmpty()) {
                    setAdapter()
                }
            }
        }

        // Observe API error LiveData
        inspectionRequestListViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe loading state LiveData
        inspectionRequestListViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


        // Observe the accept/reject success LiveData
        acceptRejectViewModel.acceptRejectSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                inspectionRequestListViewModel.inspectionRequestList(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    filterByDate = ""
                )
                showToast(response.message)
            }
        }

        // Observe the accept/reject API error LiveData
        acceptRejectViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe the accept/reject loading state LiveData
        acceptRejectViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }




  /*  fun observer() {
        inspectionRequestListViewModel.inspectionRequestListSuccess.observe(
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

        inspectionRequestListViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        inspectionRequestListViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB.show()
            } else {
                baseActivity!!.progressBarPB.dismiss()
            }
        }

        acceptRejectViewModel.acceptRejectSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
                    inspectionRequestListViewModel.inspectionRequestList(sharedPref.getString(Const.TOKEN), "")
                    showToast(it.message)
                }
            })

        acceptRejectViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        acceptRejectViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB.show()
            } else {
                baseActivity!!.progressBarPB.dismiss()
            }
        }
    }*/

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

    fun requestDetailPopup(inspectorInspectionStatus: String, inspectionType: String, userName: String, mobileNumber: String, location: String,image:String,id:String) {
        dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.popup_request_listing)
        val tvInspectionType = dialog!!.findViewById<TextView>(R.id.tvInspectionType)
        val profile_image = dialog!!.findViewById<CircleImageView>(R.id.profile_image)
        val tvUserName = dialog!!.findViewById<TextView>(R.id.tvUserName)
        val tvMobileNumber = dialog!!.findViewById<TextView>(R.id.tvMobileNumber)
        val tvLocation = dialog!!.findViewById<TextView>(R.id.tvLocation)
        val tvMakeReport = dialog!!.findViewById<TextView>(R.id.tvMakeReport)
        val tvAccept = dialog!!.findViewById<TextView>(R.id.tvAccept)
        val tvReject = dialog!!.findViewById<TextView>(R.id.tvReject)
        val bottomsheetRateLL = dialog!!.findViewById<LinearLayout>(R.id.imgeLL)
        val llAcceptReject = dialog!!.findViewById<LinearLayout>(R.id.llAcceptReject)

        Glide
            .with(requireActivity())
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .into(profile_image!!)

        if(inspectorInspectionStatus.equals("0")){
            llAcceptReject!!.visibility=View.VISIBLE
            tvMakeReport!!.visibility=View.GONE
        }else{
            llAcceptReject!!.visibility=View.GONE
            tvMakeReport!!.visibility=View.VISIBLE
        }


        tvInspectionType!!.text=inspectionType
        tvUserName!!.text=userName
        tvMobileNumber!!.text=mobileNumber
        tvLocation!!.text=location

        tvMakeReport.setOnClickListener {
//            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "", requestId = "")
            dialog!!.dismiss()

            val bundle = Bundle()
            bundle.putString("requestId", id)
            baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
        }

        tvAccept!!.setOnClickListener {
            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "1", requestId = id)
            dialog!!.dismiss()
            val bundle = Bundle()
            bundle.putString("requestId", id)
            baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
        }

        tvReject!!.setOnClickListener {
            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "2", requestId = id)
            dialog!!.dismiss()
        }

//            ratingBar!!.rating=(completedAppointmentList.get(position).rating.toFloat())
//            emailTV!!.setText(completedAppointmentList.get(position).email)
//            ageTV!!.setText(completedAppointmentList.get(position).age)
//            appointmentDateTV!!.setText(completedAppointmentList.get(position).formattedBookingDate)
        dialog!!.show()


    }


    override fun onclick(position: Int, inspectorInspectionStatus: String, inspectionType: String, userName: String, mobileNumber: String, location: String,image:String,id:String) {

        requestDetailPopup(inspectionType = inspectionType, userName = userName, mobileNumber = mobileNumber, location = location, inspectorInspectionStatus = inspectorInspectionStatus, image = image, id = id)
    }


}