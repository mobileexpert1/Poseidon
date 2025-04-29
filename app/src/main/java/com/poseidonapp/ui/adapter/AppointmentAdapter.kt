package com.poseidonapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poseidonapp.R
import com.poseidonapp.model.dashboard.Request
import com.poseidonapp.ui.Fragment.DashboardFragment
import com.poseidonapp.ui.adapter.dashboard.grid.DashboardGridItemAdapter

class AppointmentAdapter(
    private val appointmentList: List<Request>,
    var context: Context,
    var fragment: DashboardFragment
) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    // Bind data to the views (invoked by the layout manager)
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val data = appointmentList[position]
        holder.apply {
            tvDates.text = data.assignedDate
            val appointmentAdapter = DashboardGridItemAdapter(data.data) { data ->
                data.apply {
                    fragment.onRoute=true
                    fragment.onRouteCheck(
                        inspectionType = inspectionType,
//                        userName = userName,
                        userName = fullName,
                        mobileNumber = mobileNumber,
                        location = buildingNo + addressLine1 + ", " + city + ", " + state,
                        inspectorInspectionStatus = inspectorInspectionStatus,
                        image = profileImage,
                        id = id
                    )
//                    fragment.requestDetailPopup(
//                        inspectionType = inspectionType,
//                        userName = userName,
//                        mobileNumber = mobileNumber,
//                        location = buildingNo + addressLine1 + ", " + city + ", " + state,
//                        inspectorInspectionStatus = inspectorInspectionStatus,
//                        image = profileImage,
//                        id = id
//                    )
                }
            }
            rvAppointment.layoutManager = GridLayoutManager(context, 2)
            rvAppointment.adapter = appointmentAdapter
        }

    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return appointmentList.size
    }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
        val rvAppointment: RecyclerView = itemView.findViewById(R.id.rvAppointment)
//        val tvAppointmentName: TextView = itemView.findViewById(R.id.tvAppointmentName)
//        val tvAppointmentTime: TextView = itemView.findViewById(R.id.tvAppointmentTime)
//        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
//        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}
