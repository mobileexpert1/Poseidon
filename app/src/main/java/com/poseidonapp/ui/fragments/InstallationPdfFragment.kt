package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.poseidonapp.R
import com.poseidonapp.databinding.FragmentInstallationPdfBinding
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.utils.CircularProgressDialog
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.installationPdf.InstallationPdfViewModel


class InstallationPdfFragment(progressBarPB: CircularProgressDialog) : BaseFragment(),HandleClickListener {

    var binding: FragmentInstallationPdfBinding? = null
    var installationPdfViewModel: InstallationPdfViewModel?=null
    private var Id: String? =null
    private var Link: String? =null
    var progressBar: CircularProgressDialog

    init {
        this.progressBar =progressBarPB
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Id = it.getString("id")
            Log.e("tag","idd..."+Id)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentInstallationPdfBinding.inflate(inflater, container, false)
        initUI()
        obersers()
        return binding!!.root
    }


    private fun initUI() {
        binding!!.handleClick = this
        installationPdfViewModel = ViewModelProvider(this)[InstallationPdfViewModel::class.java]
        installationPdfViewModel!!.installationPdfRequest(Id.toString())

        binding!!.webViewPDF.settings.javaScriptEnabled = true
        binding!!.webViewPDF.webViewClient = WebViewClient()
        binding!!.webViewPDF.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                progressBar.dismiss()
            }
        })
        Log.e("tag","Link...."+Link)

    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun obersers() {

        installationPdfViewModel!!.installationPdfSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == true) {
                //showToast(it.message)
                Link=it.dataInstallationPDF.link

                val pdf = Link
                binding!!.webViewPDF.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")

            }
        })

        installationPdfViewModel!!.apiError.observe(viewLifecycleOwner) {
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        installationPdfViewModel!!.isLoading.observe(viewLifecycleOwner) {
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
        dialog.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        tvOk.setOnClickListener {
            dialog.dismiss()
            sharedPref.clearPref()
            baseActivity!!.gotoLoginSignUpActivity()
        }
        dialog.show()
    }
    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                baseActivity!!.onBackPressed()

//                baseActivity!!.replaceFragment(InstallationFragment(),R.id.frame_container)

            }
        }
    }


}