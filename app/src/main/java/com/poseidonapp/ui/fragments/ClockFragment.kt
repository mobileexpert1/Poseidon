package com.poseidonapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentClockBinding
import com.poseidonapp.model.clockInlistscreen.ProjectsItem
import com.poseidonapp.ui.activities.LoginActivity
import com.poseidonapp.ui.adapter.SearchItemAdapter
import com.poseidonapp.ui.adapter.ShiftTimerAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.loadFromUrl
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.ClockInListViewModel
import com.poseidonapp.viewmodel.HomeViewModel
import com.poseidonapp.viewmodel.clockinlistscreen.ClockInListScreenViewModel
import com.poseidonapp.viewmodel.dayclockin.DayClockInViewModel
import com.poseidonapp.viewmodel.switchclockin.SwitchClockInViewModel
import com.skydoves.powerspinner.PowerSpinnerView
import java.security.AccessController.checkPermission
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class ClockFragment : BaseFragment(), HandleClickListener, ShiftTimerAdapter.ClickListeners,SearchItemAdapter.OnItemClickListener  {
    var binding: FragmentClockBinding? = null
    lateinit var clockInListScreenViewModel: ClockInListScreenViewModel
    lateinit var switchClockInViewModel: SwitchClockInViewModel
    lateinit var homeViewModel: HomeViewModel
    lateinit var clockInListViewModel: ClockInListViewModel
    lateinit var dayClockInViewModel: DayClockInViewModel
    private var shiftTimerAdapter: ShiftTimerAdapter? = null
    var latitude: String = ""
    var longitude: String = ""
    var projectItemList: List<ProjectsItem>? = null
    var currentOnId: Int = 0
    val selectTypeArray = arrayOf("Yes", "No")
    var selectType: String = ""
    var isChecked: Boolean = false
    var punchData:Long = 0
    var progress=0
    val mHandler = Handler()
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationRequest: LocationRequest? = null
    private val today = LocalDate.now()
    lateinit var categoryList: ArrayList<String>
    var imageBase64: String = ""
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                captureImage()
            } else {
                Log.i("Permission: ", "Denied")
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentClockBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }
    private fun initUI() {

        binding!!.handleClick = this
        latitude = sharedPref.getString(Const.latitude)
        longitude = sharedPref.getString(Const.longitude)
        categoryList = ArrayList()
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        clockInListScreenViewModel = ViewModelProvider(this)[ClockInListScreenViewModel::class.java]
        clockInListViewModel = ViewModelProvider(this)[ClockInListViewModel::class.java]
        dayClockInViewModel = ViewModelProvider(this)[DayClockInViewModel::class.java]
        switchClockInViewModel = ViewModelProvider(this)[SwitchClockInViewModel::class.java]
        fusedLocation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
//        clockInListScreenViewModel.clockInListRequest(
//            sharedPref.getString(Const.TOKEN),
//            latitude,
//            longitude,
//            "0"
//        )
        observers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        fusedLocation()
        startLocationUpdates()
        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), filterDate = today.toString())
