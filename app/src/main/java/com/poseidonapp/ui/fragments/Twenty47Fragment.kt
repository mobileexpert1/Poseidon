package com.poseidonapp.ui.fragments

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.barteksc.pdfviewer.PDFView
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentTwenty47Binding
import com.poseidonapp.ui.base.BaseActivity
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.MeetingUpdateViewModel
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class Twenty47Fragment : BaseFragment(), HandleClickListener {

    var binding: FragmentTwenty47Binding? = null
    var id: String = ""
    var attachment: String = ""
    var signature: String = ""
    lateinit var meetingUpdateViewModel: MeetingUpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id= it.getString("id")!!
            attachment=it.getString("attachment")!!
            signature=it.getString("signature")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentTwenty47Binding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    fun initUI(){
        binding!!.handleClick = this
//        if (sharedPref.getLanguageSelected() == "DARK") {
//            val color = Color.WHITE // Replace with the color you want
//            binding!!.ivBacks.setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        }
//        if (sharedPref.getLanguageSelected() == "LIGHT") {
//            val color = R.color.dark_blue
//            binding!!.ivBacks.setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        }
        RetrievePDFFromURL(binding!!.pdfView, baseActivity!!).execute(attachment)
//        if (signature!=null){
//            binding!!.llBottom.visibility=View.VISIBLE
//        }else{
//            binding!!.llBottom.visibility=View.GONE
//
//        }

        meetingUpdateViewModel = ViewModelProvider(this)[MeetingUpdateViewModel::class.java]
        observer()

    }

    fun observer() {
        meetingUpdateViewModel.meetingUpdateSuccess.observe(
            viewLifecycleOwner,
            Observer {
                if (it.status == true) {
//                    baseActivity!!.finish()
                }
            })

        meetingUpdateViewModel.apiError.observe(viewLifecycleOwner) {
//            showToast(it)
            if (it.toString().equals("401")) {
                sessionExpiredPopup()
            } else {
                showToast(it)
            }
        }

        meetingUpdateViewModel.isLoading.observe(viewLifecycleOwner) {
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

    companion object {

    }


    override fun onViewClick(view: View) {
        when(view.id){
                R.id.ivBacks->{
//                binding!!.signaturepad.clear()
//                findNavController().popBackStack()
                findNavController().navigate(R.id.action_twenty47Fragment_to_safetyMeetingsFragment)
            }

//            R.id.llBottom->{
//                binding!!.rlFirst.visibility=View.GONE
//                binding!!.secondRL.visibility=View.VISIBLE
//            }
//            R.id.llBottomSign->{
//                if (binding!!.signaturepad.isEmpty()) {
//                    showToast("Please add signature")
//                }
//                else {
//                    val signatureBitmap: Bitmap = binding!!.signaturepad.getSignatureBitmap()
//                    val img_str = convertBitmapToBase64(signatureBitmap)
////                    val stream = ByteArrayOutputStream()
////                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
////                    val image = stream.toByteArray()
////                    val img_str = Base64.encodeToString(image, 0)
////                    //decode string to image
////                    val imageAsBytes = Base64.decode(
////                        img_str.toByteArray(),
////                        Base64.DEFAULT
////                    )
//                    Log.d("imagebase64",img_str)
//                    meetingUpdateViewModel.meetingUpdateRequest(id = id,
//                        sessionToken = sharedPref.getString(Const.TOKEN),
//                        image = img_str)
////            signatureimageIV.setImageBitmap(
////                BitmapFactory.decodeByteArray(
////                    imageAsBytes,
////                    0,
////                    imageAsBytes.size
////                )
////            )
//                }
//            }

        }
    }

//    private fun convertBitmapToBase64(bitmap: Bitmap): String {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
//        val imageBytes = byteArrayOutputStream.toByteArray()
//        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
//    }

    class RetrievePDFFromURL(pdfView: PDFView, baseActivity: BaseActivity) :
        AsyncTask<String, Void, InputStream>() {
        var baseActivity=baseActivity
        val mypdfView: PDFView = pdfView

        override fun onPreExecute() {
            super.onPreExecute()
            baseActivity.progressBarPB!!.show()
        }
        override fun doInBackground(vararg params: String?): InputStream? {
            baseActivity.progressBarPB!!!!.show()
            var inputStream: InputStream? = null
            try {
                val url = URL(params.get(0))
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)

                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                return null;
            }
            return inputStream;
        }
        override fun onPostExecute(result: InputStream?) {

            baseActivity.progressBarPB!!.dismiss()

            mypdfView.fromStream(result).load()

        }
    }

}