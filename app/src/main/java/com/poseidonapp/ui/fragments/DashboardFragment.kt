package com.poseidonapp.ui.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.CalendarDayTextBinding
import com.poseidonapp.databinding.FragmentDashboardBinding
import com.poseidonapp.inspector.form.InspectionFormFragment
import com.poseidonapp.model.Appointment.data
import com.poseidonapp.model.dashboard.Data
import com.poseidonapp.ui.activities.LoginActivity
import com.poseidonapp.ui.adapter.AppointmentAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.loadFromUrl
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.HomeViewModel
import com.poseidonapp.viewmodel.acceptreject.AcceptRejectViewModel
import com.poseidonapp.viewmodel.checkroute.CheckRouteViewModel
import com.poseidonapp.viewmodel.dashboard.DashboardViewModel
import com.poseidonapp.viewmodel.onRoute.OnRouteViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.schedule

class DashboardFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentDashboardBinding? = null
    lateinit var calendarView: CalendarView
    val selectedDates = mutableListOf<LocalDate>()
    var isForwardButtonPressed = false
    var isBackwardButtonPressed = false
    var selectedDate: LocalDate? = LocalDate.now()
    lateinit var currentMonth: YearMonth
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val selectedRange = mutableSetOf<LocalDate>()
    lateinit var homeViewModel: HomeViewModel
    lateinit var dashboardViewModel: DashboardViewModel
    private val today = LocalDate.now()

    private val REQUEST_CAMERA_PERMISSION = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentDashboardBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    fun cameraPermissions(){

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }


    private fun checkAndRequestLocationPermissions() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            startLocationUpdates()
//            fusedLocation()
            checkAndEnableLocationServices()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), REQUEST_CODEE)
        }
    }

    /// location updates
    private val REQUEST_CODEE = 123
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var latitude: String = ""
    var longitude: String = ""



    private fun checkAndEnableLocationServices() {
        val locationMode: Int =
            Settings.Secure.getInt(
                baseActivity!!.applicationContext.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )

        if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            startLocationUpdates()
            fusedLocation()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODEE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                // Permission denied, handle accordingly
            }
        }
        else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
//                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest!!, locationCallback, null)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
//        getLocation()
        checkAndRequestLocationPermissions()
        cameraPermissions()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        fusedLocation()
        startLocationUpdates()
    }

    fun fusedLocation() {

        locationRequest = LocationRequest.create().apply {
            interval = 5
            fastestInterval = 5
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation.let { location ->
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        sharedPref.setString(Const.latitude, latitude)
                        sharedPref.setString(Const.longitude, longitude)
                    }

                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                }
            }
        }

    }


    private fun initUI() {
        binding!!.handleClick = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        acceptRejectViewModel = ViewModelProvider(this)[AcceptRejectViewModel::class.java]
        onRouteViewModel = ViewModelProvider(this)[OnRouteViewModel::class.java]
        checkRouteViewModel = ViewModelProvider(this)[CheckRouteViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setCalendar()
        dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN), "")
        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
        observer()
        homeviewObserver()
    }

    fun setAdapter(data: Data) {
//        val appointmentList = listOf(
//            data("Meeting","17/01/2024", "9:30AM-10:30AM"),
//            data("Check-up", "18/01/2024", "10:00AM-11:00AM"),
//            data("Meeting", "19/01/2024", "11:00AM-12:00PM") ,
//            data("Check-up", "20/01/2024", "12:00AM-1:00PM"),
//            data("Meeting", "21/01/2024", "1:00AM-2:00PM") ,
//            data("Check-up", "22/01/2024", "2:00AM-3:00PM")
//        )
        binding!!.apply {
            if (data.requests.isEmpty()) {
                rvAppointment.visibility = View.INVISIBLE
                tvNoDataFound.visibility = View.VISIBLE
            } else {
                rvAppointment.visibility = View.VISIBLE
                tvNoDataFound.visibility = View.INVISIBLE
                val appointmentAdapter =
                    AppointmentAdapter(data.requests, requireContext(), this@DashboardFragment)
                rvAppointment.layoutManager = LinearLayoutManager(requireContext())
                rvAppointment.adapter = appointmentAdapter
            }
        }

    }

    var dialog: BottomSheetDialog? = null
    lateinit var acceptRejectViewModel: AcceptRejectViewModel
    lateinit var onRouteViewModel: OnRouteViewModel
    lateinit var checkRouteViewModel: CheckRouteViewModel

    var onRoute = false

    var inspectionType = ""
    var userName = ""
    var mobileNumber = ""
    var location = ""
    var inspectorInspectionStatus = ""
    var image = ""
    var id = ""

    fun onRouteCheck(
        inspectorInspectionStatus: String,
        inspectionType: String,
        userName: String,
        mobileNumber: String,
        location: String,
        image: String,
        id: String
    ) {
        this.inspectionType = inspectionType
        this.userName = userName
        this.mobileNumber = mobileNumber
        this.location = location
        this.inspectorInspectionStatus = inspectorInspectionStatus
        this.image = image
        this.id = id
        isPopupShown=true
        checkRouteViewModel.checkRouteRequest(
            sessionToken = sharedPref.getString(Const.TOKEN),
            requestId = id
        )
        checkRouteObserver()
    }

    var isPopupShown = false // Add this flag to prevent multiple executions

    fun checkRouteObserver() {

        checkRouteViewModel.checkRouteSuccess.observe(this) { response ->
            if (response.status == true && isPopupShown==true ) { // Check the flag
//                && !isPopupShown
                isPopupShown = true // Set the flag to true
                CoroutineScope(Main).launch {
                    delay(1500) // Replace Timer with a coroutine-friendly delay
                    withContext(Main) {
                        // Run UI-related code
                        isPopupShown = false
                        dbHelper.clearDatabase()
                        requestDetailPopup(
                            inspectionType = inspectionType,
                            userName = userName,
                            mobileNumber = mobileNumber,
                            location = location,
                            inspectorInspectionStatus = inspectorInspectionStatus,
                            image = image,
                            id = id,
                            datatype = response.data.type
                        )
                    }
                }
            }
        }

        /*
                checkRouteViewModel.checkRouteSuccess.observe(this) { response ->
                    if (response.status == true) {
        //                showToast(response.message)
        //                datatype= response.data.type
        //                dialog!!.show()
                        Timer().schedule(1500){
                            CoroutineScope(Main).launch {
                                withContext(Main){
                                    //run UI related code
                                    //will be executed after the timeout
                                    dbHelper.clearDatabase()
                                    requestDetailPopup(
                                        inspectionType = inspectionType,
                                        userName = userName,
                                        mobileNumber = mobileNumber,
                                        location = location,
                                        inspectorInspectionStatus = inspectorInspectionStatus,
                                        image = image,
                                        id = id,
                                        datatype = response.data.type
                                    )
                                }
                            }

                        }


                    }
                }
        */

        // Observe the accept/reject API error LiveData
        checkRouteViewModel.apiError.observe(this) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe the accept/reject loading state LiveData
        checkRouteViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }


    /*


       fun requestDetailPopup(inspectorInspectionStatus: String, inspectionType: String, userName: String,
                              mobileNumber: String, location: String,image:String,id:String,datatype:Int) {
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
           val tvOnRoute = dialog!!.findViewById<TextView>(R.id.tvOnRoute)
           val tvRMakeReport = dialog!!.findViewById<TextView>(R.id.tvRMakeReport)
           val llOnRouteAndMakeReport = dialog!!.findViewById<LinearLayout>(R.id.llOnRouteAndMakeReport)
           val bottomsheetRateLL = dialog!!.findViewById<LinearLayout>(R.id.imgeLL)
           val llAcceptReject = dialog!!.findViewById<LinearLayout>(R.id.llAcceptReject)
           Log.d("requestDetailPopup", "id:--- $id ")
           Glide
               .with(requireActivity())
               .load(image)
               .centerCrop()
               .placeholder(R.drawable.ic_placeholder)
               .into(profile_image!!)

           if(inspectorInspectionStatus.equals("0")){
               llAcceptReject!!.visibility=View.VISIBLE
               llOnRouteAndMakeReport!!.visibility=View.GONE
               tvMakeReport!!.visibility=View.GONE
           }else{
               llAcceptReject!!.visibility=View.GONE
               llOnRouteAndMakeReport!!.visibility=View.VISIBLE
               tvMakeReport!!.visibility=View.GONE
           }

           if (datatype==1){
               llAcceptReject!!.visibility=View.GONE
               llOnRouteAndMakeReport!!.visibility=View.GONE
               tvMakeReport!!.visibility=View.VISIBLE
           }else {
               llAcceptReject!!.visibility=View.GONE
               llOnRouteAndMakeReport!!.visibility=View.VISIBLE
               tvMakeReport!!.visibility=View.GONE

           }



           tvInspectionType!!.text=inspectionType
           tvUserName!!.text=userName
           tvMobileNumber!!.text=mobileNumber
           tvLocation!!.text=location

           tvMakeReport!!.setOnClickListener {
   //          acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "", requestId = "")
               dialog!!.dismiss()
               val bundle = Bundle()
               bundle.putString("requestId", id)
               baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
           }

           tvRMakeReport!!.setOnClickListener {
   //          acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "", requestId = "")
               dialog!!.dismiss()
               val bundle = Bundle()
               bundle.putString("requestId", id)
               baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
           }

           tvAccept!!.setOnClickListener {
               acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "1", requestId = id)
               acceptRejectObserver()
               dialog!!.dismiss()
   //            val bundle = Bundle()
   //            bundle.putString("requestId", id)
   //            baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
           }

           tvReject!!.setOnClickListener {
               acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "2", requestId = id)
               acceptRejectObserver()
               dialog!!.dismiss()
           }

           tvOnRoute!!.setOnClickListener {
               dialog!!.dismiss()
               onRoute=false
               onRouteViewModel.onRouteRequest(sessionToken = sharedPref.getString(Const.TOKEN), requestId = id)
               OnRouteObserver()
           }

   //            ratingBar!!.rating=(completedAppointmentList.get(position).rating.toFloat())
   //            emailTV!!.setText(completedAppointmentList.get(position).email)
   //            ageTV!!.setText(completedAppointmentList.get(position).age)
   //            appointmentDateTV!!.setText(completedAppointmentList.get(position).formattedBookingDate)
           dialog!!.show()

       }


       */

    var dialogKey = false
    fun requestDetailPopup(
        inspectorInspectionStatus: String,
        inspectionType: String,
        userName: String,
        mobileNumber: String,
        location: String,
        image: String,
        id: String,
        datatype: Int
    ) {

        if (dialog != null && dialog!!.isShowing) {
            return  // Exit if the dialog is already being shown
        }

        dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_request_listing)

            val tvInspectionType = findViewById<TextView>(R.id.tvInspectionType)
            val profileImage = findViewById<CircleImageView>(R.id.profile_image)
            val tvUserName = findViewById<TextView>(R.id.tvUserName)
            val tvMobileNumber = findViewById<TextView>(R.id.tvMobileNumber)
            val tvLocation = findViewById<TextView>(R.id.tvLocation)
            val tvMakeReport = findViewById<TextView>(R.id.tvMakeReport)
            val tvAccept = findViewById<TextView>(R.id.tvAccept)
            val tvReject = findViewById<TextView>(R.id.tvReject)
            val tvOnRoute = findViewById<TextView>(R.id.tvOnRoute)
            val tvRMakeReport = findViewById<TextView>(R.id.tvRMakeReport)
            val llOnRouteAndMakeReport = findViewById<LinearLayout>(R.id.llOnRouteAndMakeReport)
            val llAcceptReject = findViewById<LinearLayout>(R.id.llAcceptReject)

            // Load profile image
            Glide.with(requireActivity())
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(profileImage!!)

            // Visibility and text setting based on conditions
