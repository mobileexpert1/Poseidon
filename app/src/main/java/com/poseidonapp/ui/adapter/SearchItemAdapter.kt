package com.poseidonapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.poseidonapp.R
import com.poseidonapp.model.clockInlistscreen.ProjectsItem
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.fragments.ClockInOutFragment
import javax.xml.transform.ErrorListener

class SearchItemAdapter(
//    var baseActivity: BaseActivity,
                        var list: List<String>,
                        var listener: OnItemClickListener
                        ) :
    RecyclerView.Adapter<SearchItemAdapter.ViewHolder>()
{

    val dataList = list.distinct()
    private var activity: FragmentActivity? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_textview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvName.text=dataList.get(position)

        holder.itemView.setOnClickListener {
            try {
                listener.onClickListListener(position)
//                clockInOutFragment.searchClickItem(dataList.get(position).fullName,dataList.get(position).type)
            }catch (e:Exception){

            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvList)

    }

    interface OnItemClickListener {
        fun onClickListListener(index: Int)
    }
}