package com.poseidonapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.poseidonapp.R
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.databinding.FragmentAddOrganizationBinding
import com.poseidonapp.model.organization.Organization
import com.poseidonapp.ui.adapter.OrganizationAdapter
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.HandleClickListener
import com.poseidonapp.utils.SharedPref
import com.poseidonapp.viewmodel.addOrganization.AddOrganizationViewModel
import com.poseidonapp.viewmodel.organization.OrganizationViewModel
import com.poseidonapp.workorder.activities.BaseFragment
import de.hdodenhof.circleimageview.CircleImageView


class AddOrganizationFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentAddOrganizationBinding? = null
    lateinit var organizationViewModel: OrganizationViewModel
    lateinit var addOrganizationViewModel: AddOrganizationViewModel
    var organizeLists: List<Organization>? = null
    lateinit var sharedPref: SharedPref
    var adapter: OrganizationAdapter? = null
    private val REQUEST_CAMERA = 103
    private val REQUEST_GALLERY = 102
    private var ivprofile:CircleImageView?=null
    private var profileBitmap: Bitmap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = FragmentAddOrganizationBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this
        sharedPref = SharedPref(requireContext())
        organizeLists = ArrayList()
        organizationViewModel = ViewModelProvider(this)[OrganizationViewModel::class.java]
        addOrganizationViewModel = ViewModelProvider(this)[AddOrganizationViewModel::class.java]
        organizationViewModel.organizationsRequest(sharedPref.getString(Const.TOKEN))
        observer()
        searchView()
    }

//    fun setAdapter(organizeLists: ArrayList<Organization>) {
//        CoroutineScope(Dispatchers.Main).launch {
//            if (organizeLists.isEmpty()) {
//                binding?.noDataFoundTV?.visibility = View.VISIBLE
//                binding?.rvOrganization?.visibility = View.GONE
//            } else {
//                binding?.noDataFoundTV?.visibility = View.GONE
//                binding?.rvOrganization?.visibility = View.VISIBLE
//                withContext(Dispatchers.IO) {
//                    adapter = OrganizationAdapter(this@AddOrganizationFragment,
//                        organizeLists){ position, report ->
//                        findNavController().navigate(R.id.addressesFragment)
//                    }
//                }
//                binding!!.rvOrganization.layoutManager = LinearLayoutManager(context)
//                binding!!.rvOrganization.adapter = adapter
//            }
//        }
//    }


    fun setAdapter(organizeLists: ArrayList<Organization>) {
        // Initialize the filtered list
        var filteredList = ArrayList(organizeLists)

        if (filteredList.isEmpty()) {
            binding?.noDataFoundTV?.visibility = View.VISIBLE
            binding?.rvOrganization?.visibility = View.GONE
        } else {
            binding?.noDataFoundTV?.visibility = View.GONE
            binding?.rvOrganization?.visibility = View.VISIBLE
        }
        // Set the adapter
        adapter = OrganizationAdapter(filteredList) { position, name, id ->

            var bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("organizationId", id)
            findNavController().navigate(R.id.addressesFragment, bundle)
        }
        binding!!.rvOrganization.layoutManager = LinearLayoutManager(context)
        binding!!.rvOrganization.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    private fun observer() {
        //Organization
        organizationViewModel.organizationsSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                organizeLists = it.data.organizations
                setAdapter(organizeLists as ArrayList)
//                it.data.organizations?.let { organizations ->
//                    organizeLists?.addAll(organizations)
//                }
//                if(organizeLists?.isNotEmpty() == true) {
//                    setAdapter()
//                    binding?.noDataFoundTV?.visibility = View.GONE
//                } else {
//                    binding?.noDataFoundTV?.visibility = View.VISIBLE
//                }
            }
        })
        organizationViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }
        organizationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }


    }

    fun addOrganizationObserver() {
        //Add Organization

        addOrganizationViewModel.addOrganizationSuccess.observe(viewLifecycleOwner, Observer {
            if (it.status == true) {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                organizationViewModel.organizationsRequest(sharedPref.getString(Const.TOKEN))
                observer()
            }
        })
        addOrganizationViewModel.apiError.observe(viewLifecycleOwner) { error ->
            if (error.toString() == "401") {
                sessionExpiredPopup()
            } else {
                // Handle other errors
            }
        }
        addOrganizationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgessBar()
            } else {
                hideProgessBar()
            }
        }
    }

    private fun sessionExpiredPopup() {
        val dialog = Dialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
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
        when (view.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }

            R.id.ivaddCircle -> {
                showAddOrganizationDialog()
            }

        }
    }

//    fun progressbar() {
//        Handler().postDelayed({
//            progressbar()!!
//        }, 5000/* 5 second */)
//    }

    @SuppressLint("MissingInflatedId")
    private fun showAddOrganizationDialog() {
        // Set ivProfile to a global variable to be used in onActivityResult
        val organizationdialog = Dialog(requireContext(), R.style.CustomBottomSheetStyle)
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_addorganization, null)
        organizationdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        organizationdialog.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.25f)
        }
        organizationdialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        organizationdialog.setContentView(dialogView)
        val tvSubmit = dialogView.findViewById<TextView>(R.id.tvSubmit)
        val etOrganizationName = dialogView.findViewById<EditText>(R.id.etOrganizationName)
        val etRequestedname = dialogView.findViewById<EditText>(R.id.etRequestedname)
        val etEmailAddress = dialogView.findViewById<EditText>(R.id.etEmailAddress)
        val etPhoneNumber = dialogView.findViewById<EditText>(R.id.etPhoneNumber)
        val ivCancel = dialogView.findViewById<ImageView>(R.id.ivCancel)
        val ivCamera  = dialogView.findViewById<ImageView>(R.id.ivCamera)
        ivprofile = dialogView.findViewById(R.id.ivprofile)

        ivCancel.setOnClickListener {
            organizationdialog.dismiss()
        }
        ivCamera .setOnClickListener {
            showImagePickerDialog()
        }


        tvSubmit.setOnClickListener {
            val email = etEmailAddress.text.toString().trim()

            if (etOrganizationName.text.toString().equals("") || etRequestedname.text.toString()
                    .equals("") || email.isEmpty() || etPhoneNumber.text.toString().equals("")
            ) {
                Toast.makeText(context, "Please fill both fields", Toast.LENGTH_LONG).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_LONG)
                    .show()
            } else {
                addOrganizationViewModel!!.addOrganizationRequest(
                    fullName = etRequestedname.text!!.toString(),
                    emailAddress = email,
                    phoneNumber = etPhoneNumber.text.toString(),
                    organizationName = etOrganizationName.text!!.toString(),
                )
                organizationdialog.dismiss()
                addOrganizationObserver()
            }


        }
        organizationdialog.show()
    }

    fun searchView() {
        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the data based on the query
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = organizeLists!!.filter {
            it.requestName.contains(query!!, ignoreCase = true)
        }
        setAdapter(ArrayList(filteredList))
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (options[which]) {
                "Camera" -> openCamera()
                "Gallery" -> openGallery()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {
                        ivprofile?.setImageBitmap(imageBitmap) // Set image on dialog ivProfile
                    } else {
                        Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                REQUEST_GALLERY -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        try {
                            val inputStream =
                                requireContext().contentResolver.openInputStream(imageUri)
                            val imageBitmap = BitmapFactory.decodeStream(inputStream)
                            ivprofile?.setImageBitmap(imageBitmap) // Set image on dialog ivProfile
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}


