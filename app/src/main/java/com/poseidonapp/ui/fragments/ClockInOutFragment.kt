package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentClockInOutBinding
import com.poseidonapp.model.clockInlistscreen.ProjectsItem
import com.poseidonapp.ui.adapter.SearchItemAdapter
import com.poseidonapp.ui.adapter.ShiftTimerAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.visibleView
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.clockinlistscreen.ClockInListScreenViewModel
import com.poseidonapp.viewmodel.switchclockin.SwitchClockInViewModel
import com.skydoves.powerspinner.PowerSpinnerView
import java.util.ArrayList
import java.util.concurrent.TimeUnit


class ClockInOutFragment : BaseFragment(), HandleClickListener, ShiftTimerAdapter.ClickListeners,SearchItemAdapter.OnItemClickListener {

    var binding: FragmentClockInOutBinding? = null
    lateinit var clockInListScreenViewModel: ClockInListScreenViewModel
    lateinit var switchClockInViewModel: SwitchClockInViewModel

    private var shiftTimerAdapter: ShiftTimerAdapter? = null
    var latitude: String = ""
    var longitude: String = ""
    var projectItemList: List<ProjectsItem>? = null

    var currentOnId: Int = 0
    var projectItemListPosition: Int = 0
    val selectTypeArray = arrayOf("Yes", "No")
    val selectedArray = arrayOf("Suggestions")
    var selectType: String = ""
    var isChecked: Boolean = false
    private lateinit var searchItemAdapter: SearchItemAdapter
    var isAdapterClicked: Boolean = false
    var projectClockinID:Int=0
    var punchData:Long = 0
    var progress=0
    val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            latitude = it.getString("latitude").toString()
//            longitude = it.getString("longitude").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentClockInOutBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    fun timerRun(){
        val monitor: Runnable = object : Runnable{
            override fun run() {
                //any action
                punchData++ // Increment timestamp (you should replace this with your actual timestamp logic)
                val formattedTime = convertSecondsToHHMMSS(punchData)
                binding!!.tvPunchTime.text = formattedTime
                progress=progress+1
                binding!!.progressBarCircle.progress = progress.toInt()
            }
            //runnable
        }

        mHandler.postDelayed(monitor, 1000)
    }

    private fun initUI() {

        binding!!.handleClick = this
        latitude=sharedPref.getString(Const.latitude)
        longitude=sharedPref.getString(Const.longitude)
        if (sharedPref.getLanguageSelected() == "DARK") {
            val color = R.drawable.darkblue_rect
            binding!!.ivBack.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        }

        clockInListScreenViewModel = ViewModelProvider(this)[ClockInListScreenViewModel::class.java]
        switchClockInViewModel = ViewModelProvider(this)[SwitchClockInViewModel::class.java]

        clockInListScreenViewModel.clockInListRequest(sharedPref.getString(Const.TOKEN), latitude, longitude, "0")

        binding!!.rvSearchItems.layoutManager = LinearLayoutManager(activity)
        searchItemAdapter = SearchItemAdapter(list =selectedArray.toList(),this@ClockInOutFragment)
        binding!!.rvSearchItems.adapter = searchItemAdapter



        binding!!.edtSearch.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding!!.rvSearchItems.visibility = View.VISIBLE

                return false
            }
        })


