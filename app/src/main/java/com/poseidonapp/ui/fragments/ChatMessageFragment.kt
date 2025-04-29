package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentChatMessageBinding
import com.poseidonapp.model.chatmessage.MessagesItem
import com.poseidonapp.ui.adapter.ChatMessageAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.addchat.AddChatViewModel
import com.poseidonapp.viewmodel.chatMessage.ChatMessageViewModel
import java.util.*
import kotlin.collections.ArrayList

class ChatMessageFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentChatMessageBinding?=null
    var addChatViewModel: AddChatViewModel?=null
    var chatMessageViewModel:ChatMessageViewModel?=null
//    lateinit var chatMessageAdapter: ChatMessageAdapter
    private var chatMessageAdapter: ChatMessageAdapter? = null

    var message:String?=""
    private var Id: String? =null

    lateinit var messages: ArrayList<MessagesItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Id = it.getString("id")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentChatMessageBinding.inflate(inflater, container, false)
        initUI()
        observers()
        return binding!!.root
    }


    private fun initUI() {
        binding!!.handleClick = this
        messages= ArrayList()
        addChatViewModel = ViewModelProvider(this)[AddChatViewModel::class.java]
        chatMessageViewModel = ViewModelProvider(this)[ChatMessageViewModel::class.java]


        val handler = Handler()
        val refreshRunnable = object : Runnable {
            override fun run() {
                chatMessageViewModel!!.chatMessageRequest(sharedPref.getString(Const.TOKEN),Id.toString())

//                chatMessageAdapter!!.notifyDataSetChanged()
                handler.postDelayed(this, 1000) // 1000 milliseconds = 1 second
            }
        }

        handler.post(refreshRunnable)



    }

    private fun sessionExpiredPopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.popup_token_expired)
        dialog.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
        when (view.id) {
            R.id.ivBackpress -> {
                baseActivity!!.onBackPressed()
            }
            R.id.btnSendImg -> {
                val message = binding!!.etMessages.text.toString().trim()
                if (message.isNotEmpty()) {
                    addChatViewModel!!.addChatRequest(sharedPref.getString(Const.TOKEN), Id.toString(), message)
                    binding!!.etMessages.text.clear()
                } else {
                    Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun observers() {
        //add chat
        addChatViewModel?.addChatSuccess?.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
               // showToast(it.message)
                chatMessageViewModel!!.chatMessageRequest(sharedPref.getString(Const.TOKEN),Id.toString())
            }
        })

        addChatViewModel?.apiError?.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        addChatViewModel?.isLoading?.observe(viewLifecycleOwner) {
            if (it) {
//                baseActivity!!.progressBarPB.show()
            } else {
//                baseActivity!!.progressBarPB.dismiss()
            }
        }



        // chat messages
        chatMessageViewModel?.chatMessageSuccess?.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                //showToast(it.message)

                messages=it.dataChatMessage.messages
                Collections.reverse(messages)
                binding!!.rvChatMessage.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                chatMessageAdapter = ChatMessageAdapter(requireContext(),messages, sharedPref.getString(Const.USER_ID))
                binding!!.rvChatMessage.adapter = chatMessageAdapter

//                chatMessageAdapter!!.notifyDataSetChanged()
                for (i in messages.indices){
                    chatMessageAdapter!!.notifyItemChanged(i)

                }
//                chatMessageAdapter = ChatMessageAdapter(requireContext(),it.dataChatMessage.messages, sharedPref.getString(Const.USER_ID))
//                binding!!.rvChatMessage.adapter = chatMessageAdapter

            }
        })

        chatMessageViewModel?.apiError?.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        chatMessageViewModel?.isLoading?.observe(viewLifecycleOwner) {
            if (it) {
//                baseActivity!!.progressBarPB.show()
            } else {
//                baseActivity!!.progressBarPB.dismiss()
            }
        }

    }
}