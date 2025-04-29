package com.poseidonapp.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentTimeSheetBinding
import com.poseidonapp.model.weektimesheetBody.RequestData
import com.poseidonapp.model.weektimesheetBody.WeekTimeSheetBody
import com.poseidonapp.ui.adapter.TimeSheetAdapter
import com.poseidonapp.ui.adapter.timesheetdate.DateAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.weektimesheet.WeekTimeSheetViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale


class TimeSheetFragment : BaseFragment(), HandleClickListener {

//    , TimeSheetAdapter.ClickListeners

    var binding: FragmentTimeSheetBinding? = null
    private var timeSheetAdapter: TimeSheetAdapter? = null
    lateinit var weekTimeSheetViewModel: WeekTimeSheetViewModel
    var weekName=""

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
            binding = FragmentTimeSheetBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }



    private fun initUI() {
        binding!!.handleClick = this
        weekTimeSheetViewModel = ViewModelProvider(this)[WeekTimeSheetViewModel::class.java]
        binding!!.apply {
            // Get the list of dates for the current month
//            val dates = generateMonthDates()
//            val currentDate = LocalDate.now()
//
//            // Create the adapter and set it to the RecyclerView
//            val adapter = DateAdapter(dates, currentDate) { selectedDate ->
//                // Handle date selection
//                Toast.makeText(requireContext(), "Selected: $selectedDate", Toast.LENGTH_SHORT).show()
//            }
//
//            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            recyclerView.adapter = adapter


            // Get the list of dates for the current month
            val dates = generateMonthDates()
            val currentDate = LocalDate.now()

            val adapter = DateAdapter(dates, currentDate) { weekdayName ->
                weekName=weekdayName
            }

            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapter



            // Calculate the index of the current date and scroll to it
            val currentDateIndex = dates.indexOf(currentDate)
            if (currentDateIndex != -1) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (currentDateIndex > firstVisibleItemPosition && currentDateIndex < lastVisibleItemPosition) {
                    return
                }

                recyclerView.scrollToPosition(currentDateIndex)
            }
        }

//        timeSheetAdapter = TimeSheetAdapter(baseActivity!!, weekdaysArray, this)
//        val linearLayoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        binding!!.rvDaysSheet.adapter = timeSheetAdapter
//        binding!!.rvDaysSheet.layoutManager = linearLayoutManager

        observers()

        mondaySheet()
//        tuesdaySheet()
//        wednesdaySheet()
//        thursdaySheet()
//        fridaySheet()
    }


    // Generate a list of dates for the current week (Monday to Sunday)
    private fun generateWeekDates(): List<LocalDate> {
        val today = LocalDate.now()
        val weekDates = mutableListOf<LocalDate>()

        // Get the first day of the current week (Monday)
        val weekStart = today.with(WeekFields.of(Locale.getDefault()).firstDayOfWeek)
        for (i in 0..6) {
            weekDates.add(weekStart.plusDays(i.toLong()))  // Add each day of the week
        }

        return weekDates
    }


    // Generate a list of all dates in the current month
    private fun generateMonthDates(): List<LocalDate> {
        val today = LocalDate.now()
        val firstDayOfMonth = today.withDayOfMonth(1)
        val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        val monthDates = mutableListOf<LocalDate>()

        var date = firstDayOfMonth

        // Add dates to the list from the 1st to the last day of the month
        while (!date.isAfter(lastDayOfMonth)) {
            monthDates.add(date)
            date = date.plusDays(1)
        }

        return monthDates
    }

    fun mondaySheet() {
        binding!!.edtEnterjobnameMon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtEnterjobnameMon",
                    binding!!.edtEnterjobnameMon.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtEnterjobnameMon.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtEnterjobnameMon.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtEnterjobnameMon)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeInMon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeInMon", binding!!.edtTimeInMon.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeInMon.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeInMon.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeInMon)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeOutMon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeOutMon", binding!!.edtTimeOutMon.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeOutMon.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeOutMon.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeOutMon)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtHoursMon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtHoursMon", binding!!.edtHoursMon.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtHoursMon.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtHoursMon.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtHoursMon)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtDescriptionofWorkMon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtDescriptionofWorkMon",
                    binding!!.edtDescriptionofWorkMon.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtDescriptionofWorkMon.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtDescriptionofWorkMon.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtDescriptionofWorkMon)
                return@OnEditorActionListener true
            }
            false
        })


    }

