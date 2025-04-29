package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentInstallationBinding
import com.poseidonapp.model.installationAssigned.AssignedRequestItem
import com.poseidonapp.ui.adapter.InstallationAdapter
import com.poseidonapp.ui.adapter.SafetyMeetingsAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.installationAssigned.InstalllationAssignedViewModel
import java.util.ArrayList

class InstallationFragment : BaseFragment(),HandleClickListener,InstallationAdapter.ClickListeners {

    var binding:FragmentInstallationBinding? =null
    private var adapter: InstallationAdapter? = null
    lateinit var itemsLists: ArrayList<AssignedRequestItem>
    var installlationAssignedViewModel: InstalllationAssignedViewModel?=null

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
            binding = FragmentInstallationBinding.inflate(inflater, container, false)
        initUI()
        observers()
        return binding!!.root
    }
    private fun initUI() {
        binding!!.handleClick = this
        itemsLists = ArrayList()
        installlationAssignedViewModel = ViewModelProvider(this)[InstalllationAssignedViewModel::class.java]
        installlationAssignedViewModel?.installationAddRequest(sharedPref.getString(Const.TOKEN))


    }
    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.addInstallDetails -> {
                baseActivity!!.replaceFragment(DetailsForInstallationFragment(),R.id.frame_container)
            }

            R.id.ivBack->{
               findNavController().popBackStack()
            }
    }}

    private fun observers() {

        installlationAssignedViewModel!!.installationAssignedSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == true) {
                //showToast(it.message)
                itemsLists.clear()
                itemsLists.addAll(it.dataInstallationAssigned.assignedRequest)
                if(itemsLists !=null){
                    setAdapter()
                    binding!!.noDataFoundTV.visibility=View.GONE

                }else{
                    binding!!.noDataFoundTV.visibility=View.VISIBLE
                }
            }
        })

        installlationAssignedViewModel!!.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        installlationAssignedViewModel!!.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }

    fun setAdapter() {
        if (itemsLists.size > 0) {
            binding!!.installationRecylerView.visibleView(true)
            binding!!.noDataFoundTV.visibleView(false)
            adapter = InstallationAdapter(this,itemsLists,this)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.installationRecylerView.adapter = adapter
            binding!!.installationRecylerView.layoutManager = linearLayoutManager
        } else {
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.installationRecylerView.visibleView(false)
        }



//        adapter = InstallationAdapter(this, itemsLists,this)
//        binding!!.installationRecylerView.layoutManager = LinearLayoutManager(context)
//        binding!!.installationRecylerView.adapter = adapter
//        adapter!!.notifyDataSetChanged()
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

    override fun onclick(
        position: Int,
        id: String)
    {
        val bundle = Bundle()
        bundle.putString("id", id)
        baseActivity!!.replaceFragWithArgs(InstallationItemUrlFragment(progressBarPB!!), R.id.frame_container, bundle)
        progressBarPB!!.show()
    }
}
