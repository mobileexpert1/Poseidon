package com.poseidonapp.inspector.formquestions

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentFormQuestionsBinding
import com.poseidonapp.inspector.adapter.ArrayAdapterRecyclerViewAdapter
import com.poseidonapp.inspector.adapter.FormDataListAdapter
import com.poseidonapp.inspector.adapter.FormQuestionsAdapter
import com.poseidonapp.inspector.report.ReportFragment
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.ui.extensions.replaceFragWithArgs
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.viewmodel.uploadimage.UploadImageViewModel
import java.io.File
import java.util.*

class FormQuestionsFragment : BaseFragment(),HandleClickListener {

    var binding: FragmentFormQuestionsBinding? = null
    private var adapter: FormQuestionsAdapter? = null
    //    lateinit var questionlistDB: List<QuestionJsonItem>
    var id: String = ""
    var sectionId: String = ""
    var sectionName: String = ""
//    lateinit var image: Bitmap

    var imageBase64: String = ""
    var imagequestion: String = ""
    var imagePosition: Int = 0
    val STORAGE_PERMISSION_REQUEST = 2
    lateinit var uploadImageViewModel: UploadImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id= it.getString("sectionId")!!
            sectionId= it.getString("sectionId")!!
            sectionName= it.getString("sectionName")!!
        }
    }

    companion object {
        var formQuestionsFragment:FormQuestionsFragment?=null

        val REQUEST_IMAGE_CAPTURE = 1
        val PICK_IMAGE = 2

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentFormQuestionsBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }


    private fun enterObjectPopup() {
        val dialog = Dialog(baseActivity!!, R.style.CustomBottomSheetDialogTheme)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.popup_enter_object_name)
        dialog.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        val edtEnterObjectName = dialog.findViewById(R.id.edtEnterObjectName) as EditText
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvSave = dialog.findViewById(R.id.tvSave) as TextView

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvSave.setOnClickListener {
            if (edtEnterObjectName.text.toString().trim().isEmpty()){
                Toast.makeText(context, getString(R.string.please_enter_object_name) , Toast.LENGTH_LONG).show()
            }else{
                dbHelper.createNewSection(edtEnterObjectName.text.toString(),id)
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    private fun initUI() {
        formQuestionsFragment=this
        binding!!.handleClick=this

        val sectionsFromDb = dbHelper.getAllSections()

        sectionsFromDb.forEach { section ->
            if (sectionId==section.id) {
                if (section.isInclude=="1") {
                    binding!!.cbExpnseRequire.isChecked=true
                    binding!!.cbExpnseRequire.isSelected = true
                } else {
                    binding!!.cbExpnseRequire.isChecked=false
                    binding!!.cbExpnseRequire.isSelected = false
                }
            }
        }

        binding!!.cbExpnseRequire.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // CheckBox is ticked, perform some action
                dbHelper. updateSectionIsInclude(sectionId,"1")
            } else {
                // CheckBox is unticked, perform some other action
                dbHelper. updateSectionIsInclude(sectionId,"")
            }
        }

        uploadImageViewModel = ViewModelProvider(this)[UploadImageViewModel::class.java]

        setAdapter()
//        observer()

    }

    fun setAdapter() {
        val getQuestionFromDB = dbHelper.getQuestionsForSection(sectionId)
//        questionlistDB=getQuestionFromDB
        adapter = FormQuestionsAdapter(baseActivity = baseActivity!!, context = baseFragment!!, mList = getQuestionFromDB,
            formQuestionsFragment = formQuestionsFragment!!, dbHelper = dbHelper, id = sectionId, sectionName = sectionName)
        val linearLayoutManager = LinearLayoutManager(context)
        binding!!.rvQuestionList.adapter = adapter
        binding!!.rvQuestionList.layoutManager = linearLayoutManager
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun showDeletePopup(
        position: Int,
        itemAdapter: FormDataListAdapter
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        val cancel = "<font color=\"#33AEE8\">Cancel</font>"
        var cancelhtml = Html.fromHtml(cancel, Html.FROM_HTML_MODE_COMPACT)
        val delete = "<font color=\"#33AEE8\">Delete</font>"
        var deletehtml = Html.fromHtml(delete, Html.FROM_HTML_MODE_COMPACT)
        builder.setPositiveButton(deletehtml) { _, _ ->
            itemAdapter.removeItem(position)
        }

        builder.setNegativeButton(cancelhtml, null)
        builder.create().show()
    }


    fun showDataDialog(
        list: List<String>, question: String,
        itemAdapter: FormDataListAdapter
    ) {
        lateinit var recyclerView: RecyclerView
        lateinit var itemAdapters: ArrayAdapter<String>
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.multi_select_data_items)
        dialog.setTitle("")
        recyclerView = dialog.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dataList = list
        itemAdapters = ArrayAdapter(requireContext(), R.layout.item_layout, dataList)
        recyclerView.adapter = ArrayAdapterRecyclerViewAdapter(itemAdapters) { item ->
            dialog.dismiss()
            itemAdapter.addItem(item)
//            dbHelper.updateQuestionAnswer(id,question,item)
        }
        dialog.show()
    }


    fun selectDate(
        view: TextView
    ) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                if (!selectedDate.before(currentDate)) {
                    // Do something with the selected date
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                    view.setText(formattedDate)
                }
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.setOnShowListener { dialog: DialogInterface ->
            val positiveColor: Int = ContextCompat.getColor(
                requireContext(), R.color.dark_blue
            )
            val negativeColor: Int =
                ContextCompat.getColor(requireContext(), R.color.dark_blue)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
        }
        datePickerDialog.show()

    }

    fun showYearPickerDialog(view: TextView) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.year_picker_dialog)
        dialog.setTitle("Select Year")
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.yearPicker)
        yearPicker.minValue = 1900
        yearPicker.maxValue = 2099
        yearPicker.value = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

        val okButton = dialog.findViewById<TextView>(R.id.okButton)
        okButton.setOnClickListener {
            val selectedYear = yearPicker.value
            view.text = selectedYear.toString()
//            formQuestionsFragment.showToast("Selected year: $selectedYear")
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onViewClick(view: View) {
        when(view.id){
            R.id.ivBack->{
//                baseActivity!!.onBackPressed()
                findNavController().popBackStack()
            }
            R.id.tvAddObject->{
                enterObjectPopup()
            }
        }
    }

    fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }


    fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_REQUEST
        )
    }

    fun showCameraGalleryDialog(position: Int,question: String) {
        imagePosition=position
        imagequestion=question
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.camera_gallery_selector)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val tvCamera = dialog.findViewById<TextView>(R.id.tvCamera)
        val tvGallery = dialog.findViewById<TextView>(R.id.tvGallery)
        val tvCancelImage = dialog.findViewById<TextView>(R.id.tvCancelImage)

        tvCamera.setOnClickListener {
            cameraPermissions()
            dialog.dismiss()
        }

        tvGallery.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        tvCancelImage.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                captureImage()
            }
            else {
                Log.i("Permission: ", "Denied")
            }
        }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                requestStoragePermission()
                // Handle permission denied
            }
        }
    }

    fun cameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
