package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentBuildingBinding
import com.poseidonapp.model.building.Building
import com.poseidonapp.ui.adapter.BuildingAdapter
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.addRequest.AddRequestViewModel
import com.poseidonapp.viewmodel.building.BuildingViewModel
import com.poseidonapp.workorder.activities.BaseFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BuildingFragment : BaseFragment(), HandleClickListener {
    var binding: FragmentBuildingBinding? = null
    lateinit var buildingViewModel: BuildingViewModel
    lateinit var addRequestViewModel: AddRequestViewModel
    var buildingLists: List<Building>? = null
    lateinit var sharedPref: SharedPref
    var buildingadapter: BuildingAdapter? = null
    val calendar = Calendar.getInstance()
    var organizationId=""
    var id = ""
    var name = ""
    var addressId=""
    var buildingId=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentBuildingBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        sharedPref = SharedPref(requireContext())
        val bundle: Bundle? = arguments
        id = bundle?.getString("id").toString()
        organizationId=bundle?.getString("organizationId").toString()
        Log.e("BuildingFragment", "initUI: organizationId:$organizationId")
        Log.e("BuildingFragment", "id: $id")
        buildingViewModel = ViewModelProvider(this)[BuildingViewModel::class.java]
        addRequestViewModel = ViewModelProvider(this)[AddRequestViewModel::class.java]
        buildingViewModel.buildingRequest(sharedPref.getString(Const.TOKEN), id)
        observer()
        searchView()
    }


    fun setAdapter(buildingLists: ArrayList<Building>) {
        var filteredList = ArrayList(buildingLists)
        if (filteredList.isEmpty()) {
            binding?.noDataFoundTV?.visibility = View.VISIBLE
            binding?.rvBuilding?.visibility = View.GONE
        } else {
            binding?.noDataFoundTV?.visibility = View.GONE
            binding?.rvBuilding?.visibility = View.VISIBLE
        }
        // Set the adapter
        buildingadapter = BuildingAdapter(filteredList) { position,data->
            buildingId=data.id
            addressId=data.addressId
            val bundle = Bundle()
            bundle.putString("buildingId", buildingId)
            bundle.putString("id",id)
            showCustomDialog(bundle)
        }

        binding!!.rvBuilding.layoutManager = LinearLayoutManager(context)
        binding!!.rvBuilding.adapter = buildingadapter
        buildingadapter!!.notifyDataSetChanged()
    }

    fun addBuildingObserver(){
        buildingViewModel.addBuildingRequest(sharedPref.getString(Const.TOKEN),id)

        buildingViewModel.addBuildingSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()

                buildingViewModel.buildingRequest(sharedPref.getString(Const.TOKEN), id)
                observer()

            }
        })
    }
    private fun addRequestObserver(){
        addRequestViewModel.addRequestSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()

            }
        })

        addRequestViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }

        addRequestViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
    }

    private fun observer() {
        buildingViewModel.buildingSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                buildingLists = it.data.buildings
                setAdapter(buildingLists as ArrayList)
                Log.e("gggggg","buildinglist"+buildingLists)
            }else{

            }
        })

        buildingViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }

        buildingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
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

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }

            R.id.addBuilding -> {
                showAddBuildingDialog()
            }
        }
    }

    fun searchView() {
        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the data based on the query
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = buildingLists!!.filter {
            it.name.contains(query!!, ignoreCase = true)
        }
        setAdapter(ArrayList(filteredList))
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddBuildingDialog() {
        val buildingdialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_addbuiilding, null)
        buildingdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        buildingdialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        buildingdialog.setContentView(dialogView)
        val tvAdd = dialogView.findViewById<TextView>(R.id.tvAdd)
        val ivCancel = dialogView.findViewById<ImageView>(R.id.ivCancel)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        ivCancel.setOnClickListener{
            buildingdialog.dismiss()
        }
        tvAdd.setOnClickListener{
            if (etName.text.toString().equals("")){
                Toast.makeText(context,"Please fill the fields", Toast.LENGTH_LONG).show()
            }else{
                val addressId= id
                Log.e("addressId", "addressid: $id" )
                buildingViewModel!!.addBuildingRequest(
                    addressId =addressId,
                    name = etName.text.toString(),
                )
                buildingdialog.dismiss()
                addBuildingObserver()
            }
        }
        buildingdialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showCustomDialog(bundle: Bundle) {
        val customdialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.custom_bottomsheet, null)
        customdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customdialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        customdialog.setContentView(dialogView)
        val tvAddRequest = dialogView.findViewById<TextView>(R.id.tvAddRequest)
        val tvSystemManagement = dialogView.findViewById<TextView>(R.id.tvSystemManagement)
        val tvcancel = dialogView.findViewById<TextView>(R.id.tvcancel)
        tvAddRequest.setOnClickListener{
            showAddRequestDialog()
            customdialog.dismiss()
        }
        tvSystemManagement.setOnClickListener{
            findNavController().navigate(R.id.systemManagmentFragment,bundle)
            customdialog.dismiss()
        }
        tvcancel.setOnClickListener {
            customdialog.dismiss()
        }

        customdialog.show()
    }
    @SuppressLint("MissingInflatedId")
    private fun showAddRequestDialog() {
        val addRequestDialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.add_request, null)
        addRequestDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addRequestDialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        addRequestDialog.setContentView(dialogView)

        val etBillTo = dialogView.findViewById<EditText>(R.id.etBillto)
        val assignDateSelectedTv = dialogView.findViewById<TextView>(R.id.assignDateSelectedTv)
        val assignTimeSelectedTv = dialogView.findViewById<TextView>(R.id.assignTimeSelectedTv)
        val etNotes = dialogView.findViewById<EditText>(R.id.etNotes)
        val ivCross = dialogView.findViewById<ImageView>(R.id.ivCross)
        val tvSubmit = dialogView.findViewById<TextView>(R.id.tvSubmitt)
        val inspectorTypeList = arrayOf("Quarterly", "Semi-Annual", "Annual", "Extinguishers", "Pump Inspection")
        val spinnerInspectionType = dialogView.findViewById<Spinner>(R.id.spinner)

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, inspectorTypeList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInspectionType.adapter = arrayAdapter

        tvSubmit.setOnClickListener {
            val selectedInspectionType = spinnerInspectionType.selectedItem?.toString() ?: ""
            if (etBillTo.text.isEmpty() || selectedInspectionType.isEmpty()
                || assignTimeSelectedTv.text.isEmpty() || etNotes.text.isEmpty() || assignDateSelectedTv.text.isEmpty()) {

                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                lifecycleScope.launch {
                    addRequestViewModel.addRequestRequest(
                        billTo = etBillTo.text.toString(),
                        inspectionType = selectedInspectionType,
                        assignDate = assignDateSelectedTv.text.toString(),
                        notes = etNotes.text.toString(),
                        organizationId = organizationId,
                        addressId = addressId,
                        buildingId = buildingId,
                        assignTime = assignTimeSelectedTv.text.toString(),
                        sessionToken = sharedPref.getString(Const.TOKEN)
                    )
                    addRequestDialog.dismiss()
                    addRequestObserver()
                }
            }
        }

        assignDateSelectedTv.setOnClickListener {
            selectDate { selectedDate ->
                assignDateSelectedTv.text = selectedDate
                assignDateSelectedTv.setTextColor(resources.getColor(R.color.Black))
            }
        }

        assignTimeSelectedTv.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                assignTimeSelectedTv.text = selectedTime
                assignTimeSelectedTv.setTextColor(resources.getColor(R.color.Black))
            }
        }
        ivCross.setOnClickListener{
            addRequestDialog.dismiss()
        }
        addRequestDialog.show()
    }




    fun selectDate(onDateSelected: (String) -> Unit) {
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
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                    onDateSelected(formattedDate)
                } else {
                    Toast.makeText(context, "Please select a future date", Toast.LENGTH_SHORT).show()
                }
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val currentTime = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)

                if (selectedTime.timeInMillis >= currentTime.timeInMillis) {
                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val formattedTime = dateFormat.format(selectedTime.time)
                    onTimeSelected(formattedTime)
                } else {
                    Toast.makeText(context, "Please select a future time", Toast.LENGTH_SHORT).show()
                }
            },
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }

}