//        binding!!.rvSearchItems.visibility = View.VISIBLE
        binding!!.edtSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                binding!!.rvSearchItems.visibility = View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                filter(s.toString())
                binding!!.rvSearchItems.visibility = View.GONE

                /*if (count<=0){
                    binding!!.rvSearchItems.visibility = View.GONE
                } else if(s.length<=0){
                    binding!!.rvSearchItems.visibility = View.GONE
                } else if (binding!!.edtSearch.text.isNotEmpty()){
                    binding!!.rvSearchItems.visibility = View.GONE
                }
                else{
                    binding!!.rvSearchItems.visibility = View.VISIBLE
                }*/
            }
        })
        observers()

    }



    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<ProjectsItem> = ArrayList()

        // running a for loop to compare elements.
        for (item in projectItemList!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.fullName.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.rvStartShift.visibleView(false)
//            showToast("No Data Found..")
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            binding!!.noDataFoundTV.visibleView(false)
            binding!!.rvStartShift.visibleView(true)
            shiftTimerAdapter!!.filterList(filteredlist)
        }

    }

    fun setAdapter() {

        if (projectItemList!!.size > 0) {
            binding!!.rvStartShift.visibleView(true)
            binding!!.noDataFoundTV.visibleView(false)
            shiftTimerAdapter =
                ShiftTimerAdapter(baseActivity!!, projectItemList!!, currentOnId, this,sharedPref,
                    baseActivity!!, ClockInOutFragment()
                )
            val linearLayoutManager = LinearLayoutManager(context)
            binding!!.rvStartShift.adapter = shiftTimerAdapter
            binding!!.rvStartShift.layoutManager = linearLayoutManager
//            shiftTimerAdapter!!.setOnItemClickListener(this)
        } else {
            binding!!.noDataFoundTV.visibleView(true)
            binding!!.rvStartShift.visibleView(false)
        }

    }

    companion object {

    }


    public fun injuredPopup(position: Int,projectId:String) {

        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.popup_injured)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.window!!.setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)

        val psSelectType = dialog.findViewById(R.id.psSelectType) as PowerSpinnerView
        val edtInjury = dialog.findViewById(R.id.edtInjury) as EditText
        val llInjuredLayout = dialog.findViewById(R.id.llInjuredLayout) as LinearLayout
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
        val rlInjuredbackground = dialog.findViewById(R.id.rlInjuredbackground) as RelativeLayout

        rlInjuredbackground.setOnClickListener {
            shiftTimerAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            shiftTimerAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }


        psSelectType.setItems(selectTypeArray.toList())
        psSelectType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            selectType = newText
            if (selectType=="Yes"){
                llInjuredLayout.visibility=View.VISIBLE

            }else{
                llInjuredLayout.visibility=View.GONE
            }
        }

        tvSubmit.setOnClickListener {
            if(selectType.equals("")){
                showToast("Please select type")
            }else if (selectType.equals("Yes")){
                if (edtInjury.text.isNullOrEmpty() || edtInjury.text.isBlank()) {
                    showToast("Please enter injury explanation")
                }else{
                    switchClockInViewModel.switchClockInRequest(
                        sessionToken = sharedPref.getString(Const.TOKEN),
                        lat = latitude,
                        long = longitude,
                        project = projectId
                    )
                    shiftTimerAdapter!!.notifyDataSetChanged()
                    dialog.dismiss()
                }

            }else if (selectType.equals("No")) {
                switchClockInViewModel.switchClockInRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    lat = latitude,
                    long = longitude,
                    project = projectId
                )
                shiftTimerAdapter!!.notifyDataSetChanged()
                dialog.dismiss()

            }

        }
        dialog.show()

    }

    public fun instructionsPopup(position: Int,projectId:String) {

        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.popup_instructions)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.window!!.setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
        val cbExpnseRequire = dialog.findViewById(R.id.cbExpnseRequire) as CheckBox
        val rlBackground = dialog.findViewById(R.id.rlBackground) as RelativeLayout

        rlBackground.setOnClickListener {
            shiftTimerAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            shiftTimerAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }

        tvSubmit.setOnClickListener {
            if (cbExpnseRequire.isChecked || cbExpnseRequire.isSelected) {
                shiftTimerAdapter!!.notifyDataSetChanged()
                switchClockInViewModel.switchClockInRequest(
                    sessionToken = sharedPref.getString(Const.TOKEN),
                    lat = latitude,
                    long = longitude,
                    project = projectId
                )
                dialog.dismiss()
            } else {
                showToast("Please accept the instructions")
            }
        }

        dialog.show()

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

    private fun convertSecondsToHHMMSS(timestamp: Long): String {
        val hours = TimeUnit.SECONDS.toHours(timestamp)
        val minutes = TimeUnit.SECONDS.toMinutes(timestamp) % 60
        val seconds = timestamp % 60
        return String.format("%02d:%02d", hours, minutes, seconds)
    }

    private fun observers() {

        clockInListScreenViewModel.clockInListScreenSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status==true){
                projectItemList = it.clockinData.projects
                currentOnId = it.clockinData.projectColockIn.projectId

                //timer data
                punchData = it.clockinData.projectColockIn.timeDifferece.toLong()
                val formattedTime = convertSecondsToHHMMSS(punchData)
                binding!!.tvPunchTime.text = formattedTime
                if (punchData.toInt()<=0 ){
                }else{
                    timerRun()
                }
                progress=punchData.toInt()
                binding!!.progressBarCircle.max=  36000

                setAdapter()
            }

        })

        clockInListScreenViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        clockInListScreenViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


        switchClockInViewModel.switchClockInSuccess.observe(viewLifecycleOwner, Observer {

            if (it.status==true){
                shiftTimerAdapter!!.notifyDataSetChanged()
                clockInListScreenViewModel.clockInListRequest(sharedPref.getString(Const.TOKEN), latitude, longitude, "0")
                showToast(it.message)
            }

        })

        switchClockInViewModel.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        switchClockInViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                baseActivity!!.onBackPressed()
            }
        }
    }

    override fun onclick(position: Int, isChecked: Boolean,switch: SwitchCompat) {

        if (projectItemList != null && projectItemList!!.isNotEmpty()) {
            if (isChecked==true){
                instructionsPopup(position,projectId = projectItemList!!.get(position).id.toString())
            }else{
                injuredPopup(position,projectId = projectItemList!!.get(position).id.toString())
            }
        }
        else {
            // Handle the case where projectItemList is null or empty
            showToast("Project list is empty or not loaded yet.")
        }

    }

    override fun onClickListListener(index: Int) {
        binding!!.rvSearchItems.visibility = View.GONE
        clockInListScreenViewModel.clockInListRequest(
            sharedPref.getString(Const.TOKEN),
            latitude,
            longitude,
            "0"
        )
    }

}