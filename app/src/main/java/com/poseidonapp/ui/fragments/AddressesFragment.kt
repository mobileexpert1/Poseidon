package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
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
import com.poseidonapp.databinding.FragmentAddOrganizationBinding
import com.poseidonapp.databinding.FragmentAddressesBinding
import com.poseidonapp.model.Appointment.data
import com.poseidonapp.model.address.Addres
import com.poseidonapp.model.address.Data
import com.poseidonapp.model.organization.Organization
import com.poseidonapp.ui.adapter.AddressesAdapter
import com.poseidonapp.ui.adapter.OrganizationAdapter
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.address.AddressViewModel
import com.poseidonapp.viewmodel.organization.OrganizationViewModel
import com.poseidonapp.workorder.activities.BaseFragment

class AddressesFragment : BaseFragment(), HandleClickListener {
    var binding: FragmentAddressesBinding? = null
    lateinit var addressViewModel: AddressViewModel
    var addressLists: List<Addres>? = null
    lateinit var sharedPref: SharedPref
    var adapter: AddressesAdapter? = null
    var name = ""
    var organizationId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentAddressesBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        addressLists = ArrayList()
        sharedPref = SharedPref(requireContext())
        val bundle: Bundle? = arguments // or intent.extras in Activity
        name = bundle?.getString("name").toString()
        organizationId=bundle?.getString("organizationId").toString()
        Log.e("AddressesFragment", "id: $organizationId")
        addressViewModel = ViewModelProvider(this)[AddressViewModel::class.java]
        addressViewModel.addressRequest(sharedPref.getString(Const.TOKEN),organizationId)
        observer()
        searchView()
    }
    fun setAdapter(addressLists: ArrayList<Addres>) {
        var filteredList = ArrayList(addressLists)
        Log.d("AddressesFragment", "Filtered list: $filteredList")

        if (filteredList.isEmpty()) {
            binding?.noDataFoundTV?.visibility = View.VISIBLE
            binding?.rvAddresses?.visibility = View.GONE
        } else {
            binding?.noDataFoundTV?.visibility = View.GONE
            binding?.rvAddresses?.visibility = View.VISIBLE
            adapter = AddressesAdapter(filteredList) { position, id ->
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("organizationId",id)
                findNavController().navigate(R.id.buildingFragment, bundle)
            }
            binding!!.rvAddresses.layoutManager = LinearLayoutManager(context)
            binding!!.rvAddresses.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }


    //    fun setAdapter() {
//        adapter = AddressesAdapter()
//        binding!!.rvAddresses.layoutManager = LinearLayoutManager(context)
//        binding!!.rvAddresses.adapter = adapter
//        adapter!!.notifyDataSetChanged()
//    }
    private fun addAddressObserver(){
        //Add Address
        addressViewModel.addAddressSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                addressViewModel.addressRequest(sharedPref.getString(Const.TOKEN),organizationId)
                observer()

            }
        })

        addressViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }


        addressViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
    }

    private fun observer() {
        //Address
        addressViewModel.addressSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                addressLists = it.data.address
                setAdapter(addressLists as ArrayList)
            } else {
                Log.d("AddressesFragment", "No data found")
            }
        })


        addressViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }


        addressViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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


    companion object {
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBacks->{
                findNavController().popBackStack()
            }
            R.id.addAddress -> {
                showAddAddressDialog()
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
        val  filterList= addressLists!!.filter {
            it.addressLine.contains(query!!, ignoreCase = true)
        }
        setAdapter(ArrayList(filterList))
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddAddressDialog() {
        val addressdialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_addaddress, null)
        addressdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addressdialog.window?.apply { setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        addressdialog.setContentView(dialogView)
        val tvSubmits = dialogView.findViewById<TextView>(R.id.tvSubmits)
        val ivaddImage = dialogView.findViewById<ImageView>(R.id.ivaddImage)
        val etCity = dialogView.findViewById<EditText>(R.id.etCity)
        val etState = dialogView.findViewById<EditText>(R.id.etState)
        val etPostalCode = dialogView.findViewById<EditText>(R.id.etPostalCode)
        val etAddressLine = dialogView.findViewById<EditText>(R.id.etAddressLine)
        ivaddImage.setOnClickListener{
            addressdialog.dismiss()
        }
        tvSubmits.setOnClickListener{
            if (etCity.text.toString().equals("")||etState.text.toString().equals("")||etPostalCode.text.toString().equals("")){
                Toast.makeText(context,"Please fill both fields", Toast.LENGTH_LONG).show()
            } else {
                val organizationId = organizationId
                addressViewModel!!.addAddressRequest(
                    addressLine = etAddressLine.text!!.toString(),
                    city = etCity.text.toString(),
                    state = etState.text!!.toString(),
                    postalCode = etPostalCode.text!!.toString(),
                    organizationsId = organizationId
                )
                Log.e("ID", "organizationid: $organizationId" )
                addressdialog.dismiss()
                addAddressObserver()
            }
        }
        addressdialog.show()
    }
}