/*
    fun tuesdaySheet() {
        binding!!.edtEnterjobnameTue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtEnterjobnameTue",
                    binding!!.edtEnterjobnameTue.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtEnterjobnameTue.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtEnterjobnameTue.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtEnterjobnameTue)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeInTue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeInTue", binding!!.edtTimeInTue.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeInTue.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeInTue.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeInTue)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeOutTue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeOutTue", binding!!.edtTimeOutTue.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeOutTue.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeOutTue.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeOutTue)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtHoursTue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtHoursTue", binding!!.edtHoursTue.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtHoursTue.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtHoursTue.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtHoursTue)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtDescriptionofWorkTue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtDescriptionofWorkTue",
                    binding!!.edtDescriptionofWorkTue.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtDescriptionofWorkTue.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtDescriptionofWorkTue.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtDescriptionofWorkTue)
                return@OnEditorActionListener true
            }
            false
        })


    }
*/

/*
    fun wednesdaySheet() {
        binding!!.edtEnterjobnameWed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtEnterjobnameWed",
                    binding!!.edtEnterjobnameWed.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtEnterjobnameWed.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtEnterjobnameWed.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtEnterjobnameWed)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeInWed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeInWed", binding!!.edtTimeInWed.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeInWed.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeInWed.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeInWed)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeOutWed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeOutWed", binding!!.edtTimeOutWed.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeOutWed.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeOutWed.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeOutWed)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtHoursWed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtHoursWed", binding!!.edtHoursWed.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtHoursWed.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtHoursWed.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtHoursWed)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtDescriptionofWorkWed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtDescriptionofWorkWed",
                    binding!!.edtDescriptionofWorkWed.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtDescriptionofWorkWed.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtDescriptionofWorkWed.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtDescriptionofWorkWed)
                return@OnEditorActionListener true
            }
            false
        })


    }
*/

/*
    fun thursdaySheet() {
        binding!!.edtEnterjobnameThu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtEnterjobnameThu",
                    binding!!.edtEnterjobnameThu.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtEnterjobnameThu.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtEnterjobnameThu.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtEnterjobnameThu)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeInThu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeInThu", binding!!.edtTimeInThu.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeInThu.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeInThu.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeInThu)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeOutThu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeOutThu", binding!!.edtTimeOutThu.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeOutThu.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeOutThu.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeOutThu)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtHoursThu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtHoursThu", binding!!.edtHoursThu.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtHoursThu.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtHoursThu.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtHoursThu)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtDescriptionofWorkThu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtDescriptionofWorkThu",
                    binding!!.edtDescriptionofWorkThu.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtDescriptionofWorkThu.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtDescriptionofWorkThu.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtDescriptionofWorkThu)
                return@OnEditorActionListener true
            }
            false
        })


    }
*/