//            when {
//                inspectorInspectionStatus == "0" -> {
//                    llAcceptReject!!.visibility = View.VISIBLE
//                    llOnRouteAndMakeReport!!.visibility = View.GONE
//                    tvMakeReport!!.visibility = View.GONE
//                }
//                datatype == 1 -> {
//                    llAcceptReject!!.visibility = View.GONE
//                    llOnRouteAndMakeReport!!.visibility = View.GONE
//                    tvMakeReport!!.visibility = View.VISIBLE
//                }
//                else -> {
//                    llAcceptReject!!.visibility = View.GONE
//                    llOnRouteAndMakeReport!!.visibility = View.VISIBLE
//                    tvMakeReport!!.visibility = View.GONE
//                }
//            }


            if (datatype == 1) {
                llAcceptReject!!.visibility = View.GONE
                llOnRouteAndMakeReport!!.visibility = View.GONE
                tvMakeReport!!.visibility = View.VISIBLE
            }
            else {
//                llAcceptReject!!.visibility=View.GONE
//                llOnRouteAndMakeReport!!.visibility=View.VISIBLE
//                tvMakeReport!!.visibility=View.GONE
                if (inspectorInspectionStatus.equals("0")) {
                    llAcceptReject!!.visibility = View.VISIBLE
                    llOnRouteAndMakeReport!!.visibility = View.GONE
                    tvMakeReport!!.visibility = View.GONE
                } else {
                    llAcceptReject!!.visibility = View.GONE
                    llOnRouteAndMakeReport!!.visibility = View.VISIBLE
                    tvMakeReport!!.visibility = View.GONE
                }
            }

            // Set data to views
            tvInspectionType!!.text = inspectionType
            tvUserName!!.text = userName
            tvMobileNumber!!.text = mobileNumber
            tvLocation!!.text = location

            // Button click listeners
            tvMakeReport!!.setOnClickListener {
                dialog!!.dismiss()
                val bundle = Bundle().apply { putString("requestId", id) }
//                baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container, bundle)
                findNavController().navigate(R.id.inspectionFormFragment, bundle)
            }

            tvRMakeReport!!.setOnClickListener {
                dialog!!.dismiss()
                dbHelper.clearDatabase()
                val bundle = Bundle().apply { putString("requestId", id) }
//                baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container, bundle)
                findNavController().navigate(R.id.inspectionFormFragment, bundle)
            }

            tvAccept!!.setOnClickListener {
                acceptRejectViewModel.chageRequestStatusRequest(
                    sessionToken = sharedPref.getString(
                        Const.TOKEN
                    ), type = "1", requestId = id
                )
                acceptRejectObserver()
                dialog!!.dismiss()
            }

            tvReject!!.setOnClickListener {
                acceptRejectViewModel.chageRequestStatusRequest(
                    sessionToken = sharedPref.getString(
                        Const.TOKEN
                    ), type = "2", requestId = id
                )
                acceptRejectObserver()
                dialog!!.dismiss()
            }

            tvOnRoute!!.setOnClickListener {
                dialog!!.dismiss()
                onRoute = false
                onRouteViewModel.onRouteRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    requestId = id
                )
                OnRouteObserver()
            }

            // Add a dismiss listener to nullify the dialog on dismissal
            setOnDismissListener {
                // Reset text fields
                tvInspectionType.text = ""
                tvUserName.text = ""
                tvMobileNumber.text = ""
                tvLocation.text = ""

                // Reset visibility of buttons and layouts
                llAcceptReject.visibility = View.GONE
                llOnRouteAndMakeReport.visibility = View.GONE
                tvMakeReport.visibility = View.GONE

                // Optionally clear the profile image
                profileImage.setImageResource(R.drawable.ic_placeholder) // Reset to placeholder

                dialog = null
            }

            show()
        }
    }

    fun homeviewObserver() {
        homeViewModel.homeSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                binding!!.profileimages.loadFromUrl(
                    Const.IMG_URL + it.homeData.profilePic,
                    R.drawable.ic_placeholder
                )
                binding!!.tUserName.text = it.homeData.userName
            }
        })

        homeViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }


    fun acceptRejectObserver() {
//        OnRouteViewModel
        // Observe the accept/reject success LiveData
        acceptRejectViewModel.acceptRejectSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                showToast(response.message)
                dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN), "")
                observer()
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

    var datatype = 0
    fun OnRouteObserver() {
        onRouteViewModel.onRouteSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
//                showToast(response.message)
//                if (dialog != null && dialog!!.isShowing) {
//                    dialog!!.dismiss()
////                    return  // Exit if the dialog is already being shown
//                }
                dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN), "")
                observer()
            }
        }

        // Observe the accept/reject API error LiveData
        onRouteViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe the accept/reject loading state LiveData
        onRouteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }

    fun observer() {
        dashboardViewModel?.dashboardResponseSuccess?.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
//                if (dialog != null && dialog!!.isShowing) {
//                    dialog!!.dismiss()
////                    return  // Exit if the dialog is already being shown
//                }
                setAdapter(it.data)
            }
        })

        dashboardViewModel?.apiError?.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        dashboardViewModel?.isLoading?.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


    }

    fun setCalendar() {
        /*Calender view */
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
//        val firstDayOfWeek = firstDayOfWeekFromLocale()
        val firstDayOfWeek = DayOfWeek.SUNDAY
        binding!!.calendarViewLeave.setup(startMonth, endMonth, firstDayOfWeek)
        binding!!.calendarViewLeave.scrollToMonth(currentMonth)

        binding!!.calendarViewLeave.monthScrollListener = { month ->

            val firstDate = month.weekDays.first().first().date
            val lastDate = month.weekDays.last().last().date
            Log.e("==>", "First date ${firstDate} ${lastDate}")
            val date = month.yearMonth
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM")
            val text = date.format(formatter)
            binding!!.tvMonthYear.text = text
            this.currentMonth = month.yearMonth
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayTextBinding.bind(view).exEightDayText

            init {
                textView.setOnClickListener {
                    val date = day.date
                    if (selectedDates.contains(date)) {
                        selectedDates.remove(date)
                    } else {
                        selectedDates.add(date)
                    }
                    binding!!.calendarViewLeave.notifyDayChanged(day)
                }
            }
        }

        binding!!.calendarViewLeave.apply {
            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    container.day = data
//                    bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
                    bindDate(
                        data.date,
                        container.textView,
                        data.position == DayPosition.MonthDate,
                        selectedDates
                    )
                }
            }
        }

        binding!!.ivCalendarForward.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isForwardButtonPressed = true
                    scrollForward()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isForwardButtonPressed = false
                }
            }
            true
        }

        binding!!.ivCalendarBack.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isBackwardButtonPressed = true
                    scrollBackward()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isBackwardButtonPressed = false
                }
            }
            true
        }


    }


    private fun bindDate(
        date: LocalDate,
        textView: TextView,
        isSelectable: Boolean,
        selectedDates: MutableList<LocalDate>
    ) {
        textView.text = date.dayOfMonth.toString()

        if (isSelectable) {
            if (selectedDate == date) {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.White))
                textView.setBackgroundResource(R.drawable.darkblue_rect)
            } else {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Black))
                textView.background = null
            }
        } else {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Black))
            textView.background = null
        }

        textView.setOnClickListener {
            if (isSelectable) {
                selectedDate = date

                @RequiresApi(Build.VERSION_CODES.O)
                fun formatDate(date: LocalDate): String {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    return date.format(formatter)
                }

                selectedDate?.let { formattedDate ->
                    selectedDate = LocalDate.parse(
                        formatDate(formattedDate),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                }
                Log.e("Selected Date", "Date...." + selectedDate)

                dashboardViewModel.dashboardRequest(
                    sharedPref.getString(Const.TOKEN),
                    selectedDate.toString()
                )
                observer()
//                if (selectedDate != null && selectedDate!!.isBefore(LocalDate.now())) {
//                    showToast("You selected a past date.")
//                } else {
//                    Log.e("tag", "Date...." + selectedDate)
                binding!!.calendarViewLeave.notifyCalendarChanged()
//                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollForward() {
        if (isForwardButtonPressed) {
            binding!!.calendarViewLeave.scrollToMonth(currentMonth.plusMonths(1))
            Handler().postDelayed({ scrollForward() }, 100) // Scroll every 100 milliseconds
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollBackward() {
        if (isBackwardButtonPressed) {
            binding!!.calendarViewLeave.scrollToMonth(currentMonth.minusMonths(1))
            Handler().postDelayed({ scrollBackward() }, 100) // Scroll every 100 milliseconds
        }
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.iv_notification -> {
                findNavController().navigate(R.id.inspectorNotificationFragment)
            }

            R.id.ivAdd -> {
                findNavController().navigate(R.id.detailFillFragment)
//              findNavController().navigate(R.id.addRequestFragment)
            }

            R.id.tvallRequest -> {
                findNavController().navigate(R.id.completedRequestsFragment)
            }
            R.id.iv_poweroff -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to logout?")

                builder.setPositiveButton("Logout") { dialog, _ ->
                    dialog.dismiss()
                    context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)

                    sharedPref.clearPref()

                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.setOnShowListener {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_blue))
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_blue))
                }
                alertDialog.show()
            }

        }
    }

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


}


