package com.poseidonapp.ui.base

import android.view.ViewGroup

open class BaseAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
//    private var pageEndListener: PageEndListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(null)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemClick(vararg itemData: Any) {
        if (onItemClickListener != null) {
            onItemClickListener!!.onItemClick(*itemData)
        }
    }

   /* fun setOnPageEndListener(pageEndListener: PageEndListener) {
        this.pageEndListener = pageEndListener
    }*/
/*
    internal fun onPageEnd() {
        if (pageEndListener != null) {
           // pageEndListener!!.onPageEnd(this)
        }
    }*/


    interface OnItemClickListener {
        fun onItemClick(vararg itemData: Any)
    }

  /*  interface PageEndListener {
        fun onPageEnd(adapter: BaseAdapterCl)
    }*/
    open fun getItem(position: Int): Any {
return 0   }
}