/*
    fun fridaySheet() {
        binding!!.edtEnterjobnameFri.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtEnterjobnameFri",
                    binding!!.edtEnterjobnameFri.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtEnterjobnameFri.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtEnterjobnameFri.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtEnterjobnameFri)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeInFri.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeInFri", binding!!.edtTimeInFri.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeInFri.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeInFri.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeInFri)
                return@OnEditorActionListener true
            }
            false
        })



        binding!!.edtTimeOutFri.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtTimeOutFri", binding!!.edtTimeOutFri.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtTimeOutFri.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtTimeOutFri.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtTimeOutFri)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtHoursFri.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString("edtHoursFri", binding!!.edtHoursFri.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtHoursFri.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtHoursFri.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtHoursFri)
                return@OnEditorActionListener true
            }
            false
        })




        binding!!.edtDescriptionofWorkFri.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sharedPref.setString(
                    "edtDescriptionofWorkFri",
                    binding!!.edtDescriptionofWorkFri.getText().toString()
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // focus and keyboard hide
        binding!!.edtDescriptionofWorkFri.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //do here your stuff f
                binding!!.edtDescriptionofWorkFri.clearFocus()
                hideKeyboardFrom(requireContext(), binding!!.edtDescriptionofWorkFri)
                return@OnEditorActionListener true
            }
            false
        })


    }
*/


    override fun onResume() {
        super.onResume()


        binding!!.edtEnterjobnameMon.setText(sharedPref.getString("edtEnterjobnameMon"))
        binding!!.edtTimeInMon.setText(sharedPref.getString("edtTimeInMon"))
        binding!!.edtTimeOutMon.setText(sharedPref.getString("edtTimeOutMon"))
        binding!!.edtHoursMon.setText(sharedPref.getString("edtHoursMon"))
        binding!!.edtDescriptionofWorkMon.setText(sharedPref.getString("edtDescriptionofWorkMon"))

//        binding!!.edtEnterjobnameTue.setText(sharedPref.getString("edtEnterjobnameTue"))
//        binding!!.edtTimeInTue.setText(sharedPref.getString("edtTimeInTue"))
//        binding!!.edtTimeOutTue.setText(sharedPref.getString("edtTimeOutTue"))
//        binding!!.edtHoursTue.setText(sharedPref.getString("edtHoursTue"))
//        binding!!.edtDescriptionofWorkTue.setText(sharedPref.getString("edtDescriptionofWorkTue"))
//
//        binding!!.edtEnterjobnameWed.setText(sharedPref.getString("edtEnterjobnameWed"))
//        binding!!.edtTimeInWed.setText(sharedPref.getString("edtTimeInWed"))
//        binding!!.edtTimeOutWed.setText(sharedPref.getString("edtTimeOutWed"))
//        binding!!.edtHoursWed.setText(sharedPref.getString("edtHoursWed"))
//        binding!!.edtDescriptionofWorkWed.setText(sharedPref.getString("edtDescriptionofWorkWed"))
//
//        binding!!.edtEnterjobnameThu.setText(sharedPref.getString("edtEnterjobnameThu"))
//        binding!!.edtTimeInThu.setText(sharedPref.getString("edtTimeInThu"))
//        binding!!.edtTimeOutThu.setText(sharedPref.getString("edtTimeOutThu"))
//        binding!!.edtHoursThu.setText(sharedPref.getString("edtHoursThu"))
//        binding!!.edtDescriptionofWorkThu.setText(sharedPref.getString("edtDescriptionofWorkThu"))
//
//        binding!!.edtEnterjobnameFri.setText(sharedPref.getString("edtEnterjobnameFri"))
//        binding!!.edtTimeInFri.setText(sharedPref.getString("edtTimeInFri"))
//        binding!!.edtTimeOutFri.setText(sharedPref.getString("edtTimeOutFri"))
//        binding!!.edtHoursFri.setText(sharedPref.getString("edtHoursFri"))
//        binding!!.edtDescriptionofWorkFri.setText(sharedPref.getString("edtDescriptionofWorkFri"))


    }


    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    companion object {

    }


    private fun submitCheckAllFields(): Boolean {

        if (binding!!.edtEnterjobnameMon.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeInMon.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeOutMon.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtHoursMon.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtDescriptionofWorkMon.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } /*else if (binding!!.edtEnterjobnameTue.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeInTue.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeOutTue.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtHoursTue.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtDescriptionofWorkTue.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtEnterjobnameWed.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeInWed.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeOutWed.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtHoursWed.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtDescriptionofWorkWed.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtEnterjobnameThu.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeInThu.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeOutThu.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtHoursThu.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtDescriptionofWorkThu.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtEnterjobnameFri.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeInFri.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtTimeOutFri.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtHoursFri.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        } else if (binding!!.edtDescriptionofWorkFri.getText().toString().isEmpty()) {
            showToast(resources.getString(R.string.fill_timesheet_completely))
            return false
        }*/ else {
            return true
        }

    }




    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                    findNavController().popBackStack()
