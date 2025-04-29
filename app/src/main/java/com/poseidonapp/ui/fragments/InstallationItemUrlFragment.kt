package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.poseidonapp.R
import com.poseidonapp.databinding.FragmentInstallationItemUrlBinding
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.CircularProgressDialog
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener


class InstallationItemUrlFragment(progressBarPB: CircularProgressDialog) : BaseFragment(),HandleClickListener {

    var binding: FragmentInstallationItemUrlBinding? = null
    var progressBar: CircularProgressDialog?=null
    private var Id: String? =null

    init {
        this.progressBarPB =progressBarPB
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
    ): View{
        if (binding == null)
            binding = FragmentInstallationItemUrlBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUI() {
        binding!!.handleClick = this
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.webViewClient = WebViewClient()
        binding!!.webView.loadUrl(Const.GENERATE_REPORT+Id)
        binding!!.webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e("tag","url......."+url)
                Log.e("tag","view......."+view)

                try {
                    if (url.contains(Const.ADMIN, ignoreCase = true)) {
                        Log.e("url", "Navigating to InstallationPdfFragment" + url)
                        val bundle = Bundle()
                        bundle.putString("id", Id)
                        baseActivity!!.replaceFragWithArgs(
                            InstallationPdfFragment(progressBarPB!!),
                            R.id.frame_container,
                            bundle
                        )
                        progressBarPB!!.dismiss()
                    }
                } catch (e: Exception) {
                    Log.e("tag", "Error in onPageFinished: ${e.message}")
                    progressBar?.dismiss()
                }

            }

        })

//        binding!!.webView.setWebViewClient(object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                Log.e("tag","url......."+url)
//                Log.e("tag","view......."+view)
//
//                if (url.contains(Const.ADMIN)){
//                    val bundle = Bundle()
//                    bundle.putString("id", Id)
//                    baseActivity!!.replaceFragWithArgs(InstallationPdfFragment(progressBarPB!!), R.id.frame_container, bundle)
//                    progressBarPB!!.show()
//
//                }
//
//                progressBar?.dismiss()
//            }
//
//        })
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                baseActivity!!.onBackPressed()

            }
        }
    }
}