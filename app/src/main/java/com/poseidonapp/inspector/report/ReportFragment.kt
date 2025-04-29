package com.poseidonapp.inspector.report

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.github.barteksc.pdfviewer.PDFView
import com.poseidonapp.R
import com.poseidonapp.databinding.FragmentReportBinding
import com.poseidonapp.ui.Fragment.DashboardFragment
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragment
import com.poseidonapp.utils.HandleClickListener
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ReportFragment : BaseFragment(),HandleClickListener {

    var binding: FragmentReportBinding? = null
    var attachment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            attachment=it.getString("attachment")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentReportBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    companion object {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    fun initUI(){
        binding!!.handleClick = this
        RetrievePDFFromURL(binding!!.pdfView, baseActivity!!).execute(attachment)
    }


    class RetrievePDFFromURL(pdfView: PDFView, baseActivity: BaseActivity) :
        AsyncTask<String, Void, InputStream>() {
        @SuppressLint("StaticFieldLeak")
        var baseActivity=baseActivity
        // on below line we are creating a variable for our pdf view.
        @SuppressLint("StaticFieldLeak")
        var mypdfView: PDFView = pdfView
        override fun onPreExecute() {
            super.onPreExecute()
            baseActivity.progressBarPB!!.show()
//            licenceAnotherFragment.progressBarPB.show()
        }
        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            var inputStream: InputStream? = null
            baseActivity.progressBarPB!!.show()
            try {
                val url = URL(params[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return inputStream
        }
        // on below line we are calling on post execute
        // method to load the url in our pdf view.
        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            if (result != null) {

                baseActivity.progressBarPB!!.dismiss()

                mypdfView.fromStream(result).load()
            } else {
                Log.e("PDF Load Error", "Failed to load PDF from URL")
                // You may want to display an error message to the user here.
            }


//            licenceAnotherFragment.progressBarPB.dismiss()

        }
    }


   /* fun onBackPressed() {
        val fragmentCount = baseActivity!!.supportFragmentManager.backStackEntryCount
        if (fragmentCount > 0) {
            baseActivity!!.supportFragmentManager.popBackStack()
        } else {
            super.baseActivity!!.onBackPressed()
        }
        }
    */


    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.ivBacks->{
//                baseActivity!!.onBackPressed()
                findNavController().navigate(R.id.dashboardFragment)
//                requireActivity().supportFragmentManager.popBackStack()
//                baseActivity!!.supportFragmentManager.beginTransaction()
//                    .replace(R.id.frame_container, DashboardFragment())
//                    .addToBackStack(null)
//                    .commit()
            }
        }

    }

}