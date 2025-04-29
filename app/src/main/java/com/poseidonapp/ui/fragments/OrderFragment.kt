package com.poseidonapp.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.CalendarDayTextBinding
import com.poseidonapp.databinding.FragmentOrderBinding
import com.poseidonapp.ui.activities.LoginActivity
import com.poseidonapp.ui.extensions.loadFromUrl
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.AddExpenseViewModel
import com.poseidonapp.viewmodel.ClockInListViewModel
import com.poseidonapp.viewmodel.GetLeavesViewModel
import com.poseidonapp.viewmodel.HomeViewModel
import com.poseidonapp.viewmodel.applyleaves.ApplyLeavesViewModel
import com.poseidonapp.viewmodel.dayclockin.DayClockInViewModel
import com.poseidonapp.workorder.activities.BaseFragment
import com.skydoves.powerspinner.PowerSpinnerView
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class OrderFragment : BaseFragment(), HandleClickListener {
    var binding: FragmentOrderBinding? = null
    lateinit var sharedPref: SharedPref
    lateinit var getLeavesViewModel: GetLeavesViewModel
    lateinit var applyLeavesViewModel: ApplyLeavesViewModel
    lateinit var addExpenseViewModel: AddExpenseViewModel
    lateinit var dayClockInViewModel: DayClockInViewModel
    lateinit var clockInListViewModel: ClockInListViewModel
    lateinit var homeViewModel: HomeViewModel
    val leaveTypeArray = arrayOf("Unpaid", "Paid")
    var selectedLeaveType: String = ""
    lateinit var calendarView: CalendarView
    lateinit var currentMonth: YearMonth
    private var selectedDates = ArrayList<LocalDate>()
    private var datesOfLeaveas = ArrayList<LocalDate>()
    private val today = LocalDate.now()
    lateinit var leavedates: ArrayList<String>
    val PERMISSION_REQUEST_CODE_PROFILE = 211
    val REQUEST_IMAGE_CAPTURE = 101
    var projectName: String = ""
    var reimbursement: String = ""
    lateinit var categoryList: ArrayList<String>
    lateinit var projectNameList: ArrayList<String>
    var selectedCategory: String = ""
    var imageBase64: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentOrderBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        projectNameList = ArrayList()
        categoryList = ArrayList()
        sharedPref = SharedPref(requireContext())
        binding!!.ivVacationlLayout.setOnClickListener { onViewClick(it) }
        getLeavesViewModel = ViewModelProvider(this)[GetLeavesViewModel::class.java]
        applyLeavesViewModel = ViewModelProvider(this)[ApplyLeavesViewModel::class.java]
        addExpenseViewModel = ViewModelProvider(this)[AddExpenseViewModel::class.java]
        dayClockInViewModel = ViewModelProvider(this)[DayClockInViewModel::class.java]
        clockInListViewModel = ViewModelProvider(this)[ClockInListViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
        clockInListViewModel.clockInListRequest(
            sharedPref.getString(Const.TOKEN),
            sharedPref.getString(Const.latitude),
            sharedPref.getString(Const.longitude)
        )
        observer()
        ProfileNameobservers()
        clocklistObsserver()
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivVacationlLayout -> {
                selectMultipleDates()
            }

            R.id.ivExpenseLayout -> {
                if (checkPermission()) {
                    captureImage()
                } else {
                    requestCameraPermission()
                }
            }
            R.id.ivTimesheetLayout -> {
                findNavController().navigate(R.id.timeSheetFragment)
            }
            R.id.iv_notification -> {
                findNavController().navigate(R.id.inspectorNotificationFragment)
            }
            R.id.ivManagementtLayout->{
                findNavController().navigate(R.id.subjectToDiscussFragment)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectMultipleDates() {
        getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
        val dialog = Dialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_select_multiple_dates)
        dialog.window?.apply { setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val tvMonthYear = dialog.findViewById(R.id.tvMonthYear) as TextView
        val edtLeaveReason = dialog.findViewById(R.id.edtLeaveReason) as EditText
        val calendarView = dialog.findViewById(R.id.calendarView) as CalendarView
        val ivCalendarForward = dialog.findViewById(R.id.ivCalendarForward) as ImageView
        val ivCalendarBack = dialog.findViewById(R.id.ivCalendarBack) as ImageView
        val tvVacCancel = dialog.findViewById(R.id.tvVacCancel) as TextView
        val psLeaveType = dialog.findViewById(R.id.psLeaveType) as PowerSpinnerView
        val tvVacSubmit = dialog.findViewById(R.id.tvVacSubmit) as TextView
        val llVacationPopup = dialog.findViewById(R.id.llVacationPopup) as LinearLayout
        val llSelectedTimeDates = dialog.findViewById(R.id.llSelectedTimeDates) as LinearLayout
        llVacationPopup.visibility = View.GONE
        llSelectedTimeDates.visibility = View.VISIBLE
        psLeaveType.setItems(leaveTypeArray.toList())
        psLeaveType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
//            showToast("$newText selected!")
            selectedLeaveType = newText
        }

        this.calendarView = calendarView
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        calendarView.monthScrollListener = { month ->

            val firstDate = month.weekDays.first().first().date
            val lastDate = month.weekDays.last().last().date
            Log.e("==>", "First date ${firstDate} ${lastDate}")

            val date = month.yearMonth
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLL yyyy")
            val text = date.format(formatter)

            tvMonthYear.text = text
            this.currentMonth = month.yearMonth
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarDayTextBinding.bind(view).exEightDayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (datesOfLeaveas.contains(day.date)) {
                            Toast.makeText(
                                requireContext(),
                                "You cannot select this date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (!day.date.isBefore(today)) {
//                            if (selectedDates.size >=2) {
//                            } else {
                                dateClickedmulti(date = day.date)
                                if (selectedDates.size >= 2) {
                                    llVacationPopup.visibility = View.VISIBLE
                                    llSelectedTimeDates.visibility = View.GONE
                                }
//                            }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "You cannot select this date",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }
//                    dialogDate()
                }
            }
        }

        calendarView.apply {
            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    container.day = data
                    bindDatemulti(
                        data.date,
                        container.textView,
                        data.position == DayPosition.MonthDate
                    )
                }
            }
        }

        dialog.setOnCancelListener {
            selectedDates.clear()

        }
        ivCalendarForward.setOnClickListener {
            calendarView.scrollToMonth(currentMonth.plusMonths(1))
        }

        ivCalendarBack.setOnClickListener {
            calendarView.scrollToMonth(currentMonth.minusMonths(1))
        }

        tvVacCancel.setOnClickListener {
            selectedDates.clear()
            psLeaveType.dismiss()
            llVacationPopup.visibility = View.GONE
            llSelectedTimeDates.visibility = View.VISIBLE
            getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
        }

//        val localDateArrayList = arrayListOf(LocalDate.parse("2022-01-01"), LocalDate.parse("2022-01-02"))
//        val localDateArrayList = arrayListOf(selectedDates)
//        val formatter = SimpleDateFormat("MM/dd/yyyy")
//        val stringArrayList = localDateArrayList.map { formatter.format(it) }.toCollection(ArrayList())

        tvVacSubmit.setOnClickListener {
//            val localDateArrayList = arrayListOf(selectedDates)
//            val formattedDates = localDateArrayList.map { "\"$it\"" }.toCollection(ArrayList())
//            val stringArrayList = localDateArrayList.map { it.toString() }.toCollection(ArrayList())
//            convertArrayListToStringArray(formattedDates)
//

//            // converting arraylist to array
//            val dates_array: Array<String> = formattedDates.toTypedArray()
//
//            datesList.addAll(stringArrayList)
//
//
//            val datesList = arrayListOf<LocalDate> ()
//            datesList.addAll (selectedDates)
////            val datesArray = datesList.toTypedArray ()

            val datePattern = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val formattedDates = selectedDates.map { date -> date.format(datePattern) }
            val leaveDatesArray = JSONArray(formattedDates)

            if (selectedLeaveType.isBlank() || selectedLeaveType.isEmpty() || edtLeaveReason.text.isNullOrEmpty() || edtLeaveReason.text.isBlank()) {
                Toast.makeText(requireContext(), "Kindly Fill all", Toast.LENGTH_SHORT).show()
            } else {
                applyLeavesViewModel.applyLeaveRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    leaveDates = leaveDatesArray.toString(),
                    reason = edtLeaveReason.text.toString(),
                    type = selectedLeaveType
                )
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    fun dateClickedmulti(date: LocalDate) {
//        if (datesOfLeaveas.contains(date)) {
//        }else{
        if (selectedDates.contains(date)) {
            selectedDates.add(date)
//            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
        // We want to reload the footer text as well.
        calendarView.notifyMonthChanged(date.yearMonth)
//        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDatemulti(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        textView.text = date.dayOfMonth.toString()
        if (isSelectable) {
            when {
                datesOfLeaveas.contains(date) -> {
                    textView.setTextColor(resources.getColor(R.color.White))
                    textView.setBackgroundResource(R.drawable.darkblue_rect)
                }

                selectedDates.contains(date) -> {
                    textView.setTextColor(resources.getColor(R.color.White))
                    textView.setBackgroundResource(R.drawable.darkblue_rect)
                }

                today == date -> {
                    textView.setTextColor(resources.getColor(R.color.White))
                    textView.setBackgroundResource(R.drawable.darkblue_rect)
//                    textView.background = null
                }

                else -> {
                    textView.setTextColor(resources.getColor(R.color.Black))
                    textView.background = null
                }
            }
        } else {
            textView.setTextColor(resources.getColor(R.color.grey))
            textView.background = null
        }
    }
    private fun ProfileNameobservers() {
        homeViewModel.homeSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                binding!!.profileimage.loadFromUrl(
                    Const.IMG_URL + it.homeData.profilePic,
                    R.drawable.ic_placeholder
                )
                binding!!.tvName.text = it.homeData.userName

                for (i in it.homeData.expenseData.indices) {
                    categoryList.add(it.homeData.expenseData.get(i).name)
                }
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
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }

    }

    fun observer() {
        //ApplyLeave
        applyLeavesViewModel.applyLeaveSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                (it.message)
                selectedDates.clear()
            }
        })

        applyLeavesViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        applyLeavesViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }

        //GetLeave
        getLeavesViewModel.getLeavesSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                // showToast(it.message)
                leavedates = it.data.vacationsLeaves as ArrayList<String>
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                val getDatesfromApi = arrayListOf<LocalDate>()
                for (dateString in leavedates) {
                    val trimmedString = dateString.toString()
                    val parsedDate = LocalDate.parse(trimmedString, formatter)
                    getDatesfromApi.add(parsedDate)
                }
                datesOfLeaveas = getDatesfromApi
