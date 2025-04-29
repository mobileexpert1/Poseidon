package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.barteksc.pdfviewer.PDFView
import com.poseidonapp.R
import com.poseidonapp.databinding.FragmentWebViewOfScheduleBinding
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.HandleClickListener
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WebViewOfScheduleFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentWebViewOfScheduleBinding? = null

    var attachment: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            attachment = it.getString("attachment").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentWebViewOfScheduleBinding.inflate(inflater, container, false)

        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUI() {
        binding!!.handleClick = this

        if (attachment.endsWith(".pdf", true)) {
            // PDF attachment
            binding!!.pdfView.visibility = View.VISIBLE
            RetrievePDFFromURL(binding!!.pdfView).execute(attachment)
            showToastLong("File can take time to load, so please wait")
        } else if (attachment.endsWith(".png", true) || attachment.endsWith(
                ".jpg",
                true
            ) || attachment.endsWith(".jpeg", true)
        ) {
            // Image attachment (PNG, JPG, or JPEG)
            binding!!.webview.visibility = View.VISIBLE

            binding!!.webview.settings.setJavaScriptEnabled(true)

            binding!!.webview.settings.setSupportZoom(true)
            binding!!.webview.settings.builtInZoomControls = true
            binding!!.webview.settings.displayZoomControls = false

            binding!!.webview.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    //progressBar.dismiss()
                }
            })

            binding!!.webview.loadUrl(attachment)

        } else {
            // Handle other attachment types or unknown types
            // You can show an error message or take appropriate action.
        }

/*
        if (bundle != null) {
            val attachment = bundle.getString("attachment")

            binding!!.webview.settings.setJavaScriptEnabled(true)
            binding!!.webview.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    //progressBar.dismiss()
                }
            })

            if (attachment != null) {
                binding!!.webview.loadUrl(attachment)
            }


        }
*/


    }


    class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {

         val mypdfView: PDFView = pdfView

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): InputStream? {
            try {
                val url = URL(params[0])
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                if (urlConnection.responseCode == 200) {
                    return BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: InputStream?) {
            if (result != null) {
                mypdfView.fromStream(result).load()
            } else {
                // Handle the case where the result is null or the PDF download failed.
                // You can show an error message to the user or take appropriate action.
                // For example:
                // mypdfView.loadError("Failed to load PDF")
            }
        }
    }


    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }


}