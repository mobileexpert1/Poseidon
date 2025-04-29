package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentSubjectToDiscussBinding
import com.poseidonapp.ui.adapter.ChatNotesAdapter
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.chatNotes.ChatNotesViewModel
import com.poseidonapp.viewmodel.feedback.FeedBackViewModel


class SubjectToDiscussFragment : BaseFragment(), HandleClickListener,
    ChatNotesAdapter.ClickListeners {
    var binding: FragmentSubjectToDiscussBinding? = null
    var chatNotesViewModel: ChatNotesViewModel? = null
    var feedBackViewModel: FeedBackViewModel? = null
    private var chatNotesAdapter: ChatNotesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentSubjectToDiscussBinding.inflate(inflater, container, false)
        initUI()
        observers()
        return binding!!.root
    }


    private fun initUI() {
        binding!!.handleClick = this
        chatNotesViewModel = ViewModelProvider(this)[ChatNotesViewModel::class.java]
        feedBackViewModel = ViewModelProvider(this)[FeedBackViewModel::class.java]
        chatNotesViewModel?.chatNoteRequest(sharedPref.getString(Const.TOKEN))
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.addImg -> {
                askToAdminPopup()
            }
            R.id.ivBacks -> {
                findNavController().popBackStack()
            }
            R.id.tvSave->{
                askToAdminPopup()
            }

        }
    }
    private fun observers() {

        //chat Notes
        chatNotesViewModel?.chatNoteSuccess?.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                chatNotesAdapter =
                    ChatNotesAdapter(baseActivity!!, this, it.dataChatNotes.notesList)
                val linearLayoutManager = LinearLayoutManager(context)
                binding!!.rvSubjectToDiscuss.adapter = chatNotesAdapter
                binding!!.rvSubjectToDiscuss.layoutManager = linearLayoutManager
            }
        })

        chatNotesViewModel?.apiError?.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        chatNotesViewModel?.isLoading?.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }


        //feedBack
        feedBackViewModel?.feedbackSuccess?.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                showToast(it.message)
                chatNotesViewModel!!.chatNoteRequest(sharedPref.getString(Const.TOKEN))
            }
        })

        feedBackViewModel?.apiError?.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        feedBackViewModel?.isLoading?.observe(viewLifecycleOwner) {
            if (it) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }
    }


    private fun sessionExpiredPopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
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

    @SuppressLint("MissingInflatedId")
     fun askToAdminPopup() {
        val asktoadmindialog = Dialog(baseActivity!!, R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.popup_ask_to_admin, null)
        asktoadmindialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        asktoadmindialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        asktoadmindialog.setContentView(dialogView)
        val edtSubject = dialogView.findViewById<EditText>(R.id.edtSubject)
        val edtNotes = dialogView.findViewById<EditText>(R.id.edtNotes)
        val tvSubject = dialogView.findViewById<TextView>(R.id.tvSubject)

        tvSubject.setOnClickListener {
            if (edtSubject.text.toString().trim().isEmpty()) {
                Toast.makeText(context, getString(R.string.please_provide_all_parameters), Toast.LENGTH_LONG).show()
            } else if (edtNotes.text.toString().trim().isEmpty()) {
                Toast.makeText(context, getString(R.string.please_provide_all_parameters), Toast.LENGTH_LONG).show()
            } else {
                feedBackViewModel!!.feedBackRequest(
                    sharedPref.getString(Const.TOKEN),
                    edtSubject.text.toString(),
                    edtNotes.text.toString()
                )
                asktoadmindialog.dismiss()
                chatNotesAdapter!!.notifyDataSetChanged()
            }

        }
        asktoadmindialog.show()

    }

    override fun onclick(position: Int, id: String) {

        val bundle = Bundle()
        bundle.putString("id", id)
        baseActivity!!.replaceFragWithArgs(ChatMessageFragment(), R.id.frame_container, bundle)
    }
}