//                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.tvSubmit -> {
                if (submitCheckAllFields()) {
/*
                    val weekDataArray = listOf(
                        WeekTimeSheetBody(
                            jobName = binding!!.edtEnterjobnameMon.getText().toString(),
                            inTime = binding!!.edtTimeInMon.getText().toString(),
                            hrs = binding!!.edtHoursMon.getText().toString(),
                            descriptionOfWork = binding!!.edtDescriptionofWorkMon.getText()
                                .toString(),
                            weekName = "Mon",
                            outTime = binding!!.edtTimeOutMon.getText().toString()
                        ),
                        WeekTimeSheetBody(
                            jobName = binding!!.edtEnterjobnameTue.getText().toString(),
                            inTime = binding!!.edtTimeInTue.getText().toString(),
                            hrs = binding!!.edtHoursTue.getText().toString(),
                            descriptionOfWork = binding!!.edtDescriptionofWorkTue.getText()
                                .toString(),
                            weekName = "Tue",
                            outTime = binding!!.edtTimeOutTue.getText().toString()
                        ),
                        WeekTimeSheetBody(
                            jobName = binding!!.edtEnterjobnameWed.getText().toString(),
                            inTime = binding!!.edtTimeInWed.getText().toString(),
                            hrs = binding!!.edtHoursWed.getText().toString(),
                            descriptionOfWork = binding!!.edtDescriptionofWorkWed.getText()
                                .toString(),
                            weekName = "Wed",
                            outTime = binding!!.edtTimeOutWed.getText().toString()
                        ),
                        WeekTimeSheetBody(
                            jobName = binding!!.edtEnterjobnameThu.getText().toString(),
                            inTime = binding!!.edtTimeInThu.getText().toString(),
                            hrs = binding!!.edtHoursThu.getText().toString(),
                            descriptionOfWork = binding!!.edtDescriptionofWorkThu.getText()
                                .toString(),
                            weekName = "Thu",
                            outTime = binding!!.edtTimeOutThu.getText().toString()
                        ),

                        WeekTimeSheetBody(
                            jobName = binding!!.edtEnterjobnameFri.getText().toString(),
                            inTime = binding!!.edtTimeInFri.getText().toString(),
                            hrs = binding!!.edtHoursFri.getText().toString(),
                            descriptionOfWork = binding!!.edtDescriptionofWorkFri.getText()
                                .toString(),
                            weekName = "Fri",
                            outTime = binding!!.edtTimeOutFri.getText().toString()
                        ),
                    )
*/

//                    My JSON String: [{"weekName":"Wednesday","outTime":"09:43 PM","inTime":"08:43 PM","jobName":"cxvcxvcx","descriptionOfWork":"Vvxcvxcvcvc","hrs":"1"}]
                    val weekDataArray = listOf(
                        WeekTimeSheetBody(
                            jobName = binding!!.edtEnterjobnameMon.getText().toString(),
                            inTime = binding!!.edtTimeInMon.getText().toString(),
                            hrs = binding!!.edtHoursMon.getText().toString(),
                            descriptionOfWork = binding!!.edtDescriptionofWorkMon.getText()
                                .toString(),
                            weekName = weekName,
                            outTime = binding!!.edtTimeOutMon.getText().toString()
                        )
                    )
                    val gson = Gson()
                    val weekDataJson = gson.toJson(weekDataArray)
                    weekTimeSheetViewModel.weekTimeSheetRequest(sessionToken = sharedPref.getString(Const.TOKEN), weekData = weekDataJson)

                } else {

                }
            }
            R.id.edtTimeInMon -> {
                // Get the current time
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                // Initialize the TimePickerDialog
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                        // Format the selected time in 12-hour format with AM/PM
                        val formattedTime = formatTimeTo12Hour(selectedHour, selectedMinute)
                        binding!!.edtTimeInMon.text = formattedTime
                    },
                    hour, // Initial hour
                    minute, // Initial minute
                    false // 24-hour format set to false for 12-hour format
                )

                // Show the TimePickerDialog
                timePickerDialog.show()
            }
            R.id.edtTimeOutMon -> {
// Get the current time
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                // Initialize the TimePickerDialog
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                        // Format the selected time in 12-hour format with AM/PM
                        val formattedTime = formatTimeTo12Hour(selectedHour, selectedMinute)
                        binding!!.edtTimeOutMon.text = formattedTime
                    },
                    hour, // Initial hour
                    minute, // Initial minute
                    false // 24-hour format set to false for 12-hour format
                )

                // Show the TimePickerDialog
                timePickerDialog.show()
            }


        }
    }


    // Function to format time to 12-hour format with AM/PM
    private fun formatTimeTo12Hour(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM
        return format.format(calendar.time)
    }

    private fun observers() {

        weekTimeSheetViewModel.weekTimeSheetSuccess.observe(viewLifecycleOwner, Observer {

            if (it.status==true){

                sharedPref.setString("edtEnterjobnameMon", "")
                sharedPref.setString("edtTimeInMon", "")
                sharedPref.setString("edtTimeOutMon", "")
                sharedPref.setString("edtHoursMon", "")
                sharedPref.setString("edtDescriptionofWorkMon", "")

                binding!!.edtEnterjobnameMon.setText("")
                binding!!.edtTimeInMon.setText("")
                binding!!.edtTimeOutMon.setText("")
                binding!!.edtHoursMon.setText("")
                binding!!.edtDescriptionofWorkMon.setText("")
                showToast(it.message)
            }

        })

        weekTimeSheetViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        weekTimeSheetViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

    }

