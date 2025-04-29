package com.poseidonapp.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentDetailsForInstallationBinding
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.installation.InstallationAddAssignedViewModel
import java.text.SimpleDateFormat
import java.util.*


class DetailsForInstallationFragment : BaseFragment(),HandleClickListener {
    var binding: FragmentDetailsForInstallationBinding? = null
    var installationAddAssignedViewModel: InstallationAddAssignedViewModel? = null
    private val calendar = Calendar.getInstance()

    val installationTypeList = arrayOf("Quarterly", "Semi-Annual", "Annual", "Extinguishers")
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
            binding = FragmentDetailsForInstallationBinding.inflate(inflater, container, false)
        initUI()
        observers()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        installationAddAssignedViewModel = ViewModelProvider(this)[InstallationAddAssignedViewModel::class.java]
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_view, installationTypeList.toList())
        binding!!.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                baseActivity!!.onBackPressed()
            }
            R.id.assignDateSelectedTv->{
                selectDate()
            }
            R.id.assignTimeSelectedTv -> {
                showTimePickerDialog()
            }
            R.id.autoCompleteTextView -> {
                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_view,
                    installationTypeList.toList())
                binding!!.autoCompleteTextView.setAdapter(arrayAdapter)
            }

            R.id.nextButton -> {

                if (binding!!.nameET.text!!.isEmpty()) {
                    showToast("Please enter name.")
                }else if (binding!!.emailET.text!!.isEmpty()){
                    showToast("Please enter email.")
                } else if (!isEmailValid(binding!!.emailET.text.toString())) {
                    showToast("Please enter valid email.")
                } else if (!isValidPhoneNumber(binding!!.phoneET.text!!.toString())) {
                    showToast("Please enter valid phone number.")
                } else if (binding!!.stateET.text!!.isEmpty()) {
                    showToast("Please enter state.")
                } else if (binding!!.cityET.text!!.isEmpty()) {
                    showToast("Please enter city.")
                } else if(binding!!.postalCodeET.text!!.isEmpty()){
                    showToast("Please enter postal code.")
                } else if (isValidPostalCode(binding!!.postalCodeET.text!!.toString())) {
                    showToast("Please enter valid postal code.")
                } else if (binding!!.addressLineOneET.text!!.isEmpty()) {
                    showToast("Please enter address.")
                } /*else if (binding!!.billToET.text!!.isEmpty()) {
                    showToast("Please enter bill to.")
                }*/ else if (binding!!.assignDateSelectedTv.text!!.isEmpty()) {
                    showToast("Please select Date.")
                } else if (binding!!.assignTimeSelectedTv.text!!.isEmpty()) {
                    showToast("Please select time.")
                }/*else if (binding!!.autoCompleteTextView.text!!.isEmpty()) {
                    showToast("Please select inspector type.")
                }*/ else {
                    if (isInternetAvail()) {
                        installationAddAssignedViewModel!!.addAssignedRequest(
                            sessionToken =  sharedPref.getString(Const.TOKEN),
                            propertyName = binding!!.nameET.text!!.toString(),
                            email = binding!!.emailET.text!!.toString(),
                            phoneNumber = binding!!.phoneET.text!!.toString(),
                            state = binding!!.stateET.text!!.toString(),
                            city = binding!!.cityET.text!!.toString(),
                            postalCode = binding!!.postalCodeET.text!!.toString(),
                            address = binding!!.addressLineOneET.text!!.toString(),
                            date = binding!!.assignDateSelectedTv.text!!.toString(),
                            assignedTime = binding!!.assignTimeSelectedTv.text!!.toString(),
                        )

                    } else {
                        showToast(getString(R.string.internet_error))
                    }
                }


            }

        }
    }


    private fun observers() {

        installationAddAssignedViewModel!!.installationSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == true) {
                showToast(it.message)
                baseActivity!!.onBackPressed()
            }
        })

        installationAddAssignedViewModel!!.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        installationAddAssignedViewModel!!.isLoading.observe(viewLifecycleOwner) {
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
            sharedPref.clearPref()
            context?.deleteDatabase(DatabaseHelper.DATABASE_NAME)
            baseActivity!!.gotoLoginSignUpActivity()
        }
        dialog.show()
    }
    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)

                if (selectedTime >= currentTime) {
                    // Do something with the selected time
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updateTimeButtonText()
                } else {
                    showToast("Please select a future time.")

                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
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
                        "%04d-%02d %02d",
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

    private fun updateTimeButtonText() {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val selectedTime = calendar.time
        val formattedTime = dateFormat.format(selectedTime)
        binding!!.assignTimeSelectedTv.text = formattedTime
        binding!!.assignTimeSelectedTv.setTextColor(resources.getColor(R.color.Black))

    }

}