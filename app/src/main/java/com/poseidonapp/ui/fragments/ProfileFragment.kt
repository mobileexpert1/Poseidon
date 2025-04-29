package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentProfileBinding
import com.poseidonapp.model.organization.Organization
import com.poseidonapp.model.userProfile.UserData
import com.poseidonapp.ui.activities.LoginActivity
import com.poseidonapp.ui.extensions.loadFromUrl
import com.poseidonapp.ui.extensions.showToastMain
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.HomeViewModel
import com.poseidonapp.viewmodel.userProfile.ProfileViewModel
import com.poseidonapp.workorder.activities.BaseFragment
import java.time.LocalDate

class ProfileFragment : BaseFragment(), HandleClickListener {
    var binding: FragmentProfileBinding?=null
    lateinit var profileViewModel: ProfileViewModel
    var userLists: ArrayList<UserData>? = null
    lateinit var sharedPref: SharedPref
    lateinit var homeViewModel: HomeViewModel
    private val today = LocalDate.now()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentProfileBinding.inflate(inflater, container, false)
        initUI()
        observer()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        sharedPref = SharedPref(requireContext())
        userLists = ArrayList()
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.profileRequest(sharedPref.getString(Const.TOKEN))
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.homeRequest(sharedPref.getString(Const.TOKEN), today.toString())
        ProfileNameobservers()
    }

    private fun ProfileNameobservers() {
        homeViewModel.homeSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                binding!!.profileimage.loadFromUrl(
                    Const.IMG_URL + it.homeData.profilePic,
                    R.drawable.ic_placeholder
                )
                binding!!.tvProfileName.text = it.homeData.userName
                binding!!.userimage.loadFromUrl(
                    Const.IMG_URL+it.homeData.profilePic,
                    R.drawable.ic_placeholder
                )
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observer() {
            profileViewModel.profileSuccess.observe(viewLifecycleOwner, Observer {
                    if (it.status == true) {
                        it.data.userData?.let { userData ->
                            userLists?.addAll(userLists!!)
                        }
                        if (userLists != null) {
                            binding?.etName?.setText(it.data.userData.userName)
                            binding?.etGender?.setText(it.data.userData.gender)
                            binding?.etHourlyPrice?.setText(it.data.userData.hourlyPrice)
                            binding?.etRoll?.setText(it.data.userData.role)
                            binding?.etTotalLeave?.setText(it.data.userData.deleteStatus)
                            binding?.etleavestaken?.setText(it.data.userData.noOfVacation)
//                            val profileImageUrl = it.data.userData.profileImage
//                            if (profileImageUrl != null) {
//                                Glide.with(this)
//                                    .load(profileImageUrl)
//                                    .placeholder(R.drawable.profileimage)
//                                    .into(binding?.userimage!!)
//                            } else {
//                                binding?.userimage?.setImageResource(R.drawable.profileimage)
//                            }
                        } else {
                            Log.e("ProfileFragment", "UserData is null")
                        }
                    }

            })

        profileViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {

            }
        }

        profileViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
    }

    fun resetPasswordObserver(){

        profileViewModel.resetPasswordSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
//                showToastMain(baseActivity,it.message)
                Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
            }
        })

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
        when (view.id){
            R.id.tvChangepass->{
                showChangePasswordDialog()
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


    @SuppressLint("MissingInflatedId")
    private fun showChangePasswordDialog() {
        val changepassworddialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)

        val dialogView = layoutInflater.inflate(R.layout.dailog_change_password, null)
        changepassworddialog.dismiss()
        changepassworddialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        changepassworddialog.setContentView(dialogView)

        val tvCancel = dialogView.findViewById<TextView>(R.id.tvCancel)
        val tvSave = dialogView.findViewById<TextView>(R.id.tvSave)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)




        tvCancel.setOnClickListener {
            changepassworddialog.dismiss()
        }
        tvSave.setOnClickListener {
            val password = etPassword.text.toString()
            val newPassword = etNewPassword.text.toString()

            // Regular expression to allow only alphabets
            val regex = Regex("^[a-zA-Z0-9!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$")
            if (password.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(context, "Please fill both fields", Toast.LENGTH_LONG).show()
            } else if (!regex.matches(password) || !regex.matches(newPassword)) {
                Toast.makeText(
                    context,
                    "Please fill Correct Password",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                profileViewModel.resetPasswordRequest(sharedPref.getString(Const.TOKEN), newPassword)
                changepassworddialog.dismiss()
                resetPasswordObserver()
            }
        }

        changepassworddialog.show()
    }


}