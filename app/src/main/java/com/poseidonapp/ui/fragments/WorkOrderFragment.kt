package com.poseidonapp.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentDashboardBinding
import com.poseidonapp.databinding.FragmentWorkOrderBinding
import com.poseidonapp.ui.activities.LoginActivity
import com.poseidonapp.ui.extensions.loadFromUrl
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.HomeViewModel
import com.poseidonapp.workorder.activities.BaseFragment
import java.time.LocalDate

class WorkOrderFragment : BaseFragment(),HandleClickListener {
    var binding: FragmentWorkOrderBinding?=null
    lateinit var homeViewModel: HomeViewModel
    lateinit var sharedPref: SharedPref
    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentWorkOrderBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        sharedPref = SharedPref(requireContext())
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
        observers()
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivWorkLayout->{
                findNavController().navigate(R.id.emergencyRequestFragment)
            }
            R.id.ivSafetyLayout->{
                findNavController().navigate(R.id.safetyMeetingsFragment)
            }
            R.id.ivScheduleLayout->{
                findNavController().navigate(R.id.scheduledEventsFragment)
            }
            R.id.ivProjectLayout->{
                findNavController().navigate(R.id.addProjectFragment)
            }
            R.id.ivOrganizeLayout->{
            findNavController().navigate(R.id.addOrganizationFragment)
            }
            R.id.ivOrganizeLayout->{
                findNavController().navigate(R.id.addOrganizationFragment)
            }
            R.id.ivInstallLayout->{
                findNavController().navigate(R.id.installationFragment)
            }
            R.id.iv_notification->{
                findNavController().navigate(R.id.inspectorNotificationFragment)

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
    private fun observers() {

        homeViewModel.homeSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                binding!!.profileimage.loadFromUrl(
                    Const.IMG_URL + it.homeData.profilePic,
                    R.drawable.ic_placeholder
                )
                binding!!.tvName.text = it.homeData.userName
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