package com.poseidonapp.ui.adapter

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.poseidonapp.R
import com.poseidonapp.model.chatmessage.MessagesItem
import com.poseidonapp.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

class ChatMessageAdapter(
    val context: Context,
    var messagesList: ArrayList<MessagesItem>,
    var UserId: String,
) : RecyclerView.Adapter<MessageViewHolder>() {

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList[position].userId.equals(UserId)) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_sender_chat, parent, false))
        } else {
            OtherMessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_receiver_chat, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messagesList[position]
        holder.bind(message)
    }

    inner class MyMessageViewHolder(view: View) : MessageViewHolder(view) {


        private var txtMessage: TextView = view.findViewById(R.id.txtMessage)
        private var txtTime: TextView = view.findViewById(R.id.txtTime)


        override fun bind(message: MessagesItem) {
            txtMessage.text = message.text

            val yourTimestamp = message.addedOnTimeStamp
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = yourTimestamp * 1000L
            val date: String = DateFormat.format(" hh:mm a", cal).toString()
            txtTime.setText(date)
        }

    }

    inner class OtherMessageViewHolder(view: View) : MessageViewHolder(view) {

        private var messageText: TextView = view.findViewById(R.id.receiverTV)
        private var tvTime: TextView = view.findViewById(R.id.tvTime)


        override fun bind(message: MessagesItem) {
            messageText.text = message.text
            val yourTimestamp = message.addedOnTimeStamp
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = yourTimestamp * 1000L
            val date: String = DateFormat.format(" hh:mm a", cal).toString()
            tvTime.setText(date)
        }

    }
}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: MessagesItem) {}
}