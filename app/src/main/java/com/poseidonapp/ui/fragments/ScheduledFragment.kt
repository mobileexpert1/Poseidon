package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.poseidonapp.databinding.FragmentScheduledBinding
import com.poseidonapp.model.organization.Organization
import com.poseidonapp.model.schedules.MeetingsItem
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.GetLeavesViewModel
import com.poseidonapp.viewmodel.applyleaves.ApplyLeavesViewModel
import com.poseidonapp.viewmodel.schedules.SchedulesViewModel
import com.poseidonapp.workorder.activities.BaseFragment
import com.skydoves.powerspinner.PowerSpinnerView
import org.json.JSONArray
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
class ScheduledFragment : BaseFragment(), HandleClickListener {
    var binding: FragmentScheduledBinding? = null
    lateinit var getLeavesViewModel: GetLeavesViewModel
    lateinit var applyLeavesViewModel: ApplyLeavesViewModel
    val leaveTypeArray = arrayOf("Unpaid", "Paid")
    var selectedLeaveType: String = ""
    lateinit var calendarView: CalendarView
    lateinit var currentMonth: YearMonth
    private var selectedDates = ArrayList<LocalDate>()
    private var datesOfLeaveas = ArrayList<LocalDate>()
    private val today = LocalDate.now()
    lateinit var sharedPref: SharedPref



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentScheduledBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        sharedPref = SharedPref(requireContext())
        getLeavesViewModel = ViewModelProvider(this)[GetLeavesViewModel::class.java]
        applyLeavesViewModel = ViewModelProvider(this)[ApplyLeavesViewModel::class.java]
    }

    override fun onViewClick(view: View) {
        TODO("Not yet implemented")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun selectMultipleDates() {
        if (!isAdded) return

//        getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_select_multiple_dates)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
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
                            Toast.makeText(requireContext(), "You cannot select this date", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if (!day.date.isBefore(today)) {
//                            if (selectedDates.size >=2) {
//                            } else {
                                dateClickedmulti(date = day.date)
                                if (selectedDates.size>=2){
                                    llVacationPopup.visibility = View.VISIBLE
                                    llSelectedTimeDates.visibility = View.GONE
                                }
//                            }
                            } else {
                                Toast.makeText(requireContext(), "You cannot select this date", Toast.LENGTH_SHORT).show()
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

        tvSubmit.setOnClickListener {

        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
            selectedDates.clear()
        }

        tvVacCancel.setOnClickListener {
            selectedDates.clear()
            llVacationPopup.visibility = View.GONE
            llSelectedTimeDates.visibility = View.VISIBLE
//            getLeavesViewModel.getLeavesRequest(sharedPref.getString(Const.TOKEN))
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
                Toast.makeText(requireContext(),"Kindly Fill all", Toast.LENGTH_SHORT).show()
            } else {
//                applyLeavesViewModel.applyLeaveRequest(
//                    sessionToken = sharedPref.getString(Const.TOKEN),
//                    leaveDates = leaveDatesArray.toString(),
//                    reason = edtLeaveReason.text.toString(),
//                    type = selectedLeaveType
//                )
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
                    textView.setBackgroundResource(R.drawable.red_rect)
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

}