/*
package com.poseidonapp.ui.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.CalendarDayTextBinding
import com.poseidonapp.databinding.FragmentDashboardBinding
import com.poseidonapp.inspector.form.InspectionFormFragment
import com.poseidonapp.model.Appointment.data
import com.poseidonapp.model.dashboard.Data
import com.poseidonapp.ui.adapter.AppointmentAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.acceptreject.AcceptRejectViewModel
import com.poseidonapp.viewmodel.checkroute.CheckRouteViewModel
import com.poseidonapp.viewmodel.dashboard.DashboardViewModel
import com.poseidonapp.viewmodel.onRoute.OnRouteViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.schedule

class DashboardFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentDashboardBinding?=null
    lateinit var calendarView: CalendarView
    val selectedDates = mutableListOf<LocalDate>()
    var isForwardButtonPressed = false
    var isBackwardButtonPressed = false
    var selectedDate: LocalDate? = LocalDate.now()
    lateinit var currentMonth: YearMonth
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val selectedRange = mutableSetOf<LocalDate>()

    lateinit var dashboardViewModel: DashboardViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentDashboardBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }


    private fun initUI() {
        binding!!.handleClick = this
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        acceptRejectViewModel = ViewModelProvider(this)[AcceptRejectViewModel::class.java]
        onRouteViewModel = ViewModelProvider(this)[OnRouteViewModel::class.java]
        checkRouteViewModel = ViewModelProvider(this)[CheckRouteViewModel::class.java]
        setCalendar()
        dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN),"")
        observer()
    }

    fun setAdapter(data: Data){
//        val appointmentList = listOf(
//            data("Meeting","17/01/2024", "9:30AM-10:30AM"),
//            data("Check-up", "18/01/2024", "10:00AM-11:00AM"),
//            data("Meeting", "19/01/2024", "11:00AM-12:00PM") ,
//            data("Check-up", "20/01/2024", "12:00AM-1:00PM"),
//            data("Meeting", "21/01/2024", "1:00AM-2:00PM") ,
//            data("Check-up", "22/01/2024", "2:00AM-3:00PM")
//        )
        binding!!.apply {
            if (data.requests.isEmpty()){
                rvAppointment.visibility=View.INVISIBLE
                tvNoDataFound.visibility=View.VISIBLE
            } else {
                rvAppointment.visibility=View.VISIBLE
                tvNoDataFound.visibility=View.INVISIBLE
                val appointmentAdapter = AppointmentAdapter(data.requests, requireContext(),this@DashboardFragment)
                rvAppointment.layoutManager = LinearLayoutManager(requireContext())
                rvAppointment.adapter = appointmentAdapter
            }
        }

    }

    var dialog: BottomSheetDialog? = null
    lateinit var acceptRejectViewModel: AcceptRejectViewModel
    lateinit var onRouteViewModel: OnRouteViewModel
    lateinit var checkRouteViewModel: CheckRouteViewModel

    var onRoute=false

    var inspectionType=""
    var userName=""
    var mobileNumber=""
    var location=""
    var inspectorInspectionStatus=""
    var image=""
    var id=""

    fun onRouteCheck(inspectorInspectionStatus: String, inspectionType: String, userName: String, mobileNumber: String, location: String,image:String,id:String){
        this.inspectionType=inspectionType
        this.userName=userName
        this.mobileNumber=mobileNumber
        this.location=location
        this.inspectorInspectionStatus=inspectorInspectionStatus
        this.image=image
        this.id=id
        checkRouteViewModel.checkRouteRequest(sessionToken = sharedPref.getString(Const.TOKEN), requestId = id)
        checkRouteObserver()
    }

    fun checkRouteObserver(){

        checkRouteViewModel.checkRouteSuccess.observe(this) { response ->
            if (response.status == true) {
//                showToast(response.message)
//                datatype= response.data.type
//                dialog!!.show()
                Timer().schedule(1500){
                    CoroutineScope(Main).launch {
                        withContext(Main){
                            //run UI related code
                            //will be executed after the timeout
                            requestDetailPopup(
                                inspectionType = inspectionType,
                                userName = userName,
                                mobileNumber = mobileNumber,
                                location = location,
                                inspectorInspectionStatus = inspectorInspectionStatus,
                                image = image,
                                id = id,
                                datatype = response.data.type
                            )
                        }
                    }

                }


            }
        }

        // Observe the accept/reject API error LiveData
        checkRouteViewModel.apiError.observe(this) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe the accept/reject loading state LiveData
        checkRouteViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }




 */
/*


    fun requestDetailPopup(inspectorInspectionStatus: String, inspectionType: String, userName: String,
                           mobileNumber: String, location: String,image:String,id:String,datatype:Int) {
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
        val tvOnRoute = dialog!!.findViewById<TextView>(R.id.tvOnRoute)
        val tvRMakeReport = dialog!!.findViewById<TextView>(R.id.tvRMakeReport)
        val llOnRouteAndMakeReport = dialog!!.findViewById<LinearLayout>(R.id.llOnRouteAndMakeReport)
        val bottomsheetRateLL = dialog!!.findViewById<LinearLayout>(R.id.imgeLL)
        val llAcceptReject = dialog!!.findViewById<LinearLayout>(R.id.llAcceptReject)
        Log.d("requestDetailPopup", "id:--- $id ")
        Glide
            .with(requireActivity())
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .into(profile_image!!)

        if(inspectorInspectionStatus.equals("0")){
            llAcceptReject!!.visibility=View.VISIBLE
            llOnRouteAndMakeReport!!.visibility=View.GONE
            tvMakeReport!!.visibility=View.GONE
        }else{
            llAcceptReject!!.visibility=View.GONE
            llOnRouteAndMakeReport!!.visibility=View.VISIBLE
            tvMakeReport!!.visibility=View.GONE
        }

        if (datatype==1){
            llAcceptReject!!.visibility=View.GONE
            llOnRouteAndMakeReport!!.visibility=View.GONE
            tvMakeReport!!.visibility=View.VISIBLE
        }else {
            llAcceptReject!!.visibility=View.GONE
            llOnRouteAndMakeReport!!.visibility=View.VISIBLE
            tvMakeReport!!.visibility=View.GONE

        }



        tvInspectionType!!.text=inspectionType
        tvUserName!!.text=userName
        tvMobileNumber!!.text=mobileNumber
        tvLocation!!.text=location

        tvMakeReport!!.setOnClickListener {
//            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "", requestId = "")
            dialog!!.dismiss()
            val bundle = Bundle()
            bundle.putString("requestId", id)
            baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
        }

        tvRMakeReport!!.setOnClickListener {
//            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "", requestId = "")
            dialog!!.dismiss()
            val bundle = Bundle()
            bundle.putString("requestId", id)
            baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
        }

        tvAccept!!.setOnClickListener {
            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "1", requestId = id)
            acceptRejectObserver()
            dialog!!.dismiss()
//            val bundle = Bundle()
//            bundle.putString("requestId", id)
//            baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container,bundle)
        }

        tvReject!!.setOnClickListener {
            acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "2", requestId = id)
            acceptRejectObserver()
            dialog!!.dismiss()
        }

        tvOnRoute!!.setOnClickListener {
            dialog!!.dismiss()
            onRoute=false
            onRouteViewModel.onRouteRequest(sessionToken = sharedPref.getString(Const.TOKEN), requestId = id)
            OnRouteObserver()
        }

//            ratingBar!!.rating=(completedAppointmentList.get(position).rating.toFloat())
//            emailTV!!.setText(completedAppointmentList.get(position).email)
//            ageTV!!.setText(completedAppointmentList.get(position).age)
//            appointmentDateTV!!.setText(completedAppointmentList.get(position).formattedBookingDate)
        dialog!!.show()

    }


    *//*


    var dialogKey=false
    fun requestDetailPopup(
        inspectorInspectionStatus: String,
        inspectionType: String,
        userName: String,
        mobileNumber: String,
        location: String,
        image: String,
        id: String,
        datatype: Int
    ) {
        if (dialog != null && dialog!!.isShowing) {
            return  // Exit if the dialog is already being shown
        }

        dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_request_listing)

            val tvInspectionType = findViewById<TextView>(R.id.tvInspectionType)
            val profileImage = findViewById<CircleImageView>(R.id.profile_image)
            val tvUserName = findViewById<TextView>(R.id.tvUserName)
            val tvMobileNumber = findViewById<TextView>(R.id.tvMobileNumber)
            val tvLocation = findViewById<TextView>(R.id.tvLocation)
            val tvMakeReport = findViewById<TextView>(R.id.tvMakeReport)
            val tvAccept = findViewById<TextView>(R.id.tvAccept)
            val tvReject = findViewById<TextView>(R.id.tvReject)
            val tvOnRoute = findViewById<TextView>(R.id.tvOnRoute)
            val tvRMakeReport = findViewById<TextView>(R.id.tvRMakeReport)
            val llOnRouteAndMakeReport = findViewById<LinearLayout>(R.id.llOnRouteAndMakeReport)
            val llAcceptReject = findViewById<LinearLayout>(R.id.llAcceptReject)

            // Load profile image
            Glide.with(requireActivity())
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(profileImage!!)

            // Visibility and text setting based on conditions
//            when {
//                inspectorInspectionStatus == "0" -> {
//                    llAcceptReject!!.visibility = View.VISIBLE
//                    llOnRouteAndMakeReport!!.visibility = View.GONE
//                    tvMakeReport!!.visibility = View.GONE
//                }
//                datatype == 1 -> {
//                    llAcceptReject!!.visibility = View.GONE
//                    llOnRouteAndMakeReport!!.visibility = View.GONE
//                    tvMakeReport!!.visibility = View.VISIBLE
//                }
//                else -> {
//                    llAcceptReject!!.visibility = View.GONE
//                    llOnRouteAndMakeReport!!.visibility = View.VISIBLE
//                    tvMakeReport!!.visibility = View.GONE
//                }
//            }




            if (datatype==1){
                llAcceptReject!!.visibility=View.GONE
                llOnRouteAndMakeReport!!.visibility=View.GONE
                tvMakeReport!!.visibility=View.VISIBLE
            }else {
//                llAcceptReject!!.visibility=View.GONE
//                llOnRouteAndMakeReport!!.visibility=View.VISIBLE
//                tvMakeReport!!.visibility=View.GONE
                if(inspectorInspectionStatus.equals("0")){
                    llAcceptReject!!.visibility=View.VISIBLE
                    llOnRouteAndMakeReport!!.visibility=View.GONE
                    tvMakeReport!!.visibility=View.GONE
                }else{
                    llAcceptReject!!.visibility=View.GONE
                    llOnRouteAndMakeReport!!.visibility=View.VISIBLE
                    tvMakeReport!!.visibility=View.GONE
                }
            }

            // Set data to views
            tvInspectionType!!.text = inspectionType
            tvUserName!!.text = userName
            tvMobileNumber!!.text = mobileNumber
            tvLocation!!.text = location

            // Button click listeners
            tvMakeReport!!.setOnClickListener {
                dialog!!.dismiss()
                val bundle = Bundle().apply { putString("requestId", id) }
//                baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container, bundle)
                findNavController().navigate(R.id.inspectionFormFragment,bundle)
            }

            tvRMakeReport!!.setOnClickListener {
                dialog!!.dismiss()
                val bundle = Bundle().apply { putString("requestId", id) }
//                baseActivity!!.replaceFragWithArgs(InspectionFormFragment(), R.id.frame_container, bundle)
                findNavController().navigate(R.id.inspectionFormFragment,bundle)
            }

            tvAccept!!.setOnClickListener {
                acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "1", requestId = id)
                acceptRejectObserver()
                dialog!!.dismiss()
            }

            tvReject!!.setOnClickListener {
                acceptRejectViewModel.chageRequestStatusRequest(sessionToken = sharedPref.getString(Const.TOKEN), type = "2", requestId = id)
                acceptRejectObserver()
                dialog!!.dismiss()
            }

            tvOnRoute!!.setOnClickListener {
                dialog!!.dismiss()
                onRoute = false
                onRouteViewModel.onRouteRequest(sessionToken = sharedPref.getString(Const.TOKEN), requestId = id)
                OnRouteObserver()
            }

            // Add a dismiss listener to nullify the dialog on dismissal
            setOnDismissListener {
                // Reset text fields
                tvInspectionType.text = ""
                tvUserName.text = ""
                tvMobileNumber.text = ""
                tvLocation.text = ""

                // Reset visibility of buttons and layouts
                llAcceptReject.visibility = View.GONE
                llOnRouteAndMakeReport.visibility = View.GONE
                tvMakeReport.visibility = View.GONE

                // Optionally clear the profile image
                profileImage.setImageResource(R.drawable.ic_placeholder) // Reset to placeholder

                dialog = null
            }

            show()
        }
    }



    fun acceptRejectObserver(){
//        OnRouteViewModel
        // Observe the accept/reject success LiveData
        acceptRejectViewModel.acceptRejectSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                showToast(response.message)
                dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN),selectedDate.toString())
                observer()
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
var datatype=0
    fun OnRouteObserver(){
        onRouteViewModel.onRouteSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                showToast(response.message)

                    dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN),selectedDate.toString())
                    observer()


            }
        }

        // Observe the accept/reject API error LiveData
        onRouteViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe the accept/reject loading state LiveData
        onRouteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }

    fun observer(){
        dashboardViewModel?.dashboardResponseSuccess?.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                setAdapter(it.data)
            }
        })

        dashboardViewModel?.apiError?.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        dashboardViewModel?.isLoading?.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


    }

    fun setCalendar(){
        */