/*
    override fun onclick(position: Int) {



        if (position == 0) {
            binding!!.llMonSheet.visibility = View.VISIBLE
            binding!!.llTueSheet.visibility = View.GONE
            binding!!.llWedSheet.visibility = View.GONE
            binding!!.llThuSheet.visibility = View.GONE
            binding!!.llFriSheet.visibility = View.GONE
        } else if (position == 1) {
            binding!!.llMonSheet.visibility = View.GONE
            binding!!.llTueSheet.visibility = View.VISIBLE
            binding!!.llWedSheet.visibility = View.GONE
            binding!!.llThuSheet.visibility = View.GONE
            binding!!.llFriSheet.visibility = View.GONE
        } else if (position == 2) {
            binding!!.llMonSheet.visibility = View.GONE
            binding!!.llTueSheet.visibility = View.GONE
            binding!!.llWedSheet.visibility = View.VISIBLE
            binding!!.llThuSheet.visibility = View.GONE
            binding!!.llFriSheet.visibility = View.GONE
        } else if (position == 3) {
            binding!!.llMonSheet.visibility = View.GONE
            binding!!.llTueSheet.visibility = View.GONE
            binding!!.llWedSheet.visibility = View.GONE
            binding!!.llThuSheet.visibility = View.VISIBLE
            binding!!.llFriSheet.visibility = View.GONE
        } else if (position == 4) {
            binding!!.llMonSheet.visibility = View.GONE
            binding!!.llTueSheet.visibility = View.GONE
            binding!!.llWedSheet.visibility = View.GONE
            binding!!.llThuSheet.visibility = View.GONE
            binding!!.llFriSheet.visibility = View.VISIBLE
        }

    }
*/

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