package com.poseidonapp.ui.adapter.dashboard.grid

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.poseidonapp.R
import com.poseidonapp.model.dashboard.DataX
import java.text.SimpleDateFormat
import java.util.Locale


class DashboardGridItemAdapter(
    val mList: List<DataX>,
    var callback: (DataX) -> Unit
) :
    RecyclerView.Adapter<DashboardGridItemAdapter.ViewHolder>() {

    // Define colors array
    val colors = arrayOf(
        Color.rgb(249, 221, 241),
        Color.rgb(201, 235, 242),
        Color.rgb(242, 243, 189),
        Color.rgb(221, 249, 225),
        Color.rgb(238, 234, 246),
        Color.rgb(249, 249, 221),
        Color.rgb(221, 249, 225),
        Color.rgb(201, 235, 242),
        Color.rgb(242, 243, 189),
        Color.rgb(221, 249, 225),
        Color.rgb(238, 234, 246),
        Color.rgb(249, 221, 241)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard, parent, false)
        return ViewHolder(view)
    }

    fun convertTo12HourFormat(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time24) // Parse the 24-hour time string
        return outputFormat.format(date) // Format it to 12-hour time with AM/PM
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = mList[position]
        holder.apply {
            data.apply {
                tvTitle.text=fullName
                tvDescription.text=buildingNo+addressLine1+", "+city+", "+state
//                tvAppointmentTime.text=convertTo12HourFormat(assignedTime)
                tvAppointmentTime.text=assignedTime
                tvInspectionType.text="("+inspectionType+")"
                // Set background color based on position
                val color = colors[position % colors.size]
                llAppointmentStart.setBackgroundColor(color)
                // Set rounded corners programmatically for CardView
                val drawable = GradientDrawable()
                drawable.cornerRadius = 50f  // Set corner radius (in pixels)
                drawable.setColor(color)     // Set background color of the CardView

                // Apply this drawable to CardView's background
                llAppointmentStart.background = drawable
            }
            itemView.setOnClickListener {
                callback.invoke(data)
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var tvAppointmentTime: TextView = itemView.findViewById(R.id.tvAppointmentTime)
        var tvInspectionType: TextView = itemView.findViewById(R.id.tvInspectionType)
        var llAppointmentStart: CardView = itemView.findViewById(R.id.llAppointmentStart)
    }

}