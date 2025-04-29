package com.poseidonapp.inspector.completedrequest

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentCompletedRequestsBinding
import com.poseidonapp.inspector.adapter.CompletedRequestsAdapter
import com.poseidonapp.model.completedrequest.CompletedRequestItem
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.completedRequests.CompletedRequestsViewModel
import java.util.ArrayList

class CompletedRequestsFragment : BaseFragment(),HandleClickListener,CompletedRequestsAdapter.ClickListeners {

    var binding: FragmentCompletedRequestsBinding? = null
    lateinit var completedRequestsViewModel: CompletedRequestsViewModel
    lateinit var requestLists: ArrayList<CompletedRequestItem>
    private var adapter: CompletedRequestsAdapter? = null

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
            binding = FragmentCompletedRequestsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        requestLists = ArrayList()
        completedRequestsViewModel = ViewModelProvider(this)[CompletedRequestsViewModel::class.java]
        completedRequestsViewModel.completedRequest(sharedPref.getString(Const.TOKEN))
        observer()
    }

    fun setAdapter() {
        if (requestLists.size > 0) {
            binding!!.inspectionRecylerView.visibleView(true)
            binding!!.noDataFoundTV.visibleView(false)
            adapter = CompletedRequestsAdapter(this, requestLists, this)
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.inspectionRecylerView.adapter = adapter
            binding!!.inspectionRecylerView.layoutManager = linearLayoutManager
        } else {
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.inspectionRecylerView.visibleView(false)
        }
    }

    fun observer(){
       completedRequestsViewModel.completedRequestSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
                requestLists.clear()
                requestLists.addAll(response.data.completedRequest)
                if (!requestLists.isNullOrEmpty()) {
                    setAdapter()
                }
            }
        }

        // Observe API error LiveData
        completedRequestsViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe loading state LiveData
        completedRequestsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
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
            when(view.id){
                R.id.ivBack ->{
                    findNavController().popBackStack()
                }
            }
    }

    override fun onclick(position: Int, report: String) {
        val bundle = Bundle()
        bundle.putString("attachment",Const.pdfBaseUrl+report)
        Log.d("pdfurl","attactment"+Const.pdfBaseUrl+report)
        baseActivity!!.replaceFragWithArgs(CompletedRequestPdfFragment(), R.id.frame_container, bundle)

    }
}