/*Calender view *//*

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
//        val firstDayOfWeek = firstDayOfWeekFromLocale()
        val firstDayOfWeek = DayOfWeek.SUNDAY
        binding!!.calendarViewLeave.setup(startMonth, endMonth, firstDayOfWeek)
        binding!!.calendarViewLeave.scrollToMonth(currentMonth)

        binding!!.calendarViewLeave.monthScrollListener = { month ->

            val firstDate = month.weekDays.first().first().date
            val lastDate = month.weekDays.last().last().date
            Log.e("==>", "First date ${firstDate} ${lastDate}")
            val date = month.yearMonth
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM")
            val text = date.format(formatter)
            binding!!.tvMonthYear.text = text
            this.currentMonth = month.yearMonth
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayTextBinding.bind(view).exEightDayText

            init {
                textView.setOnClickListener {
                    val date = day.date
                    if (selectedDates.contains(date)) {
                        selectedDates.remove(date)
                    } else {
                        selectedDates.add(date)
                    }
                    binding!!.calendarViewLeave.notifyDayChanged(day)
                }
            }
        }

        binding!!.calendarViewLeave.apply {
            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    container.day = data
//                    bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
                    bindDate(data.date,container.textView, data.position == DayPosition.MonthDate,selectedDates)
                }
            }
        }

        binding!!.ivCalendarForward.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isForwardButtonPressed = true
                    scrollForward()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isForwardButtonPressed = false
                }
            }
            true
        }

        binding!!.ivCalendarBack.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isBackwardButtonPressed = true
                    scrollBackward()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isBackwardButtonPressed = false
                }
            }
            true
        }


    }


    private fun bindDate(
        date: LocalDate,
        textView: TextView,
        isSelectable: Boolean,
        selectedDates: MutableList<LocalDate>
    ) {
        textView.text = date.dayOfMonth.toString()

        if (isSelectable) {
            if (selectedDate == date) {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textView.setBackgroundResource(R.drawable.darkblue_rect)
            } else {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                textView.background = null
            }
        } else {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            textView.background = null
        }

        textView.setOnClickListener {
            if (isSelectable) {
                selectedDate = date

                @RequiresApi(Build.VERSION_CODES.O)
                fun formatDate(date: LocalDate): String {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    return date.format(formatter)
                }

                selectedDate?.let { formattedDate ->
                    selectedDate = LocalDate.parse(formatDate(formattedDate), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }
                Log.e("Selected Date", "Date...." + selectedDate)

                dashboardViewModel.dashboardRequest(sharedPref.getString(Const.TOKEN),selectedDate.toString())
                observer()
//                if (selectedDate != null && selectedDate!!.isBefore(LocalDate.now())) {
//                    showToast("You selected a past date.")
//                } else {
//                    Log.e("tag", "Date...." + selectedDate)
                binding!!.calendarViewLeave.notifyCalendarChanged()
//                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollForward() {
        if (isForwardButtonPressed) {
            binding!!.calendarViewLeave.scrollToMonth(currentMonth.plusMonths(1))
            Handler().postDelayed({ scrollForward() }, 100) // Scroll every 100 milliseconds
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollBackward() {
        if (isBackwardButtonPressed) {
            binding!!.calendarViewLeave.scrollToMonth(currentMonth.minusMonths(1))
            Handler().postDelayed({ scrollBackward()}, 100) // Scroll every 100 milliseconds
        }
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.iv_notification -> {
                findNavController().navigate(R.id.inspectorNotificationFragment)
            }
            R.id.ic_add-> {
                findNavController().navigate(R.id.detailFillFragment)
//              findNavController().navigate(R.id.addRequestFragment)
            }
            R.id.tvallRequest->{
                findNavController().navigate(R.id.completedRequestsFragment)
            }
        }
    }

    private fun sessionExpiredPopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.popup_token_expired)
        dialog.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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


*/


