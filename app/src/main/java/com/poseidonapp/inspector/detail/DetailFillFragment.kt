package com.poseidonapp.inspector.detail

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentDetailFillBinding
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.AddInspectionDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailFillFragment : BaseFragment(), HandleClickListener {

    var addInspectionDetailViewModel: AddInspectionDetailViewModel? = null
    var binding: FragmentDetailFillBinding? = null
    private val calendar = Calendar.getInstance()

//    ["Quarterly", "1st Quarterly", "2nd Quarterly", "3rd Quarterly", "Semi-Annual", "Annual", "Standpipe", "Dry Valve", "Preaction", "Extinguishers", "Pump Inspection", "5 Year", "3-Year Dry Trip Test"]
//    val inspectorTypeList = arrayOf("Quarterly", "Semi-Annual", "Annual", "Extinguishers","Pump Inspection")
    val inspectorTypeList = arrayOf("Quarterly", "1st Quarterly", "2nd Quarterly", "3rd Quarterly", "Semi-Annual", "Annual", "Standpipe", "Dry Valve", "Preaction", "Extinguishers", "Pump Inspection", "5 Year", "3-Year Dry Trip Test")
    var type=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentDetailFillBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        addInspectionDetailViewModel = ViewModelProvider(this)[AddInspectionDetailViewModel::class.java]
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_view, inspectorTypeList.toList())
        binding!!.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    fun selectDate() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
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

                    binding!!.assignDateSelectedTv.setText(formattedDate)
                    binding!!.assignDateSelectedTv.setTextColor(resources.getColor(R.color.Black))
                    // Update your UI or perform any other actions with the formatted date
                }
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }


    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeButtonText()
                /* if (selectedTime >= currentTime) {
                    // Do something with the selected time
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updateTimeButtonText()
                } else {
                    showToast("Please select a future time.")

                }*/
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }

    private fun updateTimeButtonText() {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val selectedTime = calendar.time
        val formattedTime = dateFormat.format(selectedTime)
        binding!!.assignTimeSelectedTv.text = formattedTime
        binding!!.assignTimeSelectedTv.setTextColor(resources.getColor(R.color.Black))

    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.assignDateSelectedTv -> {
                selectDate()
            }

            R.id.assignTimeSelectedTv -> {
                showTimePickerDialog()
            }

            R.id.ivBack->{
//                baseActivity!!.onBackPressed()
                findNavController().popBackStack()

            }
            R.id.autoCompleteTextView -> {
//                val languages = resources.getStringArray(R.array.inspectorType)
                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_view,
                    inspectorTypeList.toList()
                )
                binding!!.autoCompleteTextView.setAdapter(arrayAdapter)

                    // Set an OnItemClickListener to capture the selected index
                binding!!.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                    // 'position' is the selected index in the list
                    val selectedIndex = position
                    type= selectedIndex+1
                    val selectedItem = inspectorTypeList[selectedIndex]

                    // Use selectedIndex or selectedItem as needed
//                    println("Selected index: $selectedIndex")
                    Log.d("DetailFill", "Selected index: $selectedIndex" )
                    Log.d("DetailFill", "Selected type: $type" )
                    Log.d("DetailFill", "Selected item: $selectedItem" )
//                    println("Selected item: $selectedItem")
                }

            }

            R.id.nextButton -> {

                if (binding!!.nameET.text!!.isEmpty()) {
                    showToast("Please enter name.")
                } else if (!isEmailValid(binding!!.emailET.text.toString())) {
                    showToast("Please enter valid email.")
                } else if (!isValidPhoneNumber(binding!!.phoneET.text!!.toString())) {
                    showToast("Please enter valid phone number.")
                } else if (binding!!.stateET.text!!.isEmpty()) {
                    showToast("Please enter state.")
                } else if (binding!!.cityET.text!!.isEmpty()) {
                    showToast("Please enter city.")
                } else if (isValidPostalCode(binding!!.postalCodeET.text!!.toString())) {
                    showToast("Please enter valid postal code.")
                } else if (binding!!.addressLineOneET.text!!.isEmpty()) {
                    showToast("Please enter address.")
                } else if (binding!!.billToET.text!!.isEmpty()) {
                    showToast("Please enter bill to.")
                } else if (binding!!.assignDateSelectedTv.text!!.isEmpty()) {
                    showToast("Please select Date.")
                } else if (binding!!.assignTimeSelectedTv.text!!.isEmpty()) {
                    showToast("Please select time.")
                } else if (binding!!.autoCompleteTextView.text!!.isEmpty()) {
                    showToast("Please select inspector type.")
                } else if (binding!!.etBuildingName.text!!.isEmpty()) {
                    showToast("Please enter building name.")
                } else {
                    if (isInternetAvail()) {
//                        addInspectionDetailViewModel!!.addInspectionDetail(
//                            sharedPref.getString(Const.TOKEN),
//                            binding!!.nameET.text!!.toString(),binding!!.nameET.text!!.toString(),
                        //                            binding!!.emailET.text!!.toString(),
                        //                            binding!!.phoneET.text!!.toString(),
                        //                            binding!!.addressLineOneET.text!!.toString(),
                        //                            binding!!.cityET.text!!.toString(),binding!!.stateET.text!!.toString(),
                        //                            binding!!.postalCodeET.text!!.toString(),
                        //                            binding!!.assignDateSelectedTv.text!!.toString(),
                        //                            binding!!.assignTimeSelectedTv.text!!.toString(),
                        //                            binding!!.autoCompleteTextView.text!!.toString()
//                        )
                        addInspectionDetailViewModel!!.addInspectionDetail(
                            sessionToken =  sharedPref.getString(Const.TOKEN),
                            fullName = binding!!.nameET.text!!.toString(),
                            emailAddress = binding!!.emailET.text!!.toString(),
                            phoneNumber = binding!!.phoneET.text!!.toString(),
                            adddressLine1 = binding!!.addressLineOneET.text!!.toString(),
                            billTo = binding!!.billToET.text!!.toString(),
                            city = binding!!.cityET.text!!.toString(),
                            state = binding!!.stateET.text!!.toString(),
                            postalCode = binding!!.postalCodeET.text!!.toString(),
                            assignDate = binding!!.assignDateSelectedTv.text!!.toString(),
                            assignTime = binding!!.assignTimeSelectedTv.text!!.toString(),
                            inspectionType = binding!!.autoCompleteTextView.text!!.toString(),
                            type = type.toString(),
                            buildingName = binding!!.etBuildingName.text!!.toString()
                        )
                        observers()
                    } else {
                        showToast(getString(R.string.internet_error))
                    }
                }


            }


        }
    }

    fun observers() {
        addInspectionDetailViewModel?.ispectionDetails?.observe(this, Observer {
            if (it.status == true) {
//                requireActivity().supportFragmentManager.popBackStack()
                findNavController().popBackStack()
            }
        })

        addInspectionDetailViewModel?.apiError?.observe(this) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        addInspectionDetailViewModel?.isLoading?.observe(this) {
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