//                selectedDates = getDatesfromApi
            }
        })

        getLeavesViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        getLeavesViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
        //dayClockViewModel
        dayClockInViewModel.dayClockInSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                if (it.message == "Checked In Successful") {
//                    binding!!.switchStartShift.isChecked = true
                    sharedPref.setBoolean(Const.SWITCH, true)
                    homeViewModel.homeRequest(
                        sharedPref.getString(Const.TOKEN),
                        filterDate = today.toString()
                    )
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                } else if (it.message == "Please provide lat") {
                    sharedPref.setBoolean(Const.SWITCH, false)
                    Toast.makeText(
                        context,
                        "Please allow location permission from app settings",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (it.message == "Can't clockout before one hour") {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
                } else {
                    sharedPref.setBoolean(Const.SWITCH, false)
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
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
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
        //AddExpense
        addExpenseViewModel.addExpenseSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                Toast.makeText(context, "Expense added successfully", Toast.LENGTH_LONG).show()

//                image.recycle()
                // Handle success: You can update UI or perform other actions here
            } else {
//                showToast(response.message ?: "Error adding expense")
                // Handle error: Display an appropriate message
            }
        }

        addExpenseViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        addExpenseViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }

    }
    private fun clocklistObsserver(){
        clockInListViewModel.clockInListSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                for (i in it.clockinlistData.projects.indices) {
                    projectNameList.add(it.clockinlistData.projects.get(i).fullName)
                }
            }
        })

        clockInListViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