//
//
//
//
//
//package com.poseidonapp.ui.fragments
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.Dialog
//import android.content.*
//import android.content.pm.PackageManager
//import android.content.res.Configuration
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.graphics.PorterDuff
//import android.location.Location
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.provider.MediaStore
//import android.provider.Settings
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.google.android.gms.location.*
//import com.kizitonwose.calendar.core.CalendarDay
//import com.kizitonwose.calendar.core.DayPosition
//import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
//import com.kizitonwose.calendar.core.yearMonth
//import com.kizitonwose.calendar.view.CalendarView
//import com.kizitonwose.calendar.view.MonthDayBinder
//import com.kizitonwose.calendar.view.ViewContainer
//import com.poseidonapp.R
//import com.poseidonapp.database.DatabaseHelper
//import com.poseidonapp.databinding.CalendarDayTextBinding
//import com.poseidonapp.databinding.FragmentDashboardBinding
//import com.poseidonapp.inspector.home.InspectorHomeFragment
//import com.poseidonapp.model.addexpense.GridItem
//import com.poseidonapp.model.schedules.MeetingsItem
//import com.poseidonapp.ui.activities.MainActivity
//import com.poseidonapp.ui.adapter.DashboardGridItemAdapter
//import com.poseidonapp.ui.base.BaseAdapter
//import com.poseidonapp.ui.base.BaseFragment
//import com.poseidonapp.ui.extensions.*
//import com.poseidonapp.utils.Const
//import com.poseidonapp.utils.HandleClickListener
//import com.poseidonapp.viewmodel.AddExpenseViewModel
//import com.poseidonapp.viewmodel.ClockInListViewModel
//import com.poseidonapp.viewmodel.GetLeavesViewModel
//import com.poseidonapp.viewmodel.HomeViewModel
//import com.poseidonapp.viewmodel.applyleaves.ApplyLeavesViewModel
//import com.poseidonapp.viewmodel.createproject.CreateProjectViewModel
//import com.poseidonapp.viewmodel.dayclockin.DayClockInViewModel
//import com.poseidonapp.viewmodel.logout.LogoutViewModel
//import com.poseidonapp.viewmodel.schedules.SchedulesViewModel
//import com.skydoves.powerspinner.PowerSpinnerView
//import de.hdodenhof.circleimageview.CircleImageView
//import org.json.JSONArray
//import java.time.LocalDate
//import java.time.YearMonth
//import java.time.format.DateTimeFormatter
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//
//class DashboardFragment : BaseFragment(), BaseAdapter.OnItemClickListener, HandleClickListener {
//
//    var binding: FragmentDashboardBinding? = null
//    private var adapter: DashboardGridItemAdapter? = null
//    lateinit var homeViewModel: HomeViewModel
//    lateinit var clockInListViewModel: ClockInListViewModel
//    lateinit var addExpenseViewModel: AddExpenseViewModel
//    lateinit var logoutViewModel: LogoutViewModel
//    lateinit var dayClockInViewModel: DayClockInViewModel
//    lateinit var createProjectViewModel: CreateProjectViewModel
//    lateinit var applyLeavesViewModel: ApplyLeavesViewModel
//    lateinit var schedulesViewModel: SchedulesViewModel
//    lateinit var getLeavesViewModel: GetLeavesViewModel
//    val leaveTypeArray = arrayOf("Unpaid", "Paid")
//    val selectTypeArray = arrayOf("Yes", "No")
//    private var count: Int = 0
//
//    var statesArray = arrayOf(
//        "Alabama",
//        "Alaska",
//        "Arizona",
//        "Arkansas",
//        "California",
//        "Colorado",
//        "Connecticut",
//        "Delaware",
//        "Florida",
//        "Georgia",
//        "Hawaii",
//        "Idaho",
//        "Illinois",
//        "Indiana",
//        "Iowa",
//        "Kansas",
//        "Kentucky",
//        "Louisiana",
//        "Maine",
//        "Maryland",
//        "Massachusetts",
//        "Michigan",
//        "Minnesota",
//        "Mississippi",
//        "Missouri",
//        "Montana",
//        "Nebraska",
//        "Nevada",
//        "New Hampshire",
//        "New Jersey",
//        "New Mexico",
//        "New York",
//        "North Carolina",
//        "North Dakota",
//        "Ohio",
//        "Oklahoma",
//        "Oregon",
//        "Pennsylvania",
//        "Rhode Island",
//        "South Carolina",
//        "South Dakota",
//        "Tennessee",
//        "Texas",
//        "Utah",
//        "Vermont",
//        "Virginia",
//        "Washington",
//        "West Virginia",
//        "Wisconsin",
//        "Wyoming"
//    )
//
//
//
//    val REQUEST_CODE = 200
//
//    var gridItems: List<GridItem>? = null
//
//    lateinit var projectNameList: ArrayList<String>
//    lateinit var categoryList: ArrayList<String>
//    lateinit var stateList: ArrayList<String>
//    lateinit var datesList: ArrayList<String>
////     lateinit var image: Bitmap
//    var projectName: String = ""
//    var selectType: String = ""
//    var selectedCategory: String = ""
//    var reimbursement: String = ""
//    var imageBase64: String = ""
//    var latitude: String = ""
//    var longitude: String = ""
//    var selectState: String = ""
//    var selectedLeaveType: String = ""
//    var stats: Int = 0
//    var punchData: Long = 0
//    private val handler = Handler()
//    var progress = 0
//    lateinit var calendarView: CalendarView
//    lateinit var currentMonth: YearMonth
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private val today = LocalDate.now()
//
//    lateinit var dates: ArrayList<String>
//    lateinit var leavedates: ArrayList<String>
//    var meetings = java.util.ArrayList<MeetingsItem>()
//
//    private var currentLocation: Location? = null
//
//    val mHandler = Handler()
//    private var selectedDatesOfSchedule = ArrayList<LocalDate>()
//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                Log.i("Permission: ", "Granted")
//                captureImage()
//            } else {
//                Log.i("Permission: ", "Denied")
//            }
//        }
//
//    private lateinit var mFusedLocationClient: FusedLocationProviderClient
//    private val permissionId = 2
//    private var selectedDates = ArrayList<LocalDate>()
//    private var datesOfLeaveas = ArrayList<LocalDate>()
//
//    val permission = Manifest.permission.ACCESS_FINE_LOCATION
//
//    /// location updates
//    private val REQUEST_CODEE = 123
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    var locationRequest: LocationRequest? = null
//    private lateinit var locationCallback: LocationCallback
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//
//        }
//    }
//
//    companion object{
//        val REQUEST_CODE_CAMERA1 = 1
//        val REQUEST_CODE_CAMERA2 = 2
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        if (binding == null)
//            binding = FragmentDashboardBinding.inflate(inflater, container, false)
//        return binding!!.root
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initUI()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
//        textView.text = date.dayOfMonth.toString()
//        if (isSelectable) {
//            when {
//                selectedDatesOfSchedule.contains(date) -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.darkblue_rect)
//                }
//                today == date -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.red_rect)
////                    textView.background = null
//                }
//                else -> {
//                    textView.setTextColor(resources.getColor(R.color.Black))
//                    textView.background = null
//                }
//            }
//        } else {
//            textView.setTextColor(resources.getColor(R.color.grey))
//            textView.background = null
//        }
//    }
//
//
//    private fun checkAndRequestLocationPermissions() {
//        val permission = Manifest.permission.ACCESS_FINE_LOCATION
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                permission
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
////            startLocationUpdates()
////            fusedLocation()
//            checkAndEnableLocationServices()
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), REQUEST_CODEE)
//        }
//    }
//
//
//    private fun checkAndEnableLocationServices() {
//        val locationMode: Int =
//            Settings.Secure.getInt(
//                baseActivity!!.applicationContext.contentResolver,
//                Settings.Secure.LOCATION_MODE,
//                Settings.Secure.LOCATION_MODE_OFF
//            )
//
//        if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
//            // Location services are off, prompt the user to enable them
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
//        } else {
//            // Location services are already enabled, get the current location
//            startLocationUpdates()
//            fusedLocation()
//        }
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_CODEE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates()
//            } else {
//                // Permission denied, handle accordingly
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun startLocationUpdates() {
//        fusedLocationClient.requestLocationUpdates(locationRequest!!, locationCallback, null)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopLocationUpdates()
//    }
//
//    private fun stopLocationUpdates() {
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onStart() {
//        super.onStart()
//        fusedLocation()
//        startLocationUpdates()
//        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), filterDate = today.toString())
//        clockInListViewModel.clockInListRequest(
//            sharedPref.getString(Const.TOKEN),
//            lat = sharedPref.getString(Const.latitude),
//            long = sharedPref.getString(Const.longitude)
//        )
//    }
//
//    fun fusedLocation() {
//
//        locationRequest = LocationRequest.create().apply {
//            interval = 5
//            fastestInterval = 5
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                p0.lastLocation.let { location ->
//                    if (location != null) {
//                        latitude = location.latitude.toString()
//                        longitude = location.longitude.toString()
//                        sharedPref.setString(Const.latitude, latitude)
//                        sharedPref.setString(Const.longitude, longitude)
//                    }
//
//                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
//                }
//            }
//        }
//
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun initUI() {
//        if (sharedPref.getLanguageSelected() == "DARK") {
//            changeButtonColour()
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//
//        if (sharedPref.getLanguageSelected() == "LIGHT") {
//            changeButtonColor()
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//
//        }
//
//        if (sharedPref.getLanguageSelected() == "DARK") {
//            changeButtonColour()
////
//            val color = Color.WHITE // Replace with the color you want
//            binding!!.logoIV.setColorFilter(color, PorterDuff.Mode.SRC_IN)
//
//            val logout = Color.WHITE
//            binding!!.ivLogout.setColorFilter(logout, PorterDuff.Mode.SRC_IN)
//
//        }
//
//        if (sharedPref.getLanguageSelected() == "LIGHT") {
//            changeButtonColor()
////            val color = R.drawable.posidone_logo
//            binding!!.logoIV.setImageResource(R.drawable.posidone_logo)
//
////            val color = Color.BLUE // Replace with the color you want
////            binding!!.logoIV.setColorFilter(color, PorterDuff.Mode.SRC_IN)
//
//            val logout = R.color.dark_blue
//            binding!!.ivLogout.setColorFilter(logout, PorterDuff.Mode.SRC_IN)
//
//            val ivforward = R.color.dark_blue
//            binding!!.ivforward.setColorFilter(ivforward, PorterDuff.Mode.SRC_IN)
//        }
//
//        currentMonth = YearMonth.now()
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        binding!!.handleClick = this
//        projectNameList = ArrayList()
//        categoryList = ArrayList()
//        stateList = ArrayList()
//        datesList = ArrayList()
//        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
//        clockInListViewModel = ViewModelProvider(this)[ClockInListViewModel::class.java]
//        addExpenseViewModel = ViewModelProvider(this)[AddExpenseViewModel::class.java]
//        logoutViewModel = ViewModelProvider(this)[LogoutViewModel::class.java]
//        dayClockInViewModel = ViewModelProvider(this)[DayClockInViewModel::class.java]
//        createProjectViewModel = ViewModelProvider(this)[CreateProjectViewModel::class.java]
//        applyLeavesViewModel = ViewModelProvider(this)[ApplyLeavesViewModel::class.java]
//        schedulesViewModel = ViewModelProvider(this)[SchedulesViewModel::class.java]
//        getLeavesViewModel = ViewModelProvider(this)[GetLeavesViewModel::class.java]
//        fusedLocation()
//        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
//        getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
//
//
//        clockInListViewModel.clockInListRequest(
//            sharedPref.getString(Const.TOKEN),
//            sharedPref.getString(Const.latitude),
//            sharedPref.getString(Const.longitude)
//        )
//        schedulesViewModel.scheduleRequest(sharedPref.getString(Const.TOKEN), "")
//        setAdapter()
//
//        if (sharedPref.getBoolean(Const.SWITCH) == true) {
//            timerRun()
//            binding!!.switchStartShift.isChecked = true
//        } else {
//            binding!!.switchStartShift.isChecked = false
//        }
//
//        if (punchData.toInt() != 0) {
//            progress = 0
//            binding!!.switchStartShift.isChecked = false
//        } else {
//            binding!!.switchStartShift.isChecked = true
//        }
//
//        binding!!.switchStartShift.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
//            // Handle the switch state change here
//            if (binding!!.switchStartShift.isPressed || binding!!.switchStartShift.isSelected) {
//                if (isChecked) {
//                    sharedPref.setString(Const.stats, "0")
//                    binding!!.switchStartShift.isChecked = true
//                    fusedLocation()
//                    cameraPermissions()
//                } else {
//                    binding!!.switchStartShift.isChecked = false
//                    injuredPopup()
//                }
//            } else {
//
//            }
//
//        })
//
//        observer()
//
//
//    }
//
//    fun setAdapter() {
//        if (sharedPref.getString(Const.ROLE) == "6" || sharedPref.getString(Const.ROLE) == "7") {
//            gridItems = listOf(
//                GridItem(R.drawable.ic_expense, "Expense"),
//                GridItem(R.drawable.ic_vacation, "Vacations"),
//                GridItem(R.drawable.ic_calendar, "Schedule"),
//                GridItem(R.drawable.ic_schedule_requests, "Schedule Requests"),
//                GridItem(R.drawable.ic_create_project, "Create Project"),
//                GridItem(R.drawable.ic_chat, "Ask to admin"),
//                GridItem(R.drawable.ic_timesheet, "Time Sheet"),
//                GridItem(R.drawable.ic_safetymeetings, "Safety Meetings"),
//                GridItem(R.drawable.ic_installation, "Installation")
//            )
//        } else if (sharedPref.getString(Const.ROLE) == "4") {
//            gridItems = listOf(
//                GridItem(R.drawable.ic_work_order, "Work Order"),
//                GridItem(R.drawable.ic_expense, "Expense"),
//                GridItem(R.drawable.ic_vacation, "Vacations"),
//                GridItem(R.drawable.ic_calendar, "Schedule"),
//                GridItem(R.drawable.ic_schedule_requests, "Schedule Requests"),
//                GridItem(R.drawable.ic_create_project, "Create Project"),
//                GridItem(R.drawable.ic_chat, "Ask to admin"),
//                GridItem(R.drawable.ic_timesheet, "Time Sheet"),
//                GridItem(R.drawable.ic_safetymeetings, "Safety Meetings"),
//            )
//        } else {
//            gridItems = listOf(
//                GridItem(R.drawable.ic_work_order, "Work Order"),
//                GridItem(R.drawable.ic_inspection, "Inspection"),
//                GridItem(R.drawable.ic_expense, "Expense"),
//                GridItem(R.drawable.ic_vacation, "Vacations"),
//                GridItem(R.drawable.ic_calendar, "Schedule"),
//                GridItem(R.drawable.ic_schedule_requests, "Schedule Requests"),
//                GridItem(R.drawable.ic_create_project, "Create Project"),
//                GridItem(R.drawable.ic_chat, "Ask to admin"),
//                GridItem(R.drawable.ic_timesheet, "Time Sheet"),
//                GridItem(R.drawable.ic_safetymeetings, "Safety Meetings"),
//            )
//        }
//
//        adapter = DashboardGridItemAdapter(baseActivity!!, gridItems!!)
//        binding!!.servicRV.adapter = adapter
//        adapter!!.setOnItemClickListener(this)
//    }
//
//
//
//    fun cameraPermissions() {
//        when {
//            ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                captureImage()
//            }
//
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                requireActivity(),
//                Manifest.permission.CAMERA
//            ) -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.CAMERA
//                )
//            }
//
//            else -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.CAMERA
//                )
//            }
//        }
//    }
//
//    fun captureImage() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//        if (sharedPref.getString(Const.stats) == "0") {
//            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA1)
//        } else if (sharedPref.getString(Const.stats) == "1") {
//            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA2)
//        }
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onItemClick(vararg itemData: Any) {
//        val pos = itemData[0] as Int
//        val type = itemData[1] as Int
//        when (type) {
//            Const.Item_Click -> {
//                if (sharedPref.getString(Const.ROLE) == "6" || sharedPref.getString(Const.ROLE) == "7") {
//                    gridItems = listOf(
//                        GridItem(R.drawable.ic_expense, "Expense"),
//                        GridItem(R.drawable.ic_vacation, "Vacations"),
//                        GridItem(R.drawable.ic_calendar, "Schedule"),
//                        GridItem(R.drawable.ic_schedule_requests, "Schedule Requests"),
//                        GridItem(R.drawable.ic_create_project, "Create Project"),
//                        GridItem(R.drawable.ic_chat, "Ask to admin"),
//                        GridItem(R.drawable.ic_timesheet, "Time Sheet"),
//                        GridItem(R.drawable.ic_safetymeetings, "Safety Meetings"),
//                        GridItem(R.drawable.ic_installation, "Installation")
//                    )
//                    when (pos) {
//                        0 -> {
//                            sharedPref.setString(Const.stats, "1")
//                            cameraPermissions()
//                        }
//                        1 -> {
//                            selectMultipleDates()
//                        }
//                        2 -> {
//                            scheduleCalendar()
//                        }
//                        3 -> {
//                            baseActivity!!.replaceFragment(
//                                RequestDetailFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        4 -> {
//                            addProjectPopup()
//                        }
//                        5 -> {
//                            baseActivity!!.replaceFragment(
//                                SubjectToDiscussFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        6 -> {
//                            baseActivity!!.replaceFragment(
//                                TimeSheetFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        7 -> {
//                            baseActivity!!.replaceFragment(
//                                SafetyMeetingsFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        8 -> {
//                            baseActivity!!.replaceFragment(
//                                InstallationFragment(),
//                                R.id.frame_container
//                            )
//                        }
//
//                    }
//
//
//                } else if (sharedPref.getString(Const.ROLE) == "4") {
//                    gridItems = listOf(
//                        GridItem(R.drawable.ic_work_order, "Work Order"),
////                GridItem(R.drawable.ic_inspection, "Inspection"),
//                        GridItem(R.drawable.ic_expense, "Expense"),
//                        GridItem(R.drawable.ic_vacation, "Vacations"),
//                        GridItem(R.drawable.ic_calendar, "Schedule"),
//                        GridItem(R.drawable.ic_schedule_requests, "Schedule Requests"),
//                        GridItem(R.drawable.ic_create_project, "Create Project"),
//                        GridItem(R.drawable.ic_chat, "Ask to admin"),
//                        GridItem(R.drawable.ic_timesheet, "Time Sheet"),
//                        GridItem(R.drawable.ic_safetymeetings, "Safety Meetings"),
//                    )
//                    when (pos) {
//                        0 -> {
////                            val intent = Intent(MainActivity, WorkInputActivity::class.java)
////                            startActivity(intent)
//
//                          /*  val intent = Intent (requireActivity(), WorkInputActivity::class.java)
//                            requireActivity().startActivity(intent)*/
//
//                            /*baseActivity!!.replaceFragment(
//                                WorkInputFragment(),
//                                R.id.frame_container
//                            )*/
//                            baseActivity!!.replaceFragment(
//                                EmergencyRequestFragment(),
//                                R.id.frame_container
//                            )
//
//                        }
//                        1 -> {
//                            sharedPref.setString(Const.stats, "1")
//                            cameraPermissions()
//                        }
//                        2 -> {
//                            selectMultipleDates()
//                        }
//                        3 -> {
//                            scheduleCalendar()
//                        }
//                        4 -> {
//                            baseActivity!!.replaceFragment(
//                                RequestDetailFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        5 -> {
//                            addProjectPopup()
//                        }
//                        6 -> {
//                            baseActivity!!.replaceFragment(
//                                SubjectToDiscussFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        7 -> {
//                            baseActivity!!.replaceFragment(
//                                TimeSheetFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        8 -> {
//                            baseActivity!!.replaceFragment(
//                                SafetyMeetingsFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                    }
//                } else {
//                    gridItems = listOf(
//                        GridItem(R.drawable.ic_work_order, "Work Order"),
//                        GridItem(R.drawable.ic_inspection, "Inspection"),
//                        GridItem(R.drawable.ic_expense, "Expense"),
//                        GridItem(R.drawable.ic_vacation, "Vacations"),
//                        GridItem(R.drawable.ic_calendar, "Schedule"),
//                        GridItem(R.drawable.ic_schedule_requests, "Schedule Requests"),
//                        GridItem(R.drawable.ic_create_project, "Create Project"),
//                        GridItem(R.drawable.ic_chat, "Ask to admin"),
//                        GridItem(R.drawable.ic_timesheet, "Time Sheet"),
//                        GridItem(R.drawable.ic_safetymeetings, "Safety Meetings"),
//                    )
//                    when (pos) {
//                        0 -> {
////                            val intent = Intent(requireActivity(), WorkInputActivity::class.java)
////                            requireActivity().startActivity(intent)
//                          /*  baseActivity!!.replaceFragment(
//                                WorkInputFragment(),
//                                R.id.frame_container
//                            )*/
//                            baseActivity!!.replaceFragment(
//                                EmergencyRequestFragment(),
//                                R.id.frame_container
//                            )
//
//                        }
//                        1 -> {
//                            baseActivity!!.replaceFragment(
//                                InspectorHomeFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        2 -> {
////                        stats = 1
//                            sharedPref.setString(Const.stats, "1")
//                            cameraPermissions()
//                        }
//                        3 -> {
//                            selectMultipleDates()
//                        }
//                        4 -> {
//                            scheduleCalendar()
//                        }
//                        5 -> {
//                            baseActivity!!.replaceFragment(
//                                RequestDetailFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        6 -> {
//                            addProjectPopup()
//                        }
//                        7 -> {
//                            baseActivity!!.replaceFragment(
//                                SubjectToDiscussFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        8 -> {
//                            baseActivity!!.replaceFragment(
//                                TimeSheetFragment(),
//                                R.id.frame_container
//                            )
//                        }
//                        9 -> {
//                            baseActivity!!.replaceFragment(
//                                SafetyMeetingsFragment(),
//                                R.id.frame_container
//                            )
//                        }
//
//                    }
//
//
//                }
//
//
//            }
//
//        }
//
//    }
//
//
//    private fun injuredPopup() {
//
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.popup_injured)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        val psSelectType = dialog.findViewById(R.id.psSelectType) as PowerSpinnerView
//        val edtInjury = dialog.findViewById(R.id.edtInjury) as EditText
//        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
//        val rlInjuredbackground = dialog.findViewById(R.id.rlInjuredbackground) as RelativeLayout
//        val llInjuredLayout = dialog.findViewById(R.id.llInjuredLayout) as LinearLayout
//
//        psSelectType.setItems(selectTypeArray.toList())
//
//        rlInjuredbackground.setOnClickListener {
//            binding!!.switchStartShift.isChecked = true
//            dialog.dismiss()
//        }
//
//        dialog.setOnCancelListener {
//            binding!!.switchStartShift.isChecked = true
//        }
//
//        psSelectType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
//            selectType = newText
//            if (selectType=="Yes"){
//                llInjuredLayout.visibility=View.VISIBLE
//
//            }else{
//                llInjuredLayout.visibility=View.GONE
//            }
//        }
//
//        tvSubmit.setOnClickListener {
//
//            if(selectType.equals("")){
//                showToast("Please select type")
//            }else if (selectType.equals("Yes")){
//                if (edtInjury.text.isNullOrEmpty() || edtInjury.text.isBlank()) {
//                    showToast("Please enter injury explanation")
//                }else{
//                    dayClockInViewModel.dayClockInRequest(
//                        sessionToken = sharedPref.getString(Const.TOKEN),
//                        lat = sharedPref.getString(Const.latitude),
//                        long = sharedPref.getString(Const.longitude),
//                        image = imageBase64,
//                        injured = edtInjury.text.toString()
//                    )
//                    sharedPref.setBoolean(Const.SWITCH, false)
//                    dialog.dismiss()
//                }
//            }else if (selectType.equals("No")) {
//                dayClockInViewModel.dayClockInRequest(
//                    sessionToken = sharedPref.getString(Const.TOKEN),
//                    lat = sharedPref.getString(Const.latitude),
//                    long = sharedPref.getString(Const.longitude),
//                    image = imageBase64,
//                    injured = edtInjury.text.toString()
//                )
//                sharedPref.setBoolean(Const.SWITCH, false)
//                dialog.dismiss()
//            }
//
//        }
//
//        dialog.show()
//
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun selectMultipleDates() {
//        getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.popup_select_multiple_dates)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
//        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
//        val tvMonthYear = dialog.findViewById(R.id.tvMonthYear) as TextView
//        val edtLeaveReason = dialog.findViewById(R.id.edtLeaveReason) as EditText
//        val calendarView = dialog.findViewById(R.id.calendarView) as CalendarView
//        val ivCalendarForward = dialog.findViewById(R.id.ivCalendarForward) as ImageView
//        val ivCalendarBack = dialog.findViewById(R.id.ivCalendarBack) as ImageView
//        val tvVacCancel = dialog.findViewById(R.id.tvVacCancel) as TextView
//        val psLeaveType = dialog.findViewById(R.id.psLeaveType) as PowerSpinnerView
//        val tvVacSubmit = dialog.findViewById(R.id.tvVacSubmit) as TextView
//        val llVacationPopup = dialog.findViewById(R.id.llVacationPopup) as LinearLayout
//        val llSelectedTimeDates = dialog.findViewById(R.id.llSelectedTimeDates) as LinearLayout
//        llVacationPopup.visibility = View.GONE
//        llSelectedTimeDates.visibility = View.VISIBLE
//        psLeaveType.setItems(leaveTypeArray.toList())
//        psLeaveType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
////            showToast("$newText selected!")
//            selectedLeaveType = newText
//        }
//
//        this.calendarView = calendarView
//        val currentMonth = YearMonth.now()
//        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
//        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
//        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
//        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
//        calendarView.scrollToMonth(currentMonth)
//
//        calendarView.monthScrollListener = { month ->
//
//            val firstDate = month.weekDays.first().first().date
//            val lastDate = month.weekDays.last().last().date
//            Log.e("==>", "First date ${firstDate} ${lastDate}")
//
//            val date = month.yearMonth
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLL yyyy")
//            val text = date.format(formatter)
//
//            tvMonthYear.text = text
//            this.currentMonth = month.yearMonth
//        }
//
//        class DayViewContainer(view: View) : ViewContainer(view) {
//            // Will be set when this container is bound. See the dayBinder.
//            lateinit var day: CalendarDay
//            val textView = CalendarDayTextBinding.bind(view).exEightDayText
//
//            init {
//                view.setOnClickListener {
//                    if (day.position == DayPosition.MonthDate) {
//                        if (datesOfLeaveas.contains(day.date)) {
//                            showToast("You cannot select this date")
//                        }
//                        else{
//                            if (!day.date.isBefore(today)) {
////                            if (selectedDates.size >=2) {
////                            } else {
//                                dateClickedmulti(date = day.date)
//                                if (selectedDates.size>=2){
//                                    llVacationPopup.visibility = View.VISIBLE
//                                    llSelectedTimeDates.visibility = View.GONE
//                                }
////                            }
//                            } else {
//                                showToast("Previous dates cannot be selected")
//                            }
//
//                        }
//
//                    }
////                    dialogDate()
//                }
//            }
//        }
//
//        calendarView.apply {
//            dayBinder = object : MonthDayBinder<DayViewContainer> {
//                override fun create(view: View) = DayViewContainer(view)
//                override fun bind(container: DayViewContainer, data: CalendarDay) {
//                    container.day = data
//                    bindDatemulti(
//                        data.date,
//                        container.textView,
//                        data.position == DayPosition.MonthDate
//                    )
//                }
//            }
//        }
//
//        dialog.setOnCancelListener {
//            selectedDates.clear()
//
//        }
//        ivCalendarForward.setOnClickListener {
//            calendarView.scrollToMonth(currentMonth.plusMonths(1))
//        }
//
//        ivCalendarBack.setOnClickListener {
//            calendarView.scrollToMonth(currentMonth.minusMonths(1))
//        }
//
//        tvSubmit.setOnClickListener {
//
//        }
//
//        tvCancel.setOnClickListener {
//            dialog.dismiss()
//            selectedDates.clear()
//        }
//
//        tvVacCancel.setOnClickListener {
//            selectedDates.clear()
//            llVacationPopup.visibility = View.GONE
//            llSelectedTimeDates.visibility = View.VISIBLE
//            getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
//        }
//
////        val localDateArrayList = arrayListOf(LocalDate.parse("2022-01-01"), LocalDate.parse("2022-01-02"))
////        val localDateArrayList = arrayListOf(selectedDates)
////        val formatter = SimpleDateFormat("MM/dd/yyyy")
////        val stringArrayList = localDateArrayList.map { formatter.format(it) }.toCollection(ArrayList())
//
//        tvVacSubmit.setOnClickListener {
////            val localDateArrayList = arrayListOf(selectedDates)
////            val formattedDates = localDateArrayList.map { "\"$it\"" }.toCollection(ArrayList())
////            val stringArrayList = localDateArrayList.map { it.toString() }.toCollection(ArrayList())
////            convertArrayListToStringArray(formattedDates)
////
//
////            // converting arraylist to array
////            val dates_array: Array<String> = formattedDates.toTypedArray()
////
////            datesList.addAll(stringArrayList)
////
////
////            val datesList = arrayListOf<LocalDate> ()
////            datesList.addAll (selectedDates)
//////            val datesArray = datesList.toTypedArray ()
//
//            val datePattern = DateTimeFormatter.ofPattern("MM/dd/yyyy")
//            val formattedDates = selectedDates.map { date -> date.format(datePattern) }
//            val leaveDatesArray = JSONArray(formattedDates)
//
//            if (selectedLeaveType.isBlank() || selectedLeaveType.isEmpty() || edtLeaveReason.text.isNullOrEmpty() || edtLeaveReason.text.isBlank()) {
//                showToast("Kindly fill all")
//            } else {
//                applyLeavesViewModel.applyLeaveRequest(
//                    sessionToken = sharedPref.getString(Const.TOKEN),
//                    leaveDates = leaveDatesArray.toString(),
//                    reason = edtLeaveReason.text.toString(),
//                    type = selectedLeaveType
//                )
//            }
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun scheduleCalendar() {
//        schedulesViewModel.scheduleRequest(sharedPref.getString(Const.TOKEN), "")
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.popup_schedule)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
//        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
//        val tvMonthYear = dialog.findViewById(R.id.tvMonthYear) as TextView
//        val tvSCalendar = dialog.findViewById(R.id.tvSCalendar) as TextView
//        val calendarView = dialog.findViewById(R.id.calendarView) as CalendarView
//        val ivCalendarForward = dialog.findViewById(R.id.ivCalendarForward) as ImageView
//        val ivCalendarBack = dialog.findViewById(R.id.ivCalendarBack) as ImageView
//        val ivCross = dialog.findViewById(R.id.ivCross) as ImageView
//        tvSCalendar.text = "Schedule Calendar"
//
//        this.calendarView = calendarView
//        val currentMonth = YearMonth.now()
//        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
//        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
//        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
//        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
//        calendarView.scrollToMonth(currentMonth)
//
//        calendarView.monthScrollListener = { month ->
//
//            val firstDate = month.weekDays.first().first().date
//            val lastDate = month.weekDays.last().last().date
//            Log.e("==>", "First date ${firstDate} ${lastDate}")
//
//            val date = month.yearMonth
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLL yyyy")
//            val text = date.format(formatter)
//
//            tvMonthYear.text = text
//            this.currentMonth = month.yearMonth
//        }
//
//        class DayViewContainer(view: View) : ViewContainer(view) {
//            // Will be set when this container is bound. See the dayBinder.
//            lateinit var day: CalendarDay
//            val textView = CalendarDayTextBinding.bind(view).exEightDayText
//
//            init {
//                view.setOnClickListener {
//                    if (day.position == DayPosition.MonthDate) {
////                        if(!day.date.isBefore(today)) {
//                        Log.e("tag", "dateeddd.." + day.date)
//                        if (selectedDatesOfSchedule.contains(day.date)) {
//                            val bundle = Bundle()
//                            val inputDate =
//                                day.date // Replace this with your actual LocalDate object
//                            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
//                            val formattedDate = inputDate.format(formatter)
//                            bundle.putString("date", formattedDate)
//                            baseActivity!!.replaceFragWithArgs(
//                                ScheduledEventsFragment(),
//                                R.id.frame_container,
//                                bundle
//                            )
//                            dialog.dismiss()
//                        } else {
//                            showToast("No Schedule on this date")
//                        }
////                        }
//                    }
//                }
//            }
//        }
//
//        calendarView.apply {
//            dayBinder = object : MonthDayBinder<DayViewContainer> {
//                override fun create(view: View) = DayViewContainer(view)
//                override fun bind(container: DayViewContainer, data: CalendarDay) {
//                    container.day = data
//                    bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
//                }
//            }
//        }
//
//        ivCalendarForward.setOnClickListener {
//            calendarView.scrollToMonth(currentMonth.plusMonths(1))
//        }
//
//        ivCalendarBack.setOnClickListener {
//            calendarView.scrollToMonth(currentMonth.minusMonths(1))
//        }
//
//        tvSubmit.setOnClickListener {
//
//        }
//
//        tvCancel.setOnClickListener {
////            selectedDatesOfSchedule.clear()
////            selectedDatesOfSchedule.removeAll(selectedDatesOfSchedule)
////            dialog.dismiss()
//        }
//
//        ivCross.setOnClickListener {
////            selectedDatesOfSchedule.clear()
////            selectedDatesOfSchedule.removeAll(selectedDatesOfSchedule)
//            dialog.dismiss()
//        }
//
//
//
//        dialog.show()
//    }
//
//    fun convertArrayListToStringArray(arrayList: ArrayList<String>): Array<String> {
//        return arrayList.toTypedArray()
//    }
//
//
//    private fun dateClickedmulti(date: LocalDate) {
//
//
////        if (datesOfLeaveas.contains(date)) {
////        }else{
//            if (selectedDates.contains(date)) {
//                selectedDates.add(date)
////            selectedDates.remove(date)
//            } else {
//                selectedDates.add(date)
//            }
//            // We want to reload the footer text as well.
//            calendarView.notifyMonthChanged(date.yearMonth)
////        }
//
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun bindDatemulti(date: LocalDate, textView: TextView, isSelectable: Boolean) {
//        textView.text = date.dayOfMonth.toString()
//        if (isSelectable) {
//            when {
//                datesOfLeaveas.contains(date) -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.darkblue_rect)
//                }
//
//                selectedDates.contains(date) -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.darkblue_rect)
//                }
//
//                today == date -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.red_rect)
////                    textView.background = null
//                }
//                else -> {
//                    textView.setTextColor(resources.getColor(R.color.Black))
//                    textView.background = null
//                }
//            }
//        } else {
//            textView.setTextColor(resources.getColor(R.color.grey))
//            textView.background = null
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun bindDatemultiLeaves(date: LocalDate, textView: TextView, isSelectable: Boolean) {
//        textView.text = date.dayOfMonth.toString()
//        if (isSelectable) {
//            when {
//                selectedDates.contains(date) -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.darkblue_rect)
//                }
//                today == date -> {
//                    textView.setTextColor(resources.getColor(R.color.White))
//                    textView.setBackgroundResource(R.drawable.red_rect)
////                    textView.background = null
//                }
//                else -> {
//                    textView.setTextColor(resources.getColor(R.color.Black))
//                    textView.background = null
//                }
//            }
//        } else {
//            textView.setTextColor(resources.getColor(R.color.grey))
//            textView.background = null
//        }
//    }
//
//    private fun sessionExpiredPopup() {
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.popup_token_expired)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
//
//        tvOk.setOnClickListener {
//            dialog.dismiss()
//            context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)
//            sharedPref.clearPref()
//            baseActivity!!.gotoLoginSignUpActivity()
//        }
//        dialog.show()
//    }
//
//    private fun logoutPopup() {
//
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.popup_logout)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        val tvLogout = dialog.findViewById(R.id.tvLogout) as TextView
//        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
//        val rlInjuredbackground = dialog.findViewById(R.id.rlInjuredbackground) as RelativeLayout
//
//        rlInjuredbackground.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        tvLogout.setOnClickListener {
//            dialog.dismiss()
//            logoutViewModel.logOutRequest(sharedPref.getString(Const.TOKEN))
//        }
//
//        tvCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//
//    private fun expensePopup(imageBase64:String) {
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.popup_expense)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        val ivExpenseImage = dialog.findViewById(R.id.ivExpenseImage) as CircleImageView
//        val edtTitle = dialog.findViewById(R.id.edtTitle) as EditText
//        val edtPrice = dialog.findViewById(R.id.edtPrice) as EditText
//        val psProjectName = dialog.findViewById(R.id.psProjectName) as PowerSpinnerView
//        val psSelectCategory = dialog.findViewById(R.id.psSelectCategory) as PowerSpinnerView
//        val cbExpnseRequire = dialog.findViewById(R.id.cbExpnseRequire) as CheckBox
//        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
//        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
//
//        val bitmap = base64ToBitmap(imageBase64)
//
//        ivExpenseImage.setImageBitmap(bitmap)
//        psProjectName.setItems(projectNameList)
//
//        psProjectName.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
////            showToast("$newText selected!")
//            projectName = newText
//        }
//
//        if (cbExpnseRequire.isChecked && cbExpnseRequire.isSelected) {
//            reimbursement = "0"
//        } else {
//            reimbursement = "1"
//        }
//
//        psSelectCategory.setItems(categoryList)
//        psSelectCategory.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
////            showToast("$newText selected!")
//            selectedCategory = newText
//        }
//
//        tvCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        tvSubmit.setOnClickListener {
//            if (projectName.equals("")) {
//                showToast("Please select project name")
//            } else if (edtTitle.text.isNullOrEmpty() || edtPrice.text.isBlank()) {
//                showToast("Please enter title")
//            } else if (selectedCategory.isBlank()) {
//                showToast("Please select category")
//            } else if (edtPrice.text.isNullOrEmpty() || edtPrice.text.isBlank()) {
//                showToast("Please enter price")
//            } else {
//                addExpenseViewModel.addExpenseRequest(
//                    sessionToken = sharedPref.getString(Const.TOKEN),
//                    title = edtTitle.text.toString(),
//                    image = imageBase64,
//                    category = selectedCategory,
//                    price = edtPrice.text.toString(),
//                    projectId = projectName,
//                    reimbursement = reimbursement
//                )
//                dialog.dismiss()
//            }
//        }
//        dialog.show()
//
//    }
//
//    private fun addProjectPopup() {
//        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//        dialog.setContentView(R.layout.popup_add_project)
//        dialog.getWindow()!!
//            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        var edtProjectName = dialog.findViewById(R.id.edtProjectName) as EditText
//        var edtContractorName = dialog.findViewById(R.id.edtContractorName) as EditText
//        var edtAddress = dialog.findViewById(R.id.edtAddress) as EditText
//        var edtCity = dialog.findViewById(R.id.edtCity) as EditText
////        var edtState = dialog.findViewById(R.id.edtState) as EditText
//        var psSelectState = dialog.findViewById(R.id.psSelectState) as PowerSpinnerView
//        var edtPostalCode = dialog.findViewById(R.id.edtPostalCode) as EditText
//        var tvSave = dialog.findViewById(R.id.tvSave) as TextView
//
//        psSelectState.setItems(statesArray.toList())
//
//        psSelectState.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
//            showToast("$newText selected!")
//            selectState = newText
//        }
//
//        tvSave.setOnClickListener {
//            if (edtProjectName.text.isNullOrEmpty() || edtProjectName.text.isBlank()) {
//                showToast("Please enter project name")
//            } else if (edtContractorName.text.isNullOrEmpty() || edtContractorName.text.isBlank()) {
//                showToast("Please enter address")
//            } else if (edtAddress.text.isNullOrEmpty() || edtAddress.text.isBlank()) {
//                showToast("Please enter address")
//            } else if (edtCity.text.isNullOrEmpty() || edtCity.text.isBlank()) {
//                showToast("Please enter city")
//            } /*else if (edtState.text.isNullOrEmpty() || edtState.text.isBlank()) {
//                showToast("Please enter state")
//            }*/ else if (edtPostalCode.text.isNullOrEmpty() || edtPostalCode.text.isBlank()) {
//                showToast("Please enter postal code")
//            } else {
//                createProjectViewModel.createProjectRequest(
//                    sessionToken = sharedPref.getString(Const.TOKEN),
//                    name = edtProjectName.text.toString(),
//                    contractorName = edtContractorName.text.toString(),
//                    lat = sharedPref.getString(Const.latitude),
//                    long = sharedPref.getString(Const.longitude),
//                    address = edtAddress.text.toString(),
//                    city = edtCity.text.toString(),
//                    state = selectState,
//                    postalCode = edtPostalCode.text.toString()
//                )
//                dialog.dismiss()
//            }
//        }
//
//        dialog.show()
//
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_CODE_CAMERA1 -> {
//                // This is the result for CameraView1
//
//                // main code
//                if (resultCode == Activity.RESULT_OK) {
//                    val image = data!!.extras!!.get("data") as Bitmap
//                    if (image != null) {
//                        sharedPref.setString(Const.stats, "")
//                        imageBase64 = convertImageToBase64String(image)
//                        dayClockInViewModel.dayClockInRequest(
//                            sessionToken = sharedPref.getString(Const.TOKEN),
//                            lat = sharedPref.getString(Const.latitude),
//                            long = sharedPref.getString(Const.longitude),
//                            image = imageBase64,
//                            injured = ""
//                        )
//                        sharedPref.setBoolean(Const.SWITCH, true)
//                        homeViewModel.homeRequest(
//                            sharedPref.getString(Const.TOKEN),
//                            today.toString()
//                        )
//                    }
//                } else {
//                    // Handle the capture failure or cancellation for CameraView1
//                }
//            }
//
//            REQUEST_CODE_CAMERA2 -> {
//                if (resultCode == Activity.RESULT_OK) {
//                   val image = data!!.extras!!.get("data") as Bitmap
//                    if (image != null) {
//                        sharedPref.setString(Const.stats, "")
//
//                        homeViewModel.homeRequest(
//                            sharedPref.getString(Const.TOKEN),
//                            filterDate = today.toString()
//                        )
//                        clockInListViewModel.clockInListRequest(
//                            sharedPref.getString(Const.TOKEN),
//                            sharedPref.getString(Const.latitude),
//                            sharedPref.getString(Const.longitude)
//                        )
//                        imageBase64 = convertImageToBase64String(image)
//                        expensePopup(imageBase64)
//                    }
//                } else {
//                    // Handle the capture failure or cancellation for CameraView2
//                }
//            }
//
//
//        }
//
//
//    }
//
//
//    private fun changeButtonColour() {
//        binding!!.darkTV.setBackgroundResource(R.drawable.custom_shape_lang_button)
//        binding!!.darkTV.setTextColor(Color.parseColor("#2b2b48"))
//
//        binding!!.lightTV.setBackgroundResource(R.drawable.language_bg_tv)
//        binding!!.lightTV.setTextColor(Color.parseColor("#f2f2f2"))
//    }
//
//    private fun changeButtonColor() {
//        binding!!.lightTV.setBackgroundResource(R.drawable.custom_shape_lang_button)
//        binding!!.lightTV.setTextColor(Color.parseColor("#2b2b48"))
//
//        binding!!.darkTV.setBackgroundResource(R.drawable.language_bg_tv)
//        binding!!.darkTV.setTextColor(Color.parseColor("#f2f2f2"))
//    }
//
//
//    override fun onViewClick(view: View) {
//        when (view.id) {
//            R.id.rlClockInout -> {
//                val bundle = Bundle()
//                bundle.putString("latitude", latitude)
//                bundle.putString("longitude", longitude)
//                baseActivity!!.replaceFragWithArgs(
//                    ClockInOutFragment(),
//                    R.id.frame_container,
//                    bundle
//                )
//            }
//            R.id.ivLogout -> {
//                logoutPopup()
//            }
//
//            /*R.id.darkTV -> {
//                sharedPref.setLanguageSelected("DARK")
//                changeButtonColour()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//
//            }
//            R.id.lightTV -> {
//                sharedPref.setLanguageSelected("LIGHT")
//                changeButtonColor()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//
//            }*/
//
//            R.id.darkTV -> {
//                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//                if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
//                    // Switch to night (dark) mode
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                } else {
//                    // Switch to day (light) mode
//                    sharedPref.setLanguageSelected("DARK")
//                    changeButtonColour()
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                }
//            }
//
//            R.id.lightTV -> {
//                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//                if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
//                    // Switch to night (dark) mode
//                    sharedPref.setLanguageSelected("LIGHT")
//                    changeButtonColor()
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                } else {
//                    // Switch to day (light) mode
//                    sharedPref.setLanguageSelected("DARK")
//                    changeButtonColour()
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                }
//            }
//
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onResume() {
//        super.onResume()
////        getLocation()
//        if (sharedPref.getLanguageSelected() == "DARK") {
//
//            changeButtonColour()
//        }
//
//        if (sharedPref.getLanguageSelected() == "LIGHT") {
//            changeButtonColor()
//        }
//        checkAndRequestLocationPermissions()
//
//        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), filterDate = today.toString())
//        clockInListViewModel.clockInListRequest(
//            sharedPref.getString(Const.TOKEN),
//            lat = sharedPref.getString(Const.latitude),
//            long = sharedPref.getString(Const.longitude)
//        )
//        schedulesViewModel.scheduleRequest(sharedPref.getString(Const.TOKEN), "")
//
//    }
//
////    override fun onPause() {
////        super.onPause()
////        easyWayLocation!!.endUpdates()
////    }
//
//
//    fun timerRun() {
//        val monitor: Runnable = object : Runnable {
//            override fun run() {
//                //any action
//                punchData++ // Increment timestamp (you should replace this with your actual timestamp logic)
//                val formattedTime = convertSecondsToHHMMSS(punchData)
//                binding!!.tvPunchTime.text = formattedTime
//                progress = progress + 1
//                binding!!.progressBarCircle.progress = progress.toInt()
//            }
//            //runnable
//        }
//
//        mHandler.postDelayed(monitor, 1000)
//    }
//
////    private val updateTimeRunnable = object : Runnable {
////        override fun run() {
////            punchData++ // Increment timestamp (you should replace this with your actual timestamp logic)
////            val formattedTime = convertSecondsToHHMMSS(punchData)
////            binding!!.tvPunchTime.text = formattedTime
////            progress=progress+1
////            binding!!.progressBarCircle.progress = progress.toInt()
////            handler.postDelayed(this, 1000) // Update every 1 second
////        }
////    }
//
//    private fun convertSecondsToHHMMSS(timestamp: Long): String {
//        val hours = TimeUnit.SECONDS.toHours(timestamp)
//        val minutes = TimeUnit.SECONDS.toMinutes(timestamp) % 60
//        val seconds = timestamp % 60
//        return String.format("%02d:%02d", hours, minutes, seconds)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun observer() {
//        homeViewModel.homeSuccess.observe(viewLifecycleOwner, Observer {
//
//            if (it.status == true) {
////              showToast("Success")
//                binding!!.userNameTV.text = it.homeData.userName
//                binding!!.tvProjects.text = it.homeData.projectCount
//                binding!!.tvAppliedLeaves.text = it.homeData.approvedLeaveCount
//
//                //timer data
//                punchData = it.homeData.punchData
//                val formattedTime = convertSecondsToHHMMSS(punchData)
//                binding!!.tvPunchTime.text = formattedTime
//
//                if (punchData.toInt() <= 0) {
////                    binding!!.switchStartShift.isEnabled=false
//                    binding!!.switchStartShift.isChecked = false
////                    sharedPref.setBoolean(Const.SWITCH, false)
//                } else {
////                    binding!!.switchStartShift.isEnabled=true
//                    timerRun()
//                    binding!!.switchStartShift.isChecked = true
////                    sharedPref.setBoolean(Const.SWITCH, true)
//
//                }
//
//                progress = punchData.toInt()
//
//                if (progress == 0) {
//                    sharedPref.setBoolean(Const.SWITCH, false)
////                    binding!!.switchStartShift.isEnabled=false
//                } else {
//                    sharedPref.setBoolean(Const.SWITCH, true)
////                    binding!!.switchStartShift.isEnabled=true
//                }
//                binding!!.progressBarCircle.max = 36000
////              binding!!.tvPunchTime.text = formatMilliseconds(it.homeData.punchData.toLong()*1000)
////                val punchtime = it.homeData.punchData * 1000
////                binding!!.tvPunchTime.text = formatMilliseconds(punchtime)
//                binding!!.ivProfile.loadFromUrl(
//                    Const.IMG_URL + it.homeData.profilePic,
//                    R.drawable.ic_placeholder
//                )
//                for (i in it.homeData.expenseData.indices) {
//                    categoryList.add(it.homeData.expenseData.get(i).name)
//                }
//
//
////                image.recycle()
//            }
//        })
//
//        homeViewModel.apiError.observe(viewLifecycleOwner) {
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        homeViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//        clockInListViewModel.clockInListSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                for (i in it.clockinlistData.projects.indices) {
//                    projectNameList.add(it.clockinlistData.projects.get(i).fullName)
//                }
//            }
//        })
//
//        clockInListViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        clockInListViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//
//        // Observe addExpenseSuccess LiveData
//        addExpenseViewModel.addExpenseSuccess.observe(viewLifecycleOwner) { response ->
//            if (response.status == true) {
//                showToast("Expense added successfully")
////                image.recycle()
//                // Handle success: You can update UI or perform other actions here
//            } else {
////                showToast(response.message ?: "Error adding expense")
//                // Handle error: Display an appropriate message
//            }
//        }
//
//        addExpenseViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        addExpenseViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//        logoutViewModel.logoutSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                sharedPref.clearPref()
//                context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)
//                baseActivity!!.gotoLoginSignUpActivity()
//            }
//        })
//
//        logoutViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        logoutViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//        dayClockInViewModel.dayClockInSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                if (it.message == "Checked In Successful") {
//                    binding!!.switchStartShift.isChecked = true
//                    sharedPref.setBoolean(Const.SWITCH, true)
//                    homeViewModel.homeRequest(
//                        sharedPref.getString(Const.TOKEN),
//                        filterDate = today.toString()
//                    )
//                    showToast(it.message)
//               } else if (it.message == "Please provide lat") {
//                    sharedPref.setBoolean(Const.SWITCH, false)
//                    showToast("Please allow location permission from app settings")
//                }else if (it.message == "Can't clockout before one hour") {
//                    showToast(it.message!!)
//                    homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
//                } else {
//                    sharedPref.setBoolean(Const.SWITCH, false)
//                    showToast(it.message!!)
////                    showToast("Please allow location permission from app settings")
//                }
//            }
//        })
//
//        dayClockInViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        dayClockInViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//        createProjectViewModel.createProjectSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                showToast(it.message)
//                homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), filterDate = today.toString())
//            }
//        })
//
//        createProjectViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        createProjectViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//        applyLeavesViewModel.applyLeaveSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                showToast(it.message)
//                selectedDates.clear()
//            }
//        })
//
//        applyLeavesViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        applyLeavesViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//
//        //Schedules
//        schedulesViewModel.scheduleSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                // showToast(it.message)
//                dates = it.dataSchedule.dates as ArrayList<String>
//                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//                val getDatesfromApi = arrayListOf<LocalDate>()
//                for (dateString in dates) {
//                    val trimmedString = dateString.toString()
//                    val parsedDate = LocalDate.parse(trimmedString, formatter)
//                    getDatesfromApi.add(parsedDate)
//                }
//                selectedDatesOfSchedule = getDatesfromApi
//                meetings.addAll(it.dataSchedule.meetings)
//            }
//        })
//
//        schedulesViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        schedulesViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//        getLeavesViewModel.getLeavesSuccess.observe(viewLifecycleOwner, Observer {
//            if (it.status == true) {
//                // showToast(it.message)
//                leavedates= it.data.vacationsLeaves as ArrayList<String>
//                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//                val getDatesfromApi = arrayListOf<LocalDate>()
//                for (dateString in leavedates) {
//                    val trimmedString = dateString.toString()
//                    val parsedDate = LocalDate.parse(trimmedString, formatter)
//                    getDatesfromApi.add(parsedDate)
//                }
//                datesOfLeaveas = getDatesfromApi
////                selectedDates = getDatesfromApi
//            }
//        })
//
//        getLeavesViewModel.apiError.observe(viewLifecycleOwner) {
////            showToast(it)
//            if (it.toString().equals("401")) {
//                sessionExpiredPopup()
//            } else {
////                showToast(it)
//            }
//        }
//
//        getLeavesViewModel.isLoading.observe(viewLifecycleOwner) {
//            if (it) {
//                baseActivity!!.progressBarPB.show()
//            } else {
//                baseActivity!!.progressBarPB.dismiss()
//            }
//        }
//
//
//    }
//
//
//
//
//
//}