//                showToast("Permissions Granted")
                captureImage()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    fun onRadioButtonClicked(
        selectedRadioButton: RadioButton,
        radioOption1: RadioButton, radioOption2: RadioButton, radioOption3: RadioButton, question: String
    ) {
        // Uncheck all radio buttons except the selected one
        val radioOption1 = radioOption1
        val radioOption2 = radioOption2
        val radioOption3 = radioOption3

        var newAnswer=""

        if (selectedRadioButton != radioOption1) {
            radioOption1.isChecked = false
        }
        if (selectedRadioButton != radioOption2) {
            radioOption2.isChecked = false
        }
        if (selectedRadioButton != radioOption3) {
            radioOption3.isChecked = false
        }

        if (radioOption1.isChecked==true){
            newAnswer="Yes"
        }
        if (radioOption2.isChecked==true){
            newAnswer="No"
        }
        if (radioOption3.isChecked==true){
            newAnswer="N/A"
        }
        // Check the selected radio button
        selectedRadioButton.isChecked = true
        dbHelper.updateQuestionAnswer(id,question,newAnswer)

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


    fun observer(){
        uploadImageViewModel.uploadImageSuccess.observe(viewLifecycleOwner) { response ->
            if (response.status == true) {
//                image.recycle()
                response.data.imageName
                var key = (sectionName + imagequestion).replace(" ", "")
//                initialHashMap[key] = response.data.imageName
//                saveHashMapToPrefs(initialHashMap)
                dbHelper.insertKeyValue(key, response.data.imageName)

                dbHelper.updateQuestionAnswer(id,imagequestion,response.data.imageName)
                setAdapter()
            }
        }

        // Observe API error LiveData
        uploadImageViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error == "401") {
                sessionExpiredPopup()
            } else {
                showToast(error)
            }
        }

        // Observe loading state LiveData
        uploadImageViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                baseActivity!!.progressBarPB!!.show()
            } else {
                baseActivity!!.progressBarPB!!.dismiss()
            }
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            val image = data.extras!!.get("data") as Bitmap

            imageBase64 = convertImageToBase64String(image)
            adapter!!.showImage(image,imageBase64,imagePosition)

            setAdapter()
            uploadImageViewModel.uploadImageRequest(imageBase64)
            observer()
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri: Uri = data!!.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.parse(selectedImageUri.toString()))
            imageBase64 = convertImageToBase64String(bitmap)
            adapter!!.showImage(bitmap,imageBase64,imagePosition)

            setAdapter()
            uploadImageViewModel.uploadImageRequest(imageBase64)
            observer()
        }
    }



}