//                showToast(it)
            }
        }

        clockInListViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showProgessBar()
            } else {
                hideProgessBar()
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


    // Camera Code
    fun requestCameraPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE_PROFILE
        )
    }

    fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            false
        } else true
    }

    fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val image = data?.extras?.get("data") as? Bitmap
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
                expensePopup(imageBase64)

            } else {
                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun expensePopup(imageBase64: String) {
        val dialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.popup_expense, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.apply { setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.setContentView(dialogView)
        val ivExpenseImage = dialog.findViewById<CircleImageView>(R.id.ivExpenseImage)
        val edtTitle = dialog.findViewById<EditText>(R.id.edtTitle)
        val edtPrice = dialog.findViewById<EditText>(R.id.edtPrice)
        val psProjectName = dialog.findViewById<PowerSpinnerView>(R.id.psProjectName)
        val psSelectCategory = dialog.findViewById<PowerSpinnerView>(R.id.psSelectCategory)
        val cbExpnseRequire = dialog.findViewById<CheckBox>(R.id.cbExpnseRequire)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvSubmit = dialog.findViewById<TextView>(R.id.tvSubmit)

        val bitmap = base64ToBitmap(imageBase64)
        ivExpenseImage.setImageBitmap(bitmap)

        psProjectName.setItems(projectNameList)
        psSelectCategory.setItems(categoryList)

        psProjectName.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            projectName = newText
        }

        psSelectCategory.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            selectedCategory = newText
        }

        if (cbExpnseRequire.isChecked && cbExpnseRequire.isSelected) {
            reimbursement = "0"
        } else {
            reimbursement = "1"
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvSubmit.setOnClickListener {
            if (projectName.equals("")) {
                Toast.makeText(context, "Please select project name", Toast.LENGTH_LONG).show()
            } else if (edtTitle.text.isNullOrEmpty() || edtPrice.text.isBlank()) {
                Toast.makeText(context, "Please enter title", Toast.LENGTH_LONG).show()
            } else if (selectedCategory.isBlank()) {
                Toast.makeText(context, "Please select category", Toast.LENGTH_LONG).show()
            } else if (edtPrice.text.isNullOrEmpty() || edtPrice.text.isBlank()) {
                Toast.makeText(context, "Please enter price", Toast.LENGTH_LONG).show()

            } else {
                addExpenseViewModel.addExpenseRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    title = edtTitle.text.toString(),
                    image = imageBase64,
                    category = selectedCategory,
                    price = edtPrice.text.toString(),
                    projectId = projectName,
                    reimbursement = reimbursement
                )
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun convertImageToBase64String(bitmap: Bitmap): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(
            Bitmap.CompressFormat.PNG,
            60,
            outputStream
        ) // Adjust quality as needed
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

}


















//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_IMAGE_CAPTURE -> {
//                // This is the result for CameraView1
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
//            REQUEST_IMAGE_CAPTURE -> {
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