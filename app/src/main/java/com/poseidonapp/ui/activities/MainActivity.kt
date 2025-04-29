package com.poseidonapp.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.poseidonapp.R
import com.poseidonapp.databinding.ActivityMainBinding
import com.poseidonapp.inspector.completedrequest.CompletedRequestPdfFragment
import com.poseidonapp.inspector.completedrequest.CompletedRequestsFragment
import com.poseidonapp.inspector.detail.DetailFillFragment
import com.poseidonapp.inspector.form.InspectionFormFragment
import com.poseidonapp.inspector.formquestions.FormQuestionsFragment
import com.poseidonapp.inspector.home.InspectorHomeFragment
import com.poseidonapp.inspector.notification.InspectorNotificationFragment
import com.poseidonapp.inspector.report.ReportFragment
import com.poseidonapp.inspector.signature.SignatureFragment
import com.poseidonapp.ui.Fragment.DashboardFragment
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.ui.fragments.*
import com.poseidonapp.workorder.activities.PDFFragment
import com.poseidonapp.workorder.activities.WorkInputFragment

class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    private var exit: Boolean = false
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frame_container) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        binding.bottomNavigationView.setItemIconTintList(null)
        navController.addOnDestinationChangedListener(this)

        // Hide the status bar
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue_text)// Replace with your custom color


        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                    true
                }

                R.id.workOrderFragment -> {
                    navController.navigate(R.id.workOrderFragment)
                    true
                }
                R.id.clockFragment -> {
                    navController.navigate(R.id.clockFragment)
                    true
                }
                R.id.orderrFragment -> {
                    navController.navigate(R.id.orderFragment)
                    true
                }
                R.id.personFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }




    }


//
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        val fragment = supportFragmentManager.findFragmentById(R.id.frame_container)
//        when {
//            fragment is DashboardFragment-> {
//                backAction()
//            }
//            fragment is ClockInOutFragment || fragment is RequestDetailFragment || fragment is SubjectToDiscussFragment
//                    || fragment is InspectorHomeFragment || fragment is TimeSheetFragment
//                    || fragment is InstallationFragment ||fragment is ReportFragment ->{
//                replaceFragment(DashboardFragment(), R.id.frame_container)
//            }
//            fragment is DetailFillFragment || fragment is InspectorNotificationFragment || fragment is InspectionFormFragment || fragment is CompletedRequestsFragment ->{
//                replaceFragment(InspectorHomeFragment(), R.id.frame_container)
//            }
//            fragment is FormQuestionsFragment || fragment is SignatureFragment->{
//                replaceFragment(InspectionFormFragment(), R.id.frame_container)
//            }
//            fragment is ChatMessageFragment->{
//                replaceFragment(SubjectToDiscussFragment(),R.id.frame_container)
//            }
//            fragment is EmergencyRequestFragment|| fragment is SafetyMeetingsFragment || fragment is ScheduledEventsFragment
//                    ||fragment is AddProjectFragment->{
//                replaceFragment(WorkOrderFragment(), R.id.frame_container)
//            }
//
////            fragment is WorkInputFragment->{
////                replaceFragment(EmergencyRequestFragment(),R.id.frame_container)
////            }
////            fragment is PDFFragment ->{
////                replaceFragment(WorkInputFragment(),R.id.frame_container)
////            }
//            fragment is Twenty47Fragment->{
////                replaceFragment(SafetyMeetingsFragment(),R.id.frame_container)
//            }
////            fragment is InstallationItemUrlFragment || fragment is DetailsForInstallationFragment || fragment is InstallationPdfFragment->{
////                replaceFragment(InstallationFragment(),R.id.frame_container)
////            }
//            fragment is WebViewOfScheduleFragment->{
//                replaceFragment(ScheduledEventsFragment(),R.id.frame_container)
//            }
//            fragment is CompletedRequestPdfFragment ->{
//                replaceFragment(CompletedRequestsFragment(),R.id.frame_container)
//            }
//            supportFragmentManager.backStackEntryCount > 0 -> {
//                supportFragmentManager.popBackStack()
//            }
//            else -> {
////                finish()
//                //gotoHomeFragment()
//                super.onBackPressed()
//            }
//        }
//    }

    fun backAction() {
        if (exit) {
            finishAffinity()
        } else {
            showToastOne(getString(R.string.press_one_more_time))
            exit = true
            Handler(Looper.getMainLooper()).postDelayed({ exit = false }, (2 * 1000).toLong())
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.label.toString() == "DashboardFragment") {
            binding.bottomNavigationView.visibility=View.VISIBLE
        } else if (destination.label.toString() == "WorkOrderFragment") {
             binding.bottomNavigationView.visibility=View.VISIBLE
        } else if (destination.label.toString() == "ClockFragment") {
             binding.bottomNavigationView.visibility=View.VISIBLE
        } else if (destination.label.toString() == "OrderFragment") {
             binding.bottomNavigationView.visibility=View.VISIBLE
        } else if (destination.label.toString() == "ProfileFragment") {
             binding.bottomNavigationView.visibility=View.VISIBLE
        }  else if (destination.label.toString() == "ReportFragment") {
            binding.bottomNavigationView.visibility=View.VISIBLE
        }else{
            binding.bottomNavigationView.visibility=View.GONE
        }
//        else if (destination.label.toString() == "EmergencyRequestFragment") {
//            binding.bottomNavigationView.visibility=View.GONE
//        }else if (destination.label.toString() == "SafetyMeetingsFragment") {
//            binding.bottomNavigationView.visibility=View.GONE
//        }else if (destination.label.toString() == "ScheduledEventsFragment") {
//            binding.bottomNavigationView.visibility=View.GONE
//        }else if (destination.label.toString() == "AddProjectFragment") {
//            binding.bottomNavigationView.visibility=View.GONE
//        }

    }


}