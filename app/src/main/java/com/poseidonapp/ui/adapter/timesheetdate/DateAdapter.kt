package com.poseidonapp.ui.adapter.timesheetdate

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.poseidonapp.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DateAdapter(
    private val dates: List<LocalDate>,  // List of dates to display
    private val currentDate: LocalDate,  // Current date (to highlight the current day)
    private val onDateSelected: (String) -> Unit // Callback to return the full weekday name
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedDate: LocalDate = currentDate  // Set current date as selected by default

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayText)
        val dateText: TextView = view.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]

        // Set the short weekday name (e.g., Mon, Tue, Wed)
        holder.dayText.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        // Set the date number (e.g., 1, 2, 3, etc.)
        holder.dateText.text = date.dayOfMonth.toString()

        // Set background for selected date
        holder.dateText.isSelected = date == selectedDate  // Highlight the selected date

        // Change the text color when the date is selected (white)
        if (date == selectedDate) {
            holder.dateText.setTextColor(Color.WHITE)  // White text for selected date
        } else {
            holder.dateText.setTextColor(Color.BLACK)  // Black text for unselected dates
        }

        // Handle click to select the date and pass the full weekday name (e.g., "Wednesday")
        holder.itemView.setOnClickListener {
            val previousDate = selectedDate
            selectedDate = date
            notifyItemChanged(dates.indexOf(previousDate))  // Unselect previous date
            notifyItemChanged(position)  // Select new date

            // Pass the full weekday name (e.g., "Wednesday") to the fragment/activity
            val weekdayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            onDateSelected(weekdayName)  // Notify fragment/activity with full weekday name
        }
    }

    override fun getItemCount(): Int = dates.size  // Return the total number of dates
}