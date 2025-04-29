package com.poseidonapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import com.poseidonapp.R
import com.poseidonapp.databinding.ClockInOutItemBinding
import com.poseidonapp.model.clockInlistscreen.ProjectsItem
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseAdapter
import com.poseidonapp.ui.base.BaseViewHolder
import com.poseidonapp.ui.fragments.ClockInOutFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.SharedPref

class ShiftTimerAdapter(
    var context: Context,
    var projectList: List<ProjectsItem>,
    var  currentOnId: Int,
    private var listener: ClickListeners,
    var sharedPref:SharedPref,
    var baseActivity: BaseActivity,
    var fragment:ClockInOutFragment
)  : BaseAdapter() {

    var isChecked: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<ClockInOutItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.clock_in_out_item,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int = projectList.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ClockInOutItemBinding
        val data = projectList[position]
        binding.titleTV.text = data.fullName
        binding.messagetTV.text = data.address
        binding.distanceTV.text = data.distance
        fragment= ClockInOutFragment()

        binding.punchSwitch.setOnCheckedChangeListener(null)
//        binding.punchSwitch.isChecked = data.isChecked

//        if (data.clockedInStatus == currentOnId){
//           binding.punchSwitch.isChecked=false
//       }
//        else{
//           binding.punchSwitch.isChecked=true
//       }

        if (data.id== currentOnId) {
            // show switch on
            binding.punchSwitch.setChecked(true); //unchecked.
        }
        else {
            // show switch off
            binding.punchSwitch.setChecked(false); //unchecked.
        }

     /*  if (projectList.get(position).isChecked==true){
           binding.punchSwitch.isChecked=true

       }else{
           binding.punchSwitch.isChecked=false

       }*/


       binding.punchSwitch.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                projectList.get(position).isChecked=isChecked
                if (sharedPref.getBoolean(Const.SWITCH)==true){
                    if (data.id== currentOnId) {
                        // show switch on
                        binding.punchSwitch.isChecked=false //unchecked.
//                        fragment.injuredPopup(data.id.toString())
                        listener.onclick(position,false,binding.punchSwitch)
                    }
                    else {
                        // show switch off
                        binding.punchSwitch.isChecked=false //unchecked.
                        if (currentOnId == 0){
//                            instructionsPopup()
//                            fragment.instructionsPopup(data.id.toString())
                            currentOnId = data.id
                            listener.onclick(position,true,binding.punchSwitch)
                        }
                        else{
                        }
                    }
//                    listener.onclick(position,isChecked)
                }
                else{
                    binding.punchSwitch.isChecked=false
                    baseActivity.showToast("Kindly day login first then start your project clock in")
                }
            } else {
                binding.punchSwitch.isChecked=false
                listener.onclick(position,data.isChecked,binding.punchSwitch)
            }
        })




       /* if (sharedPref.getBoolean(Const.SWITCH)==true){
            if (data.id== currentOnId) {
                // show switch on
                binding.punchSwitch.setChecked(true); //unchecked.
            }
            else {
                // show switch off
                binding.punchSwitch.setChecked(false); //unchecked.
            }
        }
*/

    }


    private fun handleSwitchOn(position: Int, data: ProjectsItem) {
        if (sharedPref.getBoolean(Const.SWITCH) == true) {
            if (data.id == currentOnId) {
                // The switch is already on for this item
                fragment.injuredPopup(position, data.id.toString())
            } else {
                // The switch is off; turn it on
                fragment.instructionsPopup(position, data.id.toString())
            }
        } else {
            baseActivity.showToast("Kindly log in first before starting your project clock-in")
            projectList[position].isChecked = false // Update the state to unchecked
        }
    }



    fun switchOnOff(isChecked:Boolean, position: Int){
//         this.isChecked=isChecked
        projectList.get(position).isChecked=isChecked
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }



    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<ProjectsItem>) {
        // below line is to add our filtered
        // list in our course array list.
        projectList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }


    interface ClickListeners {
        fun onclick(position: Int,isChecked:Boolean,switch: SwitchCompat)

    }

}