//        clockInListViewModel.clockInListRequest(
//            sharedPref.getString(Const.TOKEN),
//            lat = sharedPref.getString(Const.latitude),
//            long = sharedPref.getString(Const.longitude)
//        )
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
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest!!, locationCallback, null)
    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onClickListListener(index: Int) {
        clockInListScreenViewModel.clockInListRequest(
            sharedPref.getString(Const.TOKEN),
            latitude,
            longitude,
            "0"
        )
    }

    private fun convertSecondsToHHMMSS(timestamp: Long): String {
        val hours = TimeUnit.SECONDS.toHours(timestamp)
        val minutes = TimeUnit.SECONDS.toMinutes(timestamp) % 60
        val seconds = timestamp % 60
        return String.format("%02d:%02d", hours, minutes, seconds)
    }

    fun timerRun(){
        val monitor: Runnable = object : Runnable{
            override fun run() {
                //any action
                punchData++ // Increment timestamp (you should replace this with your actual timestamp logic)
                val formattedTime = convertSecondsToHHMMSS(punchData)
                binding!!.tvPunchTime.text = formattedTime
                progress=progress+1
                binding!!.progressBarCircle.progress = progress.toInt()
            }
            //runnable
        }

        mHandler.postDelayed(monitor, 1000)
    }

    private fun observers() {

        homeViewModel.homeSuccess.observe(viewLifecycleOwner, Observer {

            if (it.status == true) {
//              showToast("Success")
                binding!!.tvProjects.text = it.homeData.projectCount

                //timer data
                punchData = it.homeData.punchData
                val formattedTime = convertSecondsToHHMMSS(punchData)
//                binding!!.tvPunchTime.text = formattedTime

                progress = punchData.toInt()
                if (punchData.toInt()<=0 ){
                    binding!!.tvPunchTime.text = "00:00"
                }else{
                    binding!!.tvPunchTime.text = formattedTime
                }

                if (progress == 0) {
                    sharedPref.setBoolean(Const.SWITCH, false)
//                    binding!!.switchStartShift.isEnabled=false
                } else {
                    sharedPref.setBoolean(Const.SWITCH, true)
//                    binding!!.switchStartShift.isEnabled=true
                }
                binding!!.progressBarCircle.max = 36000
//                binding!!.tvPunchTime.text = formatMilliseconds(punchData.toLong() * 1000)
                val punchtime = it.homeData.punchData * 1000
//                binding!!.tvPunchTime.text = formatMilliseconds(punchtime)
                binding!!.profileimage.loadFromUrl(
                    Const.IMG_URL + it.homeData.profilePic,
                    R.drawable.ic_placeholder
                )
                binding!!.tvName.text=it.homeData.userName
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


        switchClockInViewModel.switchClockInSuccess.observe(viewLifecycleOwner, Observer {

            if (it.status==true){
                shiftTimerAdapter!!.notifyDataSetChanged()
                clockInListScreenViewModel.clockInListRequest(sharedPref.getString(Const.TOKEN), latitude, longitude, "0")
                showToast(it.message)
            }

        })

        switchClockInViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        switchClockInViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

    }

    fun clockInObserver(){

        clockInListScreenViewModel.clockInListScreenSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status==true){
                projectItemList = it.clockinData.projects
                currentOnId = it.clockinData.projectColockIn.projectId

                //timer data
                punchData = it.clockinData.projectColockIn.timeDifferece.toLong()
                val formattedTime = convertSecondsToHHMMSS(punchData)
                if (punchData.toInt()<=0 ){
                    binding!!.tvPunchTime.text = "00:00"
                }else{
                    binding!!.tvPunchTime.text = formattedTime
                    timerRun()
                }
                progress=punchData.toInt()
                binding!!.progressBarCircle.max=  36000
            }

        })

        clockInListScreenViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        clockInListScreenViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    private fun injuredPopup() {

        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_injured)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        val psSelectType = dialog.findViewById(R.id.psSelectType) as PowerSpinnerView
        val edtInjury = dialog.findViewById(R.id.edtInjury) as EditText
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
        val rlInjuredbackground = dialog.findViewById(R.id.rlInjuredbackground) as RelativeLayout
        val llInjuredLayout = dialog.findViewById(R.id.llInjuredLayout) as LinearLayout

        psSelectType.setItems(selectTypeArray.toList())

        rlInjuredbackground.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
//            binding!!.switchStartShift.isChecked = true
        }

        psSelectType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            selectType = newText
            if (selectType=="Yes"){
                llInjuredLayout.visibility=View.VISIBLE

            }else{
                llInjuredLayout.visibility=View.GONE
            }
        }

        tvSubmit.setOnClickListener {

            if(selectType.equals("")){
                showToast("Please select type")
            }else if (selectType.equals("Yes")){
                if (edtInjury.text.isNullOrEmpty() || edtInjury.text.isBlank()) {
                    showToast("Please enter injury explanation")
                }else{
                    dayClockInViewModel.dayClockInRequest(
                        sessionToken = sharedPref.getString(Const.TOKEN),
                        lat = sharedPref.getString(Const.latitude),
                        long = sharedPref.getString(Const.longitude),
                        image = imageBase64,
                        injured = edtInjury.text.toString()
                    )
                    dayClockObserver()
                    sharedPref.setBoolean(Const.SWITCH, false)
                    dialog.dismiss()
                }
            }else if (selectType.equals("No")) {
                dayClockInViewModel.dayClockInRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    lat = sharedPref.getString(Const.latitude),
                    long = sharedPref.getString(Const.longitude),
                    image = imageBase64,
                    injured = edtInjury.text.toString()
                )
                dayClockObserver()
                sharedPref.setBoolean(Const.SWITCH, false)
                dialog.dismiss()
            }

        }

        dialog.show()

    }

    fun instructionsPopup(position: Int,projectId:String) {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.popup_instructions)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.window!!.setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
        val cbExpnseRequire = dialog.findViewById(R.id.cbExpnseRequire) as CheckBox
        val rlBackground = dialog.findViewById(R.id.rlBackground) as RelativeLayout

        rlBackground.setOnClickListener {
            shiftTimerAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            shiftTimerAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }

        tvSubmit.setOnClickListener {
            if (cbExpnseRequire.isChecked || cbExpnseRequire.isSelected) {
                shiftTimerAdapter!!.notifyDataSetChanged()
                switchClockInViewModel.switchClockInRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    lat = latitude,
                    long = longitude,
                    project = projectId
                )
                dialog.dismiss()
            } else {
                showToast("Please accept the instructions")
            }
        }

        dialog.show()

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
        when (view.id) {
            R.id.rlProgress->{
                var punchTime = binding!!.tvPunchTime.text.toString()
                if(punchTime.equals("00:00")){
                    fusedLocation()
                    cameraPermissions()
                }else{
                    injuredPopup()
                }

            }
            R.id.iv_notification -> {
                findNavController().navigate(R.id.inspectorNotificationFragment)
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


    fun cameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                captureImage()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }
    companion object{
        val REQUEST_CODE_CAMERA1 = 1
        val REQUEST_CODE_CAMERA2 = 2
    }

    @SuppressLint("SuspiciousIndentation")
    fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA1)
    }

    fun dayClockObserver(){
                dayClockInViewModel.dayClockInSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                if (it.message == "Checked In Successful") {
//                    binding!!.switchStartShift.isChecked = true
                    sharedPref.setBoolean(Const.SWITCH, true)
                    homeViewModel.homeRequest(
                        sharedPref.getString(Const.TOKEN),
                        filterDate = today.toString()
                    )
                    showToast(it.message)
               } else if (it.message == "Please provide lat") {
                    sharedPref.setBoolean(Const.SWITCH, false)
                    showToast("Please allow location permission from app settings")
                }else if (it.message == "Can't clockout before one hour") {
                    showToast(it.message!!)
                    homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
                } else {
                    sharedPref.setBoolean(Const.SWITCH, false)
                    showToast(it.message!!)
//                    showToast("Please allow location permission from app settings")
                }
            }
        })

        dayClockInViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        dayClockInViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_CAMERA1 -> {
                // This is the result for CameraView1
                if (resultCode == Activity.RESULT_OK) {
                    val image = data!!.extras!!.get("data") as Bitmap
                    if (image != null) {
                        sharedPref.setString(Const.stats, "")
                        imageBase64 = convertImageToBase64String(image)
                        dayClockInViewModel.dayClockInRequest(
                            sessionToken = sharedPref.getString(Const.TOKEN),
                            lat = sharedPref.getString(Const.latitude),
                            long = sharedPref.getString(Const.longitude),
                            image = imageBase64,
                            injured = ""
                        )
                        dayClockObserver()
                        sharedPref.setBoolean(Const.SWITCH, true)
                        homeViewModel.homeRequest(
                            sharedPref.getString(Const.TOKEN),
                            today.toString()
                        )
                    }
                } else {
                    // Handle the capture failure or cancellation for CameraView1
                }
            }

/*
            REQUEST_CODE_CAMERA2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val image = data!!.extras!!.get("data") as Bitmap
                    if (image != null) {
                        sharedPref.setString(Const.stats, "")

                        homeViewModel.homeRequest(
                            sharedPref.getString(Const.TOKEN),
                            filterDate = today.toString()
                        )
                        clockInListViewModel.clockInListRequest(
                            sharedPref.getString(Const.TOKEN),
                            sharedPref.getString(Const.latitude),
                            sharedPref.getString(Const.longitude)
                        )
                        imageBase64 = convertImageToBase64String(image)

                    }
                } else {
                    // Handle the capture failure or cancellation for CameraView2
                }
            }
*/


        }
    }

    override fun onclick(position: Int, isChecked: Boolean, switch: SwitchCompat) {
        TODO("Not yet implemented")
    }


}


