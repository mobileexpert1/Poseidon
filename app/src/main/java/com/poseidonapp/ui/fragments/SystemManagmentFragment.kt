package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentBuildingBinding
import com.poseidonapp.databinding.FragmentSystemManagmentBinding
import com.poseidonapp.model.address.Addres
import com.poseidonapp.model.building.Building
import com.poseidonapp.model.systemManagement.SystemLocation
import com.poseidonapp.ui.adapter.AddressesAdapter
import com.poseidonapp.ui.adapter.BuildingAdapter
import com.poseidonapp.ui.adapter.SystemManagmentAdapter
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.address.AddressViewModel
import com.poseidonapp.viewmodel.building.BuildingViewModel
import com.poseidonapp.viewmodel.systemMangement.SystemManagementViewModel
import com.poseidonapp.workorder.activities.BaseFragment

class SystemManagmentFragment : BaseFragment(), HandleClickListener {
    var binding: FragmentSystemManagmentBinding? = null
    var systemLists: List<SystemLocation>? = null
    lateinit var sharedPref: SharedPref
    var adapter: SystemManagmentAdapter? = null
    lateinit var systemManagementViewModel: SystemManagementViewModel
    var buildingId="176"
    var id=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentSystemManagmentBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }
    private fun initUI() {
        binding!!.handleClick = this
        sharedPref = SharedPref(requireContext())
        //Bundle
        val bundle: Bundle? = arguments // or intent.extras in Activity
        buildingId = bundle?.getString("buildingId").toString()
        id=bundle?.getString("id").toString()
        Log.e("addressId", "initUI: $id", )
        Log.e("SystemManagement", "initUI:$buildingId ", )
        systemManagementViewModel = ViewModelProvider(this)[SystemManagementViewModel::class.java]
        systemManagementViewModel.systemRequest(sharedPref.getString(Const.TOKEN), buildingId)
        Log.e("vieModel","systemManagement"+(sharedPref.getString(Const.TOKEN)+ buildingId))
        observer()
    }


    fun setAdapter(systemLists: ArrayList<SystemLocation>) {
        var filteredList = ArrayList(systemLists)
        Log.d("AddressesFragment", "Filtered list: $filteredList")

        if (filteredList.isEmpty()) {
            binding?.noDataFoundTV?.visibility = View.VISIBLE
            binding?.rvManagment?.visibility = View.GONE
        } else {
            binding?.noDataFoundTV?.visibility = View.GONE
            binding?.rvManagment?.visibility = View.VISIBLE
            adapter = SystemManagmentAdapter(filteredList) { position, data ->

            }
            binding!!.rvManagment.layoutManager = LinearLayoutManager(context)
            binding!!.rvManagment.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }
    fun addSystemObserver(){
        //Add System Management
        systemManagementViewModel.addSystemSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                systemManagementViewModel.systemRequest(sharedPref.getString(Const.TOKEN), buildingId)
                observer()
            }
        })
        systemManagementViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }
        systemManagementViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
    }

    fun observer() {
        systemManagementViewModel.systemSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                systemLists = it.data.systemLocation
                setAdapter(systemLists as ArrayList)
                Log.e("building","buildinglist"+systemLists)
            }else{

            }
        })

        systemManagementViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }

        systemManagementViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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
        val  filterList= systemLists!!.filter {
            it.sectionName.contains(query!!, ignoreCase = true)
        }
        setAdapter(ArrayList(filterList))
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBacks -> {
                findNavController().popBackStack()
            }
            R.id.addAddress->{
                showAddSystemialog()
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddSystemialog() {
        val systemdialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.bottom_system_allocation, null)
        systemdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        systemdialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        systemdialog.setContentView(dialogView)
        val tvAdd = dialogView.findViewById<TextView>(R.id.tvAdd)
        val etSystemId = dialogView.findViewById<TextView>(R.id.etSystemId)
        val etSystemName = dialogView.findViewById<EditText>(R.id.etSystemName)
        val etSystemLocation = dialogView.findViewById<EditText>(R.id.etSystemLocation)
        val ivaddCircle = dialogView.findViewById<ImageView>(R.id.ivaddCircle)
        ivaddCircle.setOnClickListener{
            systemdialog.dismiss()
        }

        etSystemId.text=id.toString()
        tvAdd.setOnClickListener{
            if (etSystemId.text.toString().equals("")||etSystemName.text.toString().equals("")||etSystemLocation.text.toString().equals("")){
                Toast.makeText(context,"Please fill both fields", Toast.LENGTH_LONG).show()
            } else {
                val addressId = id
                systemManagementViewModel.addSystemRequest(
                    addressId = addressId,
                    systemDescription = etSystemLocation.text.toString(),
                    systemId = etSystemId.text.toString(),
                    assignedQuestion=etSystemName.text.toString()
                )

                systemdialog.dismiss()
                addSystemObserver()
            }


        }
        systemdialog.show()
    }

}