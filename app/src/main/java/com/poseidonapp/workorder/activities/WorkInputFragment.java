package com.poseidonapp.workorder.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.DottedBorder;
import com.itextpdf.layout.border.DoubleBorder;
import com.itextpdf.layout.border.InsetBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.poseidonapp.R;
import com.poseidonapp.database.DatabaseHelper;
import com.poseidonapp.model.deleteemergencyrequest.DeletedEmergencyRequestResponse;
import com.poseidonapp.ui.activities.LoginActivity;
import com.poseidonapp.ui.base.BaseFragment;
import com.poseidonapp.utils.Const;
import com.poseidonapp.viewmodel.deleteEmergency.DeleteEmergencyViewModel;
import com.poseidonapp.workorder.adapter.LaborAdapter;
import com.poseidonapp.workorder.adapter.MaterialAdapter;
import com.poseidonapp.workorder.model.LaborData;
import com.poseidonapp.workorder.model.MaterialData;
import com.poseidonapp.workorder.util.SharedPref;
import com.poseidonapp.workorder.util.SwipeToDeleteCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkInputFragment extends BaseFragment implements View.OnClickListener {

    TextView addMaterialTV, cancelTV, doneTV, addMaterialppTV,
            addsignTV, addimageTV, clearTV, addlaborTV, priceTV,
            amountTV, dateoforderTV, startingdateTV,
            submitTV, addlaborppTV, laborTotalTV, resetTV, checboxTV,
            addServiceppTV, addserviceTV, firesprinklerTV,
            descriptionservicemmTV, firesprinklerSSTV,
            firetextTV, gross_total_TV, language_enTV, language_esTV;
    LinearLayout addmaterialpopup, addlaborpopup,
            addsignaturepopup, materialheadingLL,
            laborchargesLL, labortableData,
            servicedescLL, servicedescmmLL,
            servicefireLL, signatureimageIVLL,
            addservicepopup, workinputMainLayoutLL, totalLabourAmt;
    Spinner  laborSP, firesprinklerSP;
//    Spinner materialTV, sizeTV, laborSP, firesprinklerSP;
    EditText materialTV, sizeTV;
    Dialog dialog;
    EditText qtyET, hoursET, toET, termsET,
            ordertakenbyET, customerordernumberET,
            phoneET, jobnamenumberET, joblocationET, descriptionworkET,
            jobphoneET, otherchrgsNameET, otherchrgsAmountET,
            descriptionserviceET, workorderbyET, descriptionserviceConditionTV,
            numberofworkersrequiredET;

    String[] materials = {};
    String[] size = {};
    String[] labour = {};
    String[] service = {};
    String[] laborprice = {
            "109", "163.50", "218", "117.23"
    };

//    String[] materials = {
//            getResources().getString(R.string.cpvc_pipe), getResources().getString(R.string.steel_pipe), getResources().getString(R.string.cpvc_fitting), getResources().getString(R.string.steel_fitting), getResources().getString(R.string.s_r_head), getResources().getString(R.string.con_head), getResources().getString(R.string.dry_head), getResources().getString(R.string.valves)
//    };
//    String[] size = {
//            getResources().getString(R.string.zero_point_five_in), getResources().getString(R.string.zero_point_seventyfive_in), getResources().getString(R.string.one_inch), getResources().getString(R.string.one_point_tewenty_five_in), getResources().getString(R.string.one_point_fifty_in), getResources().getString(R.string.two_in), getResources().getString(R.string.two_point_five_in), getResources().getString(R.string.three_in), getResources().getString(R.string.four_in), getResources().getString(R.string.six_in), getResources().getString(R.string.eight_in)
//    };
//    String[] labor = {
//            getResources().getString(R.string.regular), getResources().getString(R.string.overtime), getResources().getString(R.string.double_time), getResources().getString(R.string.ny_prevailing)
//    };


    /*String[] service = {
            getResources().getString(R.string.left_in_service), getResources().getString(R.string.left_offand_out_of_service)
    };*/

    MaterialData data = new MaterialData();

    LaborData laborData = new LaborData();

    ArrayList<LaborData> labordataList = new ArrayList<LaborData>();

    ArrayList<MaterialData> meterialdatList = new ArrayList<MaterialData>();

    Gson gson = new Gson();
    MaterialAdapter adapter;

    LaborAdapter laborAdapter;

    String materialvalue, sizevalue, laborvalue, servicevalue;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private SignaturePad mSignaturePad;

    ImageView signatureimageIV, ivBack;

    CheckBox dayworkCB, contractCB, extraCB;

    double hours;
    double price;
    double amount;

    double grosstotal = 0;

    double labortotalgt = 0;
    float otherchargesgt;
    int workers = 1;
    Bitmap bmp, scaledbmp,
            laborImagerc, scaledlaborImagerc,
            materialbmp, scaledmaterialbmp,
            signbmp, scaledsignbmp,
            descriptionofworkBmp, scaleddescriptionofworkBmp,
            firespdescBmp, scaledfirespdescBmp, totalAmtLabourBmp, scaledAmtLabourBmp;

    //    int pageWidth = 1200;
    int pageWidth;
    int pageHeight;

    RecyclerView laborrecyclerView;
    LinearLayout mainLL;
    RecyclerView matrialrecyclerView;

    AlertDialog.Builder builder;
    boolean material = false;
    boolean laborcheck = false;
    boolean servicecheck = false;
    boolean signcheck = false;
    int screenheight, screenwidth;
    SharedPref pref;
    public static final int REQUEST_CODE = 1;

    int servicehandleposition = 0;
    LinearLayout totalLaborAmtLL;

    String id="",
            name,
            terms,
            orderTakenBy,
            orderNumber,
            dateOfOrder,
            phone,
            jobName,
            jobLocation,
            jobPhone,
            startingDate;

    private DeleteEmergencyViewModel emergencyRequestViewModel;

    Switch SwSubmitPayment;

    Boolean switchToggle=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
            name = getArguments().getString("name");
            terms = getArguments().getString("terms");
            orderTakenBy = getArguments().getString("orderTakenBy");
            orderNumber = getArguments().getString("orderNumber");
            dateOfOrder = getArguments().getString("dateOfOrder");
            phone = getArguments().getString("phone");
            jobName = getArguments().getString("jobName");
            jobLocation = getArguments().getString("jobLocation");
            jobPhone = getArguments().getString("jobPhone");
            startingDate = getArguments().getString("startingDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_work_input, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();

    }

    public void initUI() {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPref pref = new SharedPref(requireContext());

        if (pref.getLanguageSelected().equals("En")) {

            updateLanguage("en");
        }
        if (pref.getLanguageSelected().equals("Es")) {

            updateLanguage("es");
        }

//    setContentView(R.layout.activity_work_input);


//    if (getActivity().getSupportActionBar() != null) {
//        getSupportActionBar().hide();
//    }

        materials = new String[]{
                getResources().getString(R.string.cpvc_pipe), getResources().getString(R.string.steel_pipe), getResources().getString(R.string.cpvc_fitting), getResources().getString(R.string.steel_fitting), getResources().getString(R.string.s_r_head), getResources().getString(R.string.con_head), getResources().getString(R.string.dry_head), getResources().getString(R.string.valves)
        };
        size = new String[]{
                getResources().getString(R.string.zero_point_five_in), getResources().getString(R.string.zero_point_seventyfive_in), getResources().getString(R.string.one_inch), getResources().getString(R.string.one_point_tewenty_five_in), getResources().getString(R.string.one_point_fifty_in), getResources().getString(R.string.two_in), getResources().getString(R.string.two_point_five_in), getResources().getString(R.string.three_in), getResources().getString(R.string.four_in), getResources().getString(R.string.six_in), getResources().getString(R.string.eight_in)
        };
        labour = new String[]{
                getResources().getString(R.string.regular), getResources().getString(R.string.overtime), getResources().getString(R.string.double_time), getResources().getString(R.string.ny_prevailing)
        };
        service = new String[]{
                getResources().getString(R.string.left_in_service), getResources().getString(R.string.left_offand_out_of_service)
        };


        init();

        if (pref.getLanguageSelected().equals("En")) {

            changeButtonColour();
            updateLanguage("en");
        }
        if (pref.getLanguageSelected().equals("Es")) {

            changeButtonColor();
            updateLanguage("es");
        }


        language_enTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeButtonColour();
                pref.setLanguageSelected("En");

                updateLanguage("en");

                /*Intent refresh = new Intent(getBaseContext(), WorkInputActivity.class);
                startActivity(refresh);
                finish();
*/
                onResume();
            }
        });

        language_esTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("call", "21212121");

                changeButtonColor();
                pref.setLanguageSelected("Es");

                updateLanguage("es");

                /*Intent refresh = new Intent(getBaseContext(), WorkInputActivity.class);
                startActivity(refresh);
                finish();
*/
                onResume();

            }
        });
        observer();
    }



    private void observer() {
        emergencyRequestViewModel.getDeletedEmergencySuccess().observe(getViewLifecycleOwner(), new Observer<DeletedEmergencyRequestResponse>() {
            @Override
            public void onChanged(DeletedEmergencyRequestResponse it) {
                if (it.getStatus()) {
                    try {
                        generatePDF();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        emergencyRequestViewModel.getApiError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String it) {
                if (it.equals("401")) {
                    sessionExpiredPopup();
                } else {
                    showToast(it);
                }
            }
        });

        emergencyRequestViewModel.isLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean it) {
                if (it) {
                    getBaseActivity().getProgressBarPB().show();
                } else {
                    getBaseActivity().getProgressBarPB().dismiss();
                }
            }
        });
    }

    public void updateLanguage(String language) {
        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getActivity().createConfigurationContext(config);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }


    public void gotoLoginSignUpActivity(int isLogout) {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.putExtra(Const.IS_LOGOUT, isLogout);
        startActivity(intent);
        requireActivity().finish();
    }

    private void sessionExpiredPopup() {
        Dialog dialog = new Dialog(requireActivity(), R.style.CustomBottomSheetDialogTheme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_token_expired);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getContext().deleteDatabase(DatabaseHelper.DATABASE_NAME);
                sharedPref.clearPref();
                gotoLoginSignUpActivity(0);
            }
        });

        dialog.show();
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }


    private void changeButtonColour() {
        language_enTV.setBackgroundResource(R.drawable.custom_shape_lang_button);
        language_enTV.setTextColor(Color.parseColor("#2b2b48"));

        language_esTV.setBackgroundResource(R.drawable.language_bg_tv);
        language_esTV.setTextColor(Color.parseColor("#f2f2f2"));
    }

    private void changeButtonColor() {
        language_esTV.setBackgroundResource(R.drawable.custom_shape_lang_button);
        language_esTV.setTextColor(Color.parseColor("#2b2b48"));

        language_enTV.setBackgroundResource(R.drawable.language_bg_tv);
        language_enTV.setTextColor(Color.parseColor("#f2f2f2"));
    }



    private void init() {
        Log.e("called", "called");

        emergencyRequestViewModel = new ViewModelProvider(this).get(DeleteEmergencyViewModel.class);

        language_enTV = requireView().findViewById(R.id.language_enTV);
        language_esTV = requireView().findViewById(R.id.language_esTV);
        pref = new SharedPref(requireContext());
        addMaterialTV = requireView().findViewById(R.id.addMaterialTV);
        addMaterialTV.setOnClickListener(this);
        addsignTV = requireView().findViewById(R.id.addsignTV);
        addsignTV.setOnClickListener(this);
        signatureimageIV = requireView().findViewById(R.id.signatureimageIV);
        addlaborTV = requireView().findViewById(R.id.addlaborTV);
        addlaborTV.setOnClickListener(this);
        dateoforderTV = requireView().findViewById(R.id.dateoforderTV);
        dateoforderTV.setOnClickListener(this);
        startingdateTV = requireView().findViewById(R.id.startingdateTV);
        startingdateTV.setOnClickListener(this);
        materialheadingLL = requireView().findViewById(R.id.materialheadingLL);
        SwSubmitPayment = requireView().findViewById(R.id.SwSubmitPayment);

        totalLaborAmtLL = requireView().findViewById(R.id.totalLaborAmtLL);
        totalLaborAmtLL.setDrawingCacheEnabled(true);
        submitTV = requireView().findViewById(R.id.submitTV);
        ivBack = requireView().findViewById(R.id.ivBack);
        submitTV.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        builder = new AlertDialog.Builder(requireActivity());

        resetTV = requireView().findViewById(R.id.resetTV);
        resetTV.setOnClickListener(this);
        mainLL = requireView().findViewById(R.id.mainLL);
        mainLL.setDrawingCacheEnabled(false);
        //  totalLabourAmt=findViewById(R.id.totalLabourAmt);
        // totalLabourAmt.setDrawingCacheEnabled(true);
        laborrecyclerView = requireView().findViewById(R.id.datalaborRV);
        laborrecyclerView.setDrawingCacheEnabled(false);

        matrialrecyclerView = requireView().findViewById(R.id.datamaterialRV);

        dayworkCB = requireView().findViewById(R.id.dayworkCB);
        dayworkCB.setChecked(true);
        contractCB = requireView().findViewById(R.id.contractCB);
        extraCB = requireView().findViewById(R.id.extraCB);
        labortableData = requireView().findViewById(R.id.labortableData);
        laborTotalTV = requireView().findViewById(R.id.laborTotalTV);
        laborchargesLL = requireView().findViewById(R.id.laborchargesLL);
        servicedescmmLL = requireView().findViewById(R.id.servicedescmmLL);
        servicefireLL = requireView().findViewById(R.id.servicefireLL);
        firesprinklerTV = requireView().findViewById(R.id.firesprinklerTV);
        descriptionservicemmTV = requireView().findViewById(R.id.descriptionservicemmTV);
        workorderbyET = requireView().findViewById(R.id.workorderbyET);
        signatureimageIVLL = requireView().findViewById(R.id.signatureimageIVLL);
        workinputMainLayoutLL = requireView().findViewById(R.id.workinputMainLayoutLL);
        firetextTV = requireView().findViewById(R.id.firetextTV);
        gross_total_TV = requireView().findViewById(R.id.gross_total_TV);


        toET = requireView().findViewById(R.id.toET);
        termsET = requireView().findViewById(R.id.termsET);
        ordertakenbyET = requireView().findViewById(R.id.ordertakenbyET);
        customerordernumberET = requireView().findViewById(R.id.customerordernumberET);
        phoneET = requireView().findViewById(R.id.phoneET);
        jobnamenumberET = requireView().findViewById(R.id.jobnamenumberET);
        joblocationET = requireView().findViewById(R.id.joblocationET);
        jobphoneET = requireView().findViewById(R.id.jobphoneET);
        checboxTV = requireView().findViewById(R.id.checboxTV);
        descriptionworkET = requireView().findViewById(R.id.descriptionworkET);
        otherchrgsNameET = requireView().findViewById(R.id.otherchrgsNameET);
        otherchrgsAmountET = requireView().findViewById(R.id.otherchrgsAmountET);
        numberofworkersrequiredET = requireView().findViewById(R.id.numberofworkersrequiredET);
        addserviceTV = requireView().findViewById(R.id.addserviceTV);
        addserviceTV.setOnClickListener(this);
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
//        scaledbmp = Bitmap.createScaledBitmap(bmp, pageWidth - 20, 500, false);
        descriptionserviceConditionTV = requireView().findViewById(R.id.descriptionserviceConditionTV);


        SwSubmitPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The switch is turned ON
                    switchToggle=true;
//                    Toast.makeText(requireActivity(), "Switch is ON", Toast.LENGTH_SHORT).show();
                } else {
                    // The switch is turned OFF
                    switchToggle=false;
//                    Toast.makeText(requireActivity(), "Switch is OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });



        /*toET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                pref.setString("To", toET.getText().toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


/*
        termsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pref.setString("terms", termsET.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
/*
        ordertakenbyET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pref.setString("OrderTaken", ordertakenbyET.getText().toString());


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
/*
        customerordernumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("CustomerOrderNumber", customerordernumberET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
 /*       phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("Phone", phoneET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
      /*  jobnamenumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("JobPhoneNumber", jobnamenumberET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
/*        joblocationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("JobLocation", joblocationET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
/*
        jobphoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("JobPhone", jobphoneET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
/*
        descriptionworkET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("Description", descriptionworkET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

/*        otherchrgsNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("OtherChargesText", otherchrgsNameET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
/*
        otherchrgsAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                pref.setString("OtherChargesAmt", otherchrgsAmountET.getText().toString());
                String str = laborTotalTV.getText().toString();
                str = str.replace("$", "");
                if (otherchrgsAmountET.getText().toString().isEmpty()) {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = 0;
                    if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        workers = 1;
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    }
                } else {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                    if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        workers = 1;
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
/*
        numberofworkersrequiredET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                pref.setString("Numberofworkersrequired", numberofworkersrequiredET.getText().toString());
                String str = laborTotalTV.getText().toString();
                str = str.replace("$", "");
                if (otherchrgsAmountET.getText().toString().isEmpty()) {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = 0;
                    if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        workers = 1;
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    }
                } else {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                    if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        workers = 1;
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
 /*       workorderbyET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pref.setString("WorkOrderBy", workorderbyET.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/

        dayworkCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxTV.setText(getResources().getString(R.string.day_work));
                    contractCB.setChecked(false);
                    extraCB.setChecked(false);
//                    pref.setString("checkBox", "1");

                }
            }
        });
        contractCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxTV.setText(R.string.Contract);
                    dayworkCB.setChecked(false);
                    extraCB.setChecked(false);
//                    pref.setString("checkBox", "2");
                }
            }
        });
        extraCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checboxTV.setText(R.string.Extra);
                    dayworkCB.setChecked(false);
                    contractCB.setChecked(false);
//                    pref.setString("checkBox", "3");
                }
            }
        });


        // focus and keyboard hide
        toET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    toET.clearFocus();
                    hideKeyboardFrom(requireActivity(), toET);
                    return true;
                }
                return false;
            }
        });


        termsET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    termsET.clearFocus();
                    hideKeyboardFrom(requireActivity(), termsET);
                    return true;
                }
                return false;
            }
        });

        ordertakenbyET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    ordertakenbyET.clearFocus();
                    hideKeyboardFrom(requireActivity(), ordertakenbyET);
                    return true;
                }
                return false;
            }
        });

        customerordernumberET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    customerordernumberET.clearFocus();
                    hideKeyboardFrom(requireActivity(), customerordernumberET);
                    return true;
                }
                return false;
            }
        });

        phoneET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    phoneET.clearFocus();
                    hideKeyboardFrom(requireActivity(), phoneET);
                    return true;
                }
                return false;
            }
        });
        jobnamenumberET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    jobnamenumberET.clearFocus();
                    hideKeyboardFrom(requireActivity(), jobnamenumberET);
                    return true;
                }
                return false;
            }
        });
        joblocationET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    joblocationET.clearFocus();
                    hideKeyboardFrom(requireActivity(), joblocationET);
                    return true;
                }
                return false;
            }
        });
        jobphoneET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    jobphoneET.clearFocus();
                    hideKeyboardFrom(requireActivity(), jobphoneET);
                    return true;
                }
                return false;
            }
        });
        descriptionworkET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    descriptionworkET.clearFocus();
                    hideKeyboardFrom(requireActivity(), descriptionworkET);
                    return true;
                }
                return false;
            }
        });
        otherchrgsNameET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    otherchrgsNameET.clearFocus();
                    hideKeyboardFrom(requireActivity(), otherchrgsNameET);
                    return true;
                }
                return false;
            }
        });

/*
        otherchrgsAmountET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    otherchrgsAmountET.clearFocus();
                    hideKeyboardFrom(requireActivity(), otherchrgsAmountET);

                    String str = laborTotalTV.getText().toString();
                    str = str.replace("$", "");
                    if (otherchrgsAmountET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = 0;

                        if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            workers = 1;
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        }
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                            workers = 1;
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                            workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        }
                    }


                    return true;
                }
                return false;
            }
        });
*/

        workorderbyET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    workorderbyET.clearFocus();
                    hideKeyboardFrom(requireActivity(), workorderbyET);
                    return true;
                }
                return false;
            }
        });
/*
        numberofworkersrequiredET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    numberofworkersrequiredET.clearFocus();
                    hideKeyboardFrom(requireActivity(), numberofworkersrequiredET);
                    String str = laborTotalTV.getText().toString();
                    str = str.replace("$", "");
                    if (otherchrgsAmountET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = 0;
                        if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            workers = 1;
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        }
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                            workers = 1;
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                            workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
*/

        // for swipe delete functionality
        enableSwipeToDeleteAndUndo();
        enableMMSwipeToDeleteAndUndo();

   /*     String str = laborTotalTV.getText().toString();
        str = str.replace("$", "");
        if (otherchrgsAmountET.getText().toString().isEmpty()) {
            labortotalgt = Double.parseDouble(str);
            otherchargesgt = 0;
            if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                labortotalgt = Double.parseDouble(str);
                workers = 1;
                grosstotal = (workers * labortotalgt) + otherchargesgt;
                gross_total_TV.setText("$" + grosstotal);
            } else {
                labortotalgt = Double.parseDouble(str);
                workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                grosstotal = (workers * labortotalgt) + otherchargesgt;
                gross_total_TV.setText("$" + grosstotal);
            }
        } else {
            labortotalgt = Double.parseDouble(str);
            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
            if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                labortotalgt = Double.parseDouble(str);
                otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                workers = 1;
                grosstotal = (workers * labortotalgt) + otherchargesgt;
                gross_total_TV.setText("$" + grosstotal);
            } else {
                labortotalgt = Double.parseDouble(str);
                otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                grosstotal = (workers * labortotalgt) + otherchargesgt;
                gross_total_TV.setText("$" + grosstotal);
            }
        }
*/

    }

    // hide keyboard
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // service popup
    private void showServicePopup() {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_service_popup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();
        dialog.setCancelable(true);
        firesprinklerSP = dialog.findViewById(R.id.firesprinklerSP);
        descriptionserviceET = dialog.findViewById(R.id.descriptionserviceET);
        addServiceppTV = dialog.findViewById(R.id.addServiceppTV);
        servicedescLL = dialog.findViewById(R.id.servicedescLL);
        addservicepopup = dialog.findViewById(R.id.addservicepopup);
        firesprinklerSSTV = dialog.findViewById(R.id.firesprinklerSSTV);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        window.setGravity(Gravity.CENTER);
        descriptionserviceET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    descriptionserviceET.clearFocus();
                    hideKeyboardFrom(requireActivity(), descriptionserviceET);
                    return true;
                }
                return false;
            }
        });
        List<String> serviceitems = new ArrayList<>();
        for (int i = 0; i < service.length; i++) {
            serviceitems.add(service[i]);
            Gson gson = new Gson();

            String json = gson.toJson(serviceitems);
            pref.setString("ServiceTable", json);

        }
        ArrayAdapter<String> materialAdapter = new ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, serviceitems);
        firesprinklerSP.setAdapter(materialAdapter);
        firesprinklerSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                servicevalue = serviceitems.get(position);
                String item = parent.getItemAtPosition(position).toString();
                if (item == service[0]) {
                    servicedescLL.setVisibility(View.GONE);
                    servicedescmmLL.setVisibility(View.GONE);
                } else if (item == service[1]) {
                    servicedescLL.setVisibility(View.VISIBLE);
                }
                servicehandleposition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        addServiceppTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (servicehandleposition == 0) {
                    servicedescLL.setVisibility(View.GONE);
                    servicedescmmLL.setVisibility(View.GONE);
                    firesprinklerTV.setText(service[0]);
                    firesprinklerSSTV.setText(service[0]);
                    servicefireLL.setVisibility(View.VISIBLE);
                    descriptionservicemmTV.setText(descriptionserviceET.getText().toString());

                    if (firesprinklerSSTV.getText().toString().equals(service[1]) && descriptionserviceET.getText().toString().isEmpty()) {
                        alertDialog(getResources().getString(R.string.Please_add_description));
                    } else {
                        dialog.dismiss();
                        pref.setboolen("servicecheck", true);
                        servicecheck = true;
                    }
                } else {
                    servicedescLL.setVisibility(View.VISIBLE);
                    servicedescmmLL.setVisibility(View.VISIBLE);
                    firesprinklerTV.setText(service[1]);
                    firesprinklerSSTV.setText(service[1]);
                    servicefireLL.setVisibility(View.VISIBLE);
                    descriptionservicemmTV.setText(descriptionserviceET.getText().toString());
                    if (firesprinklerSSTV.getText().toString().equals(service[1]) && descriptionserviceET.getText().toString().isEmpty()) {
                        alertDialog(getResources().getString(R.string.please_add_description));
                    } else {
                        dialog.dismiss();
                        pref.setboolen("servicecheck", true);
                        servicecheck = true;
                    }
                }
                pref.setString("FireSprinklerSystem", firesprinklerTV.getText().toString());
//                pref.setString("DescriptionService", descriptionserviceET.getText().toString());
            }
        });

        addservicepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (servicehandleposition == 0) {
                    servicefireLL.setVisibility(View.GONE);
                    firesprinklerTV.setText("");
                    firesprinklerSSTV.setText("");
                    descriptionservicemmTV.setText("");
                    dialog.dismiss();
                    pref.setboolen("servicecheck", false);
                    servicecheck = false;
                } else {
                    servicefireLL.setVisibility(View.GONE);
                    firesprinklerTV.setText("");
                    firesprinklerSSTV.setText("");
                    descriptionservicemmTV.setText("");
                    dialog.dismiss();
                    pref.setboolen("servicecheck", false);
                    servicecheck = false;
                }

            }
        });

    }

    // for checking valid phone number
    public static boolean isValidnumber(String s) {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // The number should be of 10 digits.

        // Creating a Pattern class object
//        Pattern p = Pattern.compile("^\\d{12}$");
        Pattern p = Pattern.compile("^[+]?[0-9]{6,13}$");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(s);

        // Returning boolean value
        return (m.matches());
    }


    // check all field validation
    private boolean submitCheckAllFields() {

        if (toET.getText().toString().isEmpty() || termsET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.please_provide_all_parameters));
            return false;
        } else if (ordertakenbyET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.add_order_taken_by_field));
            return false;
        } else if (customerordernumberET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_customer_order_number));
            return false;
        } else if (dateoforderTV.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_date_of_order));
            return false;
        } else if (phoneET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_phone_number));
            return false;
        } else if (!isValidnumber(phoneET.getText().toString())) {
            alertDialog(getResources().getString(R.string.enter_valid_phone_number));
            return false;
        } else if (jobnamenumberET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_job_name));
            return false;
        } else if (joblocationET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_job_location));
            return false;
        } else if (jobphoneET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_job_phone));
            return false;
        } else if (!isValidnumber(jobphoneET.getText().toString())) {
            alertDialog(getResources().getString(R.string.enter_valid_job_phone_number));
            return false;
        } else if (startingdateTV.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_stating_date));
            return false;
        } else if (descriptionworkET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_description_of_work));
            return false;
        } else if (!pref.getboolen("material")) {
            alertDialog(getResources().getString(R.string.add_material));
            return false;
        } /*else if (!pref.getboolen("laborcheck")) {
            alertDialog("Add labor");
            return false;
        } */ else if (!pref.getboolen("servicecheck")) {
            alertDialog(getResources().getString(R.string.add_service));
            return false;
        } else if (workorderbyET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_the_work_order_by_person_name));
            return false;
        } else if (numberofworkersrequiredET.getText().toString().isEmpty()) {
            alertDialog(getResources().getString(R.string.enter_the_number_of_workers_required));
            return false;
        }/* else if (!pref.getboolen("signcheck")) {
            alertDialog(getResources().getString(R.string.add_signature));
            return false;
        }*/ else {
            return true;
        }
    }


    // Material popup
    private void openMaterialDialog() {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_material_popup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();
        dialog.setCancelable(true);
        qtyET = dialog.findViewById(R.id.qtyET);
        qtyET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                    qtyET.clearFocus();
                    hideKeyboardFrom(requireActivity(), qtyET);
                    return true;
                }
                return false;
            }
        });
        materialTV = dialog.findViewById(R.id.materialTV);
        sizeTV = dialog.findViewById(R.id.sizeTV);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        window.setGravity(Gravity.CENTER);
        addmaterialpopup = dialog.findViewById(R.id.addmaterialpopup);
        addmaterialpopup.setOnClickListener(this);
        addMaterialppTV = dialog.findViewById(R.id.addMaterialppTV);


       /* List<String> materialitems = new ArrayList<>();
        for (int i = 0; i < materials.length; i++) {
            materialitems.add(materials[i]);
        }
        ArrayAdapter<String> materialAdapter = new ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, materialitems);
        materialTV.setAdapter(materialAdapter);
        materialTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                materialvalue = materialitems.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        List<String> sizeitems = new ArrayList<>();
        for (int i = 0; i < size.length; i++) {
            sizeitems.add(size[i]);
        }
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, sizeitems);
        sizeTV.setAdapter(sizeAdapter);
        sizeTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sizevalue = sizeitems.get(position);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
*/

        addMaterialppTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qtyET.getText().toString().isEmpty()) {
                    alertDialog("All fields are required");
                }else if (materialTV.getText().toString().isEmpty()) {
                    alertDialog("All fields are required");
                } else if (sizeTV.getText().toString().isEmpty()) {
                    alertDialog("All fields are required");
                }  else {
                    dialog.dismiss();
                    String qty = qtyET.getText().toString();
                    data = new MaterialData();
                    data.setQty(qty);
                    data.setMaterial(materialTV.getText().toString());
                    data.setSize(sizeTV.getText().toString());
//                    data.setMaterial(materialTV.getSelectedItem().toString());
//                    data.setSize(sizeTV.getSelectedItem().toString());
                    meterialdatList.add(data);
                    setAdapter(meterialdatList);

                    materialheadingLL.setVisibility(View.VISIBLE);

                    String materialJson = gson.toJson(meterialdatList);
//                    pref.setString("MaterialItems", materialJson);

                    pref.setboolen("material", true);
                    material = true;
                    requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                }
            }
        });

    }

    // Labor Popup
    private void showLaborDataPopup() {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_labor_popup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();
        dialog.setCancelable(true);
        hoursET = dialog.findViewById(R.id.hoursET);
        hoursET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hoursET.clearFocus();
                    hideKeyboardFrom(requireActivity(), hoursET);
                    return true;
                }
                return false;
            }
        });
        laborSP = dialog.findViewById(R.id.laborSP);
        priceTV = dialog.findViewById(R.id.priceTV);
        amountTV = dialog.findViewById(R.id.amountTV);
        addlaborppTV = dialog.findViewById(R.id.addlaborppTV);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        window.setGravity(Gravity.CENTER);
        addlaborpopup = dialog.findViewById(R.id.addlaborpopup);
        addlaborpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        List<String> laboritem = new ArrayList<>();
        for (int i = 0; i < labour.length; i++) {
            laboritem.add(labour[i]);
        }

        ArrayAdapter<String> laborAdapter = new ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, laboritem);
        laborSP.setAdapter(laborAdapter);
        laborSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                laborvalue = laboritem.get(position);
                String item = parent.getItemAtPosition(position).toString();
                if (hoursET.getText().toString().isEmpty()) {
                    if (item == "Regular") {
                        priceTV.setText(laborprice[0]);
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    } else if (item == "Overtime") {
                        priceTV.setText(laborprice[1]);
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    } else if (item == "Double time") {
                        priceTV.setText(laborprice[2]);
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    } else if (item == "NY Prevailing") {
                        priceTV.setText(laborprice[3]);
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    }
                } else {
                    if (item == "Regular") {
                        priceTV.setText(laborprice[0]);
                        hours = Double.parseDouble(hoursET.getText().toString());
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    } else if (item == "Overtime") {
                        priceTV.setText(laborprice[1]);
                        hours = Double.parseDouble(hoursET.getText().toString());
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    } else if (item == "Double time") {
                        priceTV.setText(laborprice[2]);
                        hours = Double.parseDouble(hoursET.getText().toString());
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    } else if (item == "NY Prevailing") {
                        priceTV.setText(laborprice[3]);
                        hours = Double.parseDouble(hoursET.getText().toString());
                        price = Double.parseDouble(priceTV.getText().toString());
                        amount = hours * price;
                        amountTV.setText(String.valueOf(amount));
                    }
                }
                Log.e("@@@@@", "laboritem" + item);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        hoursET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (hoursET.getText().toString().isEmpty()) {
                    hours = 0;
                    price = 0;
                    amount = hours * price;
                    amountTV.setText(String.valueOf(amount));
                } else {
                    hours = Double.parseDouble(hoursET.getText().toString());
                    price = Double.parseDouble(priceTV.getText().toString());
                    amount = hours * price;
                    amountTV.setText(String.valueOf(amount));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addlaborppTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hoursET.getText().toString().isEmpty() || priceTV.getText().toString().isEmpty() || amountTV.getText().toString().isEmpty() || hoursET.getText().toString().trim().equals("0.0")) {
                    hoursET.setText("");
                    hoursET.getText().clear();
                    alertDialog("All fields are required");

                } else {
                    dialog.dismiss();
                    String hrs = hoursET.getText().toString();
                    laborData = new LaborData();
                    laborData.setHrs(hrs);
                    laborData.setLabor(laborSP.getSelectedItem().toString());
                    laborData.setRate(priceTV.getText().toString());
                    laborData.setAmount(amountTV.getText().toString());
                    labordataList.add(laborData);
                    laborAdapter(labordataList);
                    laborchargesLL.setVisibility(View.VISIBLE);

                    String labordataJson = gson.toJson(labordataList);
                    pref.setString("LaborDataSharedPref", labordataJson);


                    double totalamount = 0;
                    for (int i = 0; i < labordataList.size(); i++) {
                        totalamount += labordataList.get(i).getAmount();
                    }
                    laborAdapter.notifyDataSetChanged();
                    laborTotalTV.setText(String.valueOf("$" + totalamount));

                    pref.setString("Amount", String.valueOf(totalamount));

                    String str = laborTotalTV.getText().toString();
                    str = str.replace("$", "");
                    if (otherchrgsAmountET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = 0;
                        if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            workers = 1;
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        }
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                            workers = 1;
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                            workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                            grosstotal = (workers * labortotalgt) + otherchargesgt;
                            gross_total_TV.setText("$" + grosstotal);
                        }
                    }


                    pref.setboolen("laborcheck", true);
                    laborcheck = true;
                    laborAdapter.notifyDataSetChanged();

                    amountTV.setText("0.0");
                    hours = 0;
                    hoursET.setText(String.valueOf(hours));
                }
                amountTV.setText("0.0");
                requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

    }


    //  alert popup
/*    public void alertDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
        dialog.setMessage(message);
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }*/

    //reset popup
    private void resetAlertDialog(String message) {
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pref.clear();
                        toET.setText("");
                        termsET.setText("");
                        ordertakenbyET.setText("");
                        customerordernumberET.setText("");
                        dateoforderTV.setText("");
                        phoneET.setText("");
                        jobnamenumberET.setText("");
                        joblocationET.setText("");
                        jobphoneET.setText("");
                        startingdateTV.setText("");
                        checboxTV.setText("");
                        dayworkCB.setChecked(false);
                        contractCB.setChecked(false);
                        extraCB.setChecked(false);
                        otherchrgsNameET.setText("");
                        otherchrgsAmountET.setText("");
                        numberofworkersrequiredET.setText("");

                        meterialdatList.clear();
                        materialheadingLL.setVisibility(View.GONE);
                        pref.setboolen("material", false);
                        material = false;

                        labordataList.clear();
                        laborchargesLL.setVisibility(View.GONE);
                        pref.setboolen("laborcheck", false);
                        laborcheck = false;

                        firesprinklerTV.setText("");
                        descriptionservicemmTV.setText("");
                        pref.setboolen("servicecheck", false);
                        servicecheck = false;
                        servicefireLL.setVisibility(View.GONE);
                        signatureimageIVLL.setVisibility(View.GONE);
                        signatureimageIV.setImageResource(0);
                        descriptionworkET.setText("");
                        workorderbyET.setText("");
                        pref.setboolen("signcheck", false);
                        signcheck = false;

                        customerordernumberET.clearFocus();
                        toET.clearFocus();
                        termsET.clearFocus();
                        ordertakenbyET.clearFocus();
                        customerordernumberET.clearFocus();
                        phoneET.clearFocus();
                        jobnamenumberET.clearFocus();
                        joblocationET.clearFocus();
                        jobphoneET.clearFocus();
                        descriptionworkET.clearFocus();
                        otherchrgsNameET.clearFocus();
                        otherchrgsAmountET.clearFocus();
                        descriptionserviceConditionTV.clearFocus();
                        workorderbyET.clearFocus();
                        numberofworkersrequiredET.clearFocus();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getString(R.string.reset));
        alert.show();
    }


    // Materail adapter set
    private void setAdapter(List<MaterialData> data) {
        matrialrecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new MaterialAdapter(requireContext(), data);
        matrialrecyclerView.setAdapter(adapter);
    }

    // labor adaptor set
    private void laborAdapter(List<LaborData> data) {
        laborrecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        laborAdapter = new LaborAdapter(requireActivity(), data);
        laborrecyclerView.setAdapter(laborAdapter);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.addMaterialTV) {
            openMaterialDialog();

        } else if (id == R.id.addmaterialpopup) {
            dialog.dismiss();

        } else if (id == R.id.addsignTV) {
            showSignaturePopup();

        } else if (id == R.id.addlaborTV) {
            showLaborDataPopup();

        } else if (id == R.id.dateoforderTV) {
            customerordernumberET.clearFocus();
            toET.clearFocus();
            termsET.clearFocus();
            ordertakenbyET.clearFocus();
            phoneET.clearFocus();
            jobnamenumberET.clearFocus();
            jobphoneET.clearFocus();
            joblocationET.clearFocus();
            handleDateButton();

        } else if (id == R.id.ivBack) {
//    finish();
            requireActivity().onBackPressed();

        } else if (id == R.id.startingdateTV) {
            customerordernumberET.clearFocus();
            toET.clearFocus();
            termsET.clearFocus();
            ordertakenbyET.clearFocus();
            phoneET.clearFocus();
            jobnamenumberET.clearFocus();
            jobphoneET.clearFocus();
            joblocationET.clearFocus();
            handleDateButton1();

        } else if (id == R.id.resetTV) {
            resetAlertDialog(getString(R.string.are_you_sure_you_want_to_reset_the_fields));

        } else if (id == R.id.addserviceTV) {
            showServicePopup();

        } else if (id == R.id.submitTV) {
            if (submitCheckAllFields()) {
                if (!otherchrgsNameET.getText().toString().isEmpty()) {
                    if (otherchrgsAmountET.getText().toString().isEmpty()) {
                        alertDialog(getResources().getString(R.string.enter_other_charge_amount));
                    } else {
                        Log.e("data@#@#", "" + labordataList.size());
//                showProgessBar();
//                emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN),id);

                        if (switchToggle) {
                            Fragment fragment = new CreditCardPaymentFragment();
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
//                    fragment.setArguments(bundle);
                            ft.replace(R.id.frame_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        } else {
                            try {
                                generatePDF();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (!otherchrgsAmountET.getText().toString().isEmpty()) {
                    if (otherchrgsNameET.getText().toString().isEmpty()) {
                        alertDialog(getResources().getString(R.string.enter_other_charges_title));
                    } else {
                        Log.e("data@#@#", "" + labordataList.size());
//                showProgessBar();
//                emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN),id);

                        if (switchToggle) {
                            Fragment fragment = new CreditCardPaymentFragment();
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
//                    fragment.setArguments(bundle);
                            ft.replace(R.id.frame_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        } else {
                            try {
                                generatePDF();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.e("data@#@#", "" + labordataList.size());
//            showProgessBar();
//            emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN),id);

                    if (switchToggle) {
                        Fragment fragment = new CreditCardPaymentFragment();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
//                fragment.setArguments(bundle);
                        ft.replace(R.id.frame_container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        try {
                            generatePDF();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

/*
        switch (view.getId()) {
            case R.id.addMaterialTV:
                openMaterialDialog();
                break;
            case R.id.addmaterialpopup:
                dialog.dismiss();
                break;

            case R.id.addsignTV:
                showSignaturePopup();
                break;

            case R.id.addlaborTV:
                showLaborDataPopup();
                break;
            case R.id.dateoforderTV:
                customerordernumberET.clearFocus();
                toET.clearFocus();
                termsET.clearFocus();
                ordertakenbyET.clearFocus();
                customerordernumberET.clearFocus();
                phoneET.clearFocus();
                jobnamenumberET.clearFocus();
                jobphoneET.clearFocus();
                joblocationET.clearFocus();
                handleDateButton();
                break;

            case R.id.ivBack:
//                finish();
                requireActivity().onBackPressed();
                break;

            case R.id.startingdateTV:
                customerordernumberET.clearFocus();
                toET.clearFocus();
                termsET.clearFocus();
                ordertakenbyET.clearFocus();
                customerordernumberET.clearFocus();
                phoneET.clearFocus();
                jobnamenumberET.clearFocus();
                jobphoneET.clearFocus();
                joblocationET.clearFocus();
                handleDateButton1();
                break;
            case R.id.resetTV:
                resetAlertDialog(getString(R.string.are_you_sure_you_want_to_reset_the_fields));
                break;
            case R.id.addserviceTV:
                showServicePopup();
                break;
            case R.id.submitTV:

                if (submitCheckAllFields()) {
                    if (!otherchrgsNameET.getText().toString().isEmpty()) {
                        if (otherchrgsAmountET.getText().toString().isEmpty()) {
                            alertDialog(getResources().getString(R.string.enter_other_charge_amount));
                        } else {

                            Log.e("data@#@#", "" + labordataList.size());
//                            showProgessBar();
//                                emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN),id);

                            if (switchToggle){
                                Fragment fragment = new CreditCardPaymentFragment();
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
//                                fragment.setArguments(bundle);
                                ft.replace(R.id.frame_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            } else {
                                try {
                                    generatePDF();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    } else if (!otherchrgsAmountET.getText().toString().isEmpty()) {
                        if (otherchrgsNameET.getText().toString().isEmpty()) {
                            alertDialog(getResources().getString(R.string.enter_other_charges_title));
                        } else {
                            Log.e("data@#@#", "" + labordataList.size());
//                            showProgessBar();
//                                emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN),id);

                            if (switchToggle){
                                Fragment fragment = new CreditCardPaymentFragment();
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
//                                fragment.setArguments(bundle);
                                ft.replace(R.id.frame_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            } else {
                                try {
                                    generatePDF();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Log.e("data@#@#", "" + labordataList.size());
//                        showProgessBar();
//                            emergencyRequestViewModel.emergencyRequest(sharedPref.getString(Const.TOKEN),id);

                        if (switchToggle){
                            Fragment fragment = new CreditCardPaymentFragment();
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
//                                fragment.setArguments(bundle);
                            ft.replace(R.id.frame_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        } else {
                            try {
                                generatePDF();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {

                }

                break;
        }
*/


    }

    // getting bitmap image screenshot of the view
    public static Bitmap getViewBitmap(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
    /*public static Bitmap getViewBitmap2(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.GRAY);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap getViewBitmap1(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }
*/
    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("MMM-dd-yyyy", calendar1).toString();
                dateoforderTV.setText(dateText);
//                pref.setString("DateOfOrder", dateoforderTV.getText().toString());
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();

    }


    private void handleDateButton1() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("MMM-dd-yyyy", calendar1).toString();
                startingdateTV.setText(dateText);

//                pref.setString("StartDate", startingdateTV.getText().toString());
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }


    // signature popup dialog
    private void showSignaturePopup() {
        dialog = new Dialog(requireContext());
//        dialog = new Dialog(WorkInputActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.signature_poup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        window.setGravity(Gravity.CENTER);

        addimageTV = dialog.findViewById(R.id.addimageTV);
        addsignaturepopup = dialog.findViewById(R.id.addsignaturepopup);
        addsignaturepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        clearTV = dialog.findViewById(R.id.clearTV);
        mSignaturePad = dialog.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                addimageTV.setEnabled(true);
                clearTV.setEnabled(true);
            }

            @Override
            public void onClear() {
                addimageTV.setEnabled(false);
                clearTV.setEnabled(false);
            }
        });

        clearTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        addimageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSignaturePad.isEmpty()) {
                    alertDialog("Please add signature");
                } else {


                    // Commented on 13 September 2023

                    Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    byte[] image = stream.toByteArray();
                    String img_str = Base64.encodeToString(image, 0);
//                    decode string to image
                    String base = img_str;
                    byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
                    signatureimageIV.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                    pref.setString("SignatureImage", img_str);
                    dialog.dismiss();
                    signatureimageIVLL.setVisibility(View.VISIBLE);
                    signatureimageIV.setVisibility(View.VISIBLE);
                    pref.setboolen("signcheck", true);
                    signcheck = true;
                }
            }
        });

    }

    // get file dir
//    public File getAlbumStorageDir(String albumName) {
//        // Get the directory for the user's public pictures directory.
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), albumName);
//        if (!file.mkdirs()) {
//            Log.e("SignaturePad", "Directory not created");
//        }
//        return file;
//    }

    // save bitmap to jpg
   /* public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

        stream.close();
    }*/

   /* public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/

//    private void scanMediaFile(File photo) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(photo);
//        mediaScanIntent.setData(contentUri);
//        WorkInputActivity.this.sendBroadcast(mediaScanIntent);
//    }

   /* public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void generatePDF() throws FileNotFoundException {

        int descriptionwidth = descriptionworkET.getMeasuredWidth();
        int descritpionheight = descriptionworkET.getMeasuredHeight();
        int materialwidth = materialheadingLL.getMeasuredWidth();
        int materialheight = materialheadingLL.getMeasuredHeight();

        int firedescwidth = descriptionservicemmTV.getMeasuredWidth();
        int firedescheight = descriptionservicemmTV.getMeasuredHeight();

        int laborwidth = mainLL.getMeasuredWidth();
        int laborheight = mainLL.getMeasuredHeight();


        pageWidth = this.getResources().getDisplayMetrics().widthPixels;
        pageHeight = this.getResources().getDisplayMetrics().heightPixels;

        int totalAmtwidth = totalLaborAmtLL.getMeasuredWidth();
        int totalAmtHeight = totalLaborAmtLL.getMeasuredHeight();

/*
        totalAmtLabourBmp = getViewBitmap(totalLaborAmtLL);
        scaledAmtLabourBmp = Bitmap.createScaledBitmap(totalAmtLabourBmp, totalAmtwidth, totalAmtHeight, false);

        Log.e("called@#@#", "" + laborheight);
*/

//        materialbmp = getViewBitmap(materialheadingLL);
//        scaledmaterialbmp = Bitmap.createScaledBitmap(materialbmp, materialwidth, materialheight, false);

        signbmp = getViewBitmap(signatureimageIV);
        scaledsignbmp = Bitmap.createScaledBitmap(signbmp, pageWidth - 500, 450, false);

//        descriptionofworkBmp = getViewBitmap(descriptionworkET);
//        scaleddescriptionofworkBmp = Bitmap.createScaledBitmap(descriptionofworkBmp, descriptionwidth, descritpionheight, false);

/*
        laborImagerc = getViewBitmap(mainLL);
        scaledlaborImagerc = Bitmap.createScaledBitmap(laborImagerc, laborwidth, laborheight, false);
*/


//        screenwidth = workinputMainLayoutLL.getMeasuredWidth();
//        screenheight = workinputMainLayoutLL.getMeasuredHeight();


        String filename = "workorder.pdf";
        File file = new File(requireActivity().getCacheDir(), filename);
        PdfWriter writer;
        OutputStream outputStream;

        outputStream = new FileOutputStream(file);

        writer = new PdfWriter(outputStream);

        PdfDocument mypdfdocument = new PdfDocument(writer);
        Document document = new Document(mypdfdocument, PageSize.A4);
        document.setMargins(20, 20, 25, 20);


        Drawable d = requireActivity().getDrawable(R.drawable.banner);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);


        Paragraph jobinvoice = new Paragraph(getResources().getString(R.string.work_order)).setFontSize(21).setTextAlignment(TextAlignment.LEFT);
        Paragraph notfinalprice = new Paragraph(getResources().getString(R.string.this_is_not_the_final_price)).setFontSize(14).setTextAlignment(TextAlignment.LEFT);
        Paragraph line = new Paragraph("________________________________________________________________").setFontSize(15).setTextAlignment(TextAlignment.LEFT).setBold();

        Paragraph to = new Paragraph(getResources().getString(R.string.To) + " : " + toET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph terms = new Paragraph(getResources().getString(R.string.Terms) + " : " + termsET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph ordertakenby = new Paragraph(getResources().getString(R.string.order_taken_by) + " :  " + ordertakenbyET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph customerordernumber = new Paragraph(getResources().getString(R.string.customer_s_order_number) + " :  " + customerordernumberET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph dateoforder = new Paragraph(getResources().getString(R.string.date_of_order) + " :  " + dateoforderTV.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph phone = new Paragraph(getResources().getString(R.string.phone) + " :  " + phoneET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph jobnamenumber = new Paragraph(getResources().getString(R.string.job_name_number) + " :  " + jobnamenumberET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph joblocation = new Paragraph(getResources().getString(R.string.job_location) + " :  " + joblocationET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph jobphone = new Paragraph(getResources().getString(R.string.job_phone) + " :  " + jobphoneET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph startingdate = new Paragraph(getResources().getString(R.string.starting_date) + " :  " + startingdateTV.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph worktype = new Paragraph(getResources().getString(R.string.work_type) + checboxTV.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph descriptionofwork = new Paragraph(getResources().getString(R.string.description_of_work) + " :  " + descriptionworkET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph otherchargesnameandamount = new Paragraph("" + otherchrgsNameET.getText() + ": " + otherchrgsAmountET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph materialdetails = new Paragraph(getResources().getString(R.string.material_detail)).setFontSize(15).setTextAlignment(TextAlignment.LEFT);

     /*   ByteArrayOutputStream stream1=new ByteArrayOutputStream();
        materialbmp.compress(Bitmap.CompressFormat.PNG,100,stream1);
        byte[] bitmapData1=stream1.toByteArray();

        ImageData imageDatamaterial= ImageDataFactory.create(bitmapData1);
        Image imagematerial=new Image(imageDatamaterial);
*/
        float columnwidth[] = {80f, 200f, 80f};
        Table table = new Table(columnwidth);
        table.setTextAlignment(TextAlignment.CENTER);
        table.addCell(getString(R.string.qty));
        table.addCell(getString(R.string.material));
        table.addCell(getString(R.string.size));
        for (int i = 0; i < meterialdatList.size(); i++) {
            table.addCell("" + adapter.getData().get(i).getQty());
            table.addCell("" + adapter.getData().get(i).getMaterial());
            table.addCell("" + adapter.getData().get(i).getSize());
        }

//        Paragraph labordetails=new Paragraph("Labor details :  ").setFontSize(15).setTextAlignment(TextAlignment.LEFT);

        float laborcolumnwidth[] = {80f, 150f, 80f, 150f};
        Table table2 = new Table(laborcolumnwidth);
        table2.setTextAlignment(TextAlignment.CENTER);
        table2.addCell("HRS");
        table2.addCell("LABOR");
        table2.addCell("RATE");
        table2.addCell("AMOUNT");
        for (int i = 0; i < labordataList.size(); i++) {
            table2.addCell("" + laborAdapter.getData().get(i).getHrs());
            table2.addCell("" + laborAdapter.getData().get(i).getLabor());
            table2.addCell("" + laborAdapter.getData().get(i).getRate());
            table2.addCell("" + laborAdapter.getData().get(i).getAmount());
        }

        float labortotalcolumnwidth[] = {310f, 150f};
        Table table3 = new Table(labortotalcolumnwidth);
        table3.setTextAlignment(TextAlignment.CENTER);
        table3.addCell("TOTAL AMOUNT");
        table3.addCell("" + laborTotalTV.getText());

        Paragraph labortotal = new Paragraph("LABOR TOTAL :  " + "" + laborTotalTV.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        if (otherchrgsAmountET.getText().toString().isEmpty()) {
            otherchrgsAmountET.setText("0");
        }
        Paragraph othercharges = new Paragraph(getResources().getString(R.string.other_charges) + " :  " + otherchrgsAmountET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph firesprinklersystem = new Paragraph(getString(R.string.fire_sprinkler_system) + " : " + firesprinklerTV.getText()).setFontSize(15).setFontColor(com.itextpdf.kernel.color.Color.RED).setTextAlignment(TextAlignment.LEFT);
//        Paragraph firesprinklersystem1=new Paragraph(getString(R.string.fire_sprinkler_system) + firesprinklerTV.getText()).setFontSize(15).setFontColor(com.itextpdf.kernel.color.Color.BLUE).setTextAlignment(TextAlignment.LEFT);
        Paragraph firesprinklersystemNote = new Paragraph(
                getString(R.string.any_system_that_cannot_be) + "\n" +
                        getString(R.string.same_buisness_day_will_require) + "\n" +
                        getString(R.string.and_provided_by_the_building) + "\n" +
                        getString(R.string.company_and_or_building_order) + "\n" +
                        getString(R.string.cut_into_will_be_left) + "\n" +
                        getString(R.string.cure_time)).setFontSize(15).setFontColor(com.itextpdf.kernel.color.Color.RED).setTextAlignment(TextAlignment.LEFT);
        Paragraph description = new Paragraph(getString(R.string.Description) + " : \n" + descriptionservicemmTV.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph workorderby = new Paragraph(getString(R.string.work_order_by) + " : " + workorderbyET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph numberofworkers = new Paragraph(getString(R.string.number_of_workers_required) + " : " + numberofworkersrequiredET.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph grosstotal = new Paragraph(getString(R.string.gross_total) + " : " + gross_total_TV.getText()).setFontSize(15).setTextAlignment(TextAlignment.LEFT).setBold();
        Paragraph signature=new Paragraph(getString(R.string.signature)+" : ").setFontSize(15).setTextAlignment(TextAlignment.LEFT);
        Paragraph blank = new Paragraph(" ").setFontSize(15).setTextAlignment(TextAlignment.LEFT);

        Paragraph labourheading = new Paragraph(getString(R.string.our_labor_rates_are_as_follows) + "\n").setFontSize(20).setTextAlignment(TextAlignment.LEFT);
        Paragraph labourtemplate = new Paragraph(getString(R.string.straight_time_m) + "\n" +
                getString(R.string._per_hr_per_man) +
                getString(R.string.anything_over_the_hrs_is) +
                getString(R.string.overtime_mf_pm) +
                getString(R.string.per_hr_per_man_total) +
                getString(R.string.anything_over_the_hrs_is) +
                getString(R.string.double_time_sunday_) +
                getString(R.string.per_hr_each_total_) +
                getString(R.string.anything_over_the_hrs_is)).setFontSize(15).setTextAlignment(TextAlignment.LEFT);


        ByteArrayOutputStream stream2=new ByteArrayOutputStream();
        signbmp.compress(Bitmap.CompressFormat.PNG,100,stream2);
        byte[] bitmapData2=stream2.toByteArray();

        ImageData imageDatamaterial2= ImageDataFactory.create(bitmapData2);
        Image signatureimage=new Image(imageDatamaterial2);
        signatureimage.scaleToFit(250,300);
        signatureimage.setHorizontalAlignment(HorizontalAlignment.LEFT);
        Border border = new DashedBorder(com.itextpdf.kernel.color.Color.BLACK, 2);
        Border border2 = new DottedBorder(com.itextpdf.kernel.color.Color.BLACK, 2);
        Border border3 = new DoubleBorder(com.itextpdf.kernel.color.Color.BLACK, 1);
        Border border4 = new InsetBorder(1);


       signatureimage.setBorder(border3);


        // trying image border---------------------------------------


        // trying image border---------------------------------------

        document.add(image);
        document.add(jobinvoice);
        document.add(notfinalprice);
        document.add(line);
        document.add(blank);
        document.add(to);
        document.add(terms);
        document.add(ordertakenby);
        document.add(customerordernumber);
        document.add(dateoforder);
        document.add(phone);
        document.add(jobnamenumber);
        document.add(joblocation);
        document.add(jobphone);
        document.add(startingdate);
        document.add(worktype);
        document.add(descriptionofwork);

        if (!otherchrgsNameET.getText().toString().equals("")) {
            document.add(otherchargesnameandamount);
            document.add(materialdetails);
            document.add(table);
            document.add(blank);
//            document.add(labordetails);
//            document.add(table2);
//            document.add(table3);
//            document.add(blank);
//            document.add(labortotal);

            document.add(othercharges);
            document.add(numberofworkers);
//            document.add(grosstotal);
            if (firesprinklerTV.getText().toString().equalsIgnoreCase("left off and out of service")) {
                document.add(firesprinklersystem);
                document.add(firesprinklersystemNote);
                document.add(description);
//                document.add(numberofworkers);
//                document.add(grosstotal);
                document.add(workorderby);
                document.add(signature);
                document.add(blank);
                document.add(signatureimage);
            } else {
                document.add(firesprinklersystem);
//                document.add(numberofworkers);
//                document.add(grosstotal);
                document.add(workorderby);
                document.add(signature);
                document.add(blank);
                document.add(signatureimage);
            }

        } else {
            document.add(materialdetails);
            document.add(table);
            document.add(blank);

//            document.add(labordetails);
//            document.add(table2);
//            document.add(table3);
//            document.add(blank);
//            document.add(labortotal);

            document.add(othercharges);
            document.add(numberofworkers);
//            document.add(grosstotal);
            if (firesprinklerTV.getText().toString().equalsIgnoreCase("left off and out of service")) {
                document.add(firesprinklersystem);
                document.add(firesprinklersystemNote);
                document.add(description);
//                document.add(numberofworkers);
//                document.add(grosstotal);
                document.add(workorderby);
                document.add(signature);
                document.add(blank);
                document.add(signatureimage);
            } else {
                document.add(firesprinklersystem);
//                document.add(numberofworkers);
//                document.add(grosstotal);
                document.add(workorderby);
                document.add(signature);
                document.add(blank);
                document.add(signatureimage);
            }


        }

        document.add(blank);
        document.add(labourheading);
        document.add(labourtemplate);
        document.add(blank);
        document.close();


        Bundle bundle = new Bundle();
        bundle.putString("pdfName", filename);

        pref.setboolen("sentEmail",true);

        Fragment fragment = new PDFFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.frame_container, fragment);
        ft.addToBackStack(null);
        ft.commit();


// Intent mainIntent = new Intent(requireActivity(), PDFActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("pdfName", filename);
//        mainIntent.putExtras(bundle);
//        requireActivity().startActivity(mainIntent);

    }


    @Override
    public void onResume() {
        super.onResume();

//        setContentView(R.layout.activity_work_input);

        materials = new String[]{
                getResources().getString(R.string.cpvc_pipe), getResources().getString(R.string.steel_pipe), getResources().getString(R.string.cpvc_fitting), getResources().getString(R.string.steel_fitting), getResources().getString(R.string.s_r_head), getResources().getString(R.string.con_head), getResources().getString(R.string.dry_head), getResources().getString(R.string.valves)
        };
        size = new String[]{
                getResources().getString(R.string.zero_point_five_in), getResources().getString(R.string.zero_point_seventyfive_in), getResources().getString(R.string.one_inch), getResources().getString(R.string.one_point_tewenty_five_in), getResources().getString(R.string.one_point_fifty_in), getResources().getString(R.string.two_in), getResources().getString(R.string.two_point_five_in), getResources().getString(R.string.three_in), getResources().getString(R.string.four_in), getResources().getString(R.string.six_in), getResources().getString(R.string.eight_in)
        };
        labour = new String[]{
                getResources().getString(R.string.regular), getResources().getString(R.string.overtime), getResources().getString(R.string.double_time), getResources().getString(R.string.ny_prevailing)
        };
        service = new String[]{
                getResources().getString(R.string.left_in_service), getResources().getString(R.string.left_offand_out_of_service)
        };


        init();

        if (pref.getLanguageSelected().equals("En")) {

            changeButtonColour();
            updateLanguage("en");
        }
        if (pref.getLanguageSelected().equals("Es")) {

            changeButtonColor();
            updateLanguage("es");
        }


        language_enTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeButtonColour();
                pref.setLanguageSelected("En");

                updateLanguage("en");

                /*Intent refresh = new Intent(getBaseContext(), WorkInputActivity.class);
                startActivity(refresh);
                finish();
*/
                onResume();
            }
        });


        language_esTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("call", "21212121");

                changeButtonColor();
                pref.setLanguageSelected("Es");

                updateLanguage("es");

                /*Intent refresh = new Intent(getBaseContext(), WorkInputActivity.class);
                startActivity(refresh);
                finish();
*/
                onResume();

            }
        });


        // save edittext fields
//        toET.setText(pref.getString("To"));
        toET.setText(name);
//        termsET.setText(pref.getString("terms"));
        termsET.setText(terms);
//        ordertakenbyET.setText(pref.getString("OrderTaken"));
        ordertakenbyET.setText(orderTakenBy);
//        customerordernumberET.setText(pref.getString("CustomerOrderNumber"));
        customerordernumberET.setText(orderNumber);
//        phoneET.setText(pref.getString("Phone"));
        phoneET.setText(phone);
//        jobnamenumberET.setText(pref.getString("JobPhoneNumber"));
        jobnamenumberET.setText(jobName);
//        joblocationET.setText(pref.getString("JobLocation"));
        joblocationET.setText(jobLocation);
//        jobphoneET.setText(pref.getString("JobPhone"));
        jobphoneET.setText(jobPhone);
//        descriptionworkET.setText(pref.getString("Description"));
//        otherchrgsNameET.setText(pref.getString("OtherChargesText"));
//        otherchrgsAmountET.setText(pref.getString("OtherChargesAmt"));
//        numberofworkersrequiredET.setText(pref.getString("Numberofworkersrequired"));
//        workorderbyET.setText(pref.getString("WorkOrderBy"));
//        dateoforderTV.setText(pref.getString("DateOfOrder"));
        dateoforderTV.setText(dateOfOrder);
//        startingdateTV.setText(pref.getString("StartDate"));
        startingdateTV.setText(startingDate);

        //save service data
        /*if (!pref.getString("FireSprinklerSystem").isEmpty()) {
            servicefireLL.setVisibility(View.VISIBLE);
            servicedescmmLL.setVisibility(View.GONE);
            firesprinklerTV.setText(pref.getString("FireSprinklerSystem"));
        }
        if (!pref.getString("DescriptionService").isEmpty()) {
            servicedescmmLL.setVisibility(View.VISIBLE);
            descriptionservicemmTV.setText(pref.getString("DescriptionService"));
            firesprinklerTV.setText(pref.getString("FireSprinklerSystem"));
        }*/

        //save material data
  /*      String materialItems = pref.getString("MaterialItems");
        Type type = new TypeToken<List<MaterialData>>() {
        }.getType();
        if (!materialItems.isEmpty()) {
            meterialdatList = gson.fromJson(materialItems, type);
            setAdapter(meterialdatList);
            if (meterialdatList.size() > 0) {
                materialheadingLL.setVisibility(View.VISIBLE);
            } else {
                materialheadingLL.setVisibility(View.GONE);
            }
        }
*/

        //save labour data
//        String laborData = pref.getString("LaborDataSharedPref");
//        Type type1 = new TypeToken<List<LaborData>>() {
//        }.getType();

/*
        if (!laborData.isEmpty()) {
            labordataList = gson.fromJson(laborData, type1);
            laborAdapter(labordataList);
            if (labordataList.size() > 0) {
                laborchargesLL.setVisibility(View.VISIBLE);
            } else {
                laborchargesLL.setVisibility(View.GONE);
            }
            laborTotalTV.setText("$" + pref.getString("Amount"));


            String str = laborTotalTV.getText().toString();
            str = str.replace("$", "");
            if (otherchrgsAmountET.getText().toString().isEmpty()) {
                labortotalgt = Double.parseDouble(str);
                otherchargesgt = 0;
                if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                    labortotalgt = Double.parseDouble(str);
                    workers = 1;
                    grosstotal = (workers * labortotalgt) + otherchargesgt;
                    gross_total_TV.setText("$" + grosstotal);
                } else {
                    labortotalgt = Double.parseDouble(str);
                    workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                    grosstotal = (workers * labortotalgt) + otherchargesgt;
                    gross_total_TV.setText("$" + grosstotal);
                }
            } else {
                labortotalgt = Double.parseDouble(str);
                otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                    workers = 1;
                    grosstotal = (workers * labortotalgt) + otherchargesgt;
                    gross_total_TV.setText("$" + grosstotal);
                } else {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                    workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                    grosstotal = (workers * labortotalgt) + otherchargesgt;
                    gross_total_TV.setText("$" + grosstotal);
                }
            }


        }
*/

        // save signature image
//        String image = pref.getString("SignatureImage");
//        if (!image.equals("")) {
//            String base = image;
//            byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
//            signatureimageIV.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
//            signatureimageIVLL.setVisibility(View.VISIBLE);
//        } else {
//            signatureimageIVLL.setVisibility(View.GONE);
//        }

        // save check box status
   /*     String checkBox = pref.getString("checkBox");
        switch (checkBox) {
            case "1":
                dayworkCB.setChecked(true);
                break;
            case "2":
                contractCB.setChecked(true);
                break;
            case "3":
                extraCB.setChecked(true);
                break;
            default:
                dayworkCB.setChecked(true);
        }*/

        if (otherchrgsAmountET.getText().toString().contains("0")) {
            otherchrgsAmountET.setText(otherchrgsAmountET.getText().toString());
        }

//        hideProgessBar();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                LaborData item = laborAdapter.getData().get(position);
                laborAdapter.removeItem(position);
                if (labordataList.size() == 0) {
                    laborchargesLL.setVisibility(View.GONE);
                    pref.setboolen("laborcheck", false);
                    laborcheck = false;
                } else {
                    laborchargesLL.setVisibility(View.VISIBLE);
                }
                double totalamount = 0;
                for (int ii = 0; ii < labordataList.size(); ii++) {
                    totalamount += labordataList.get(ii).getAmount();

                }
                laborTotalTV.setText(String.valueOf("$ " + totalamount));


                String str = laborTotalTV.getText().toString();
                str = str.replace("$", "");
                if (otherchrgsAmountET.getText().toString().isEmpty()) {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = 0;
                    if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        workers = 1;
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    }
                } else {
                    labortotalgt = Double.parseDouble(str);
                    otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));

                    if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        workers = 1;
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    } else {
                        labortotalgt = Double.parseDouble(str);
                        otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                        workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                        grosstotal = (workers * labortotalgt) + otherchargesgt;
                        gross_total_TV.setText("$" + grosstotal);
                    }
                }


                laborAdapter.notifyItemRemoved(position);
                Snackbar snackbar = Snackbar.make(laborchargesLL, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        laborAdapter.restoreItem(item, position);
                        laborrecyclerView.scrollToPosition(position);
                        double totalamount = 0;
                        for (int ii = 0; ii < labordataList.size(); ii++) {
                            totalamount += labordataList.get(ii).getAmount();
                        }
                        laborTotalTV.setText(String.valueOf("$" + totalamount));


                        String str = laborTotalTV.getText().toString();
                        str = str.replace("$", "");
                        if (otherchrgsAmountET.getText().toString().isEmpty()) {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = 0;
                            if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                                labortotalgt = Double.parseDouble(str);
                                workers = 1;
                                grosstotal = (workers * labortotalgt) + otherchargesgt;
                                gross_total_TV.setText("$" + grosstotal);
                            } else {
                                labortotalgt = Double.parseDouble(str);
                                workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                                grosstotal = (workers * labortotalgt) + otherchargesgt;
                                gross_total_TV.setText("$" + grosstotal);
                            }
                        } else {
                            labortotalgt = Double.parseDouble(str);
                            otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));

                            if (numberofworkersrequiredET.getText().toString().isEmpty()) {
                                labortotalgt = Double.parseDouble(str);
                                otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                                workers = 1;
                                grosstotal = (workers * labortotalgt) + otherchargesgt;
                                gross_total_TV.setText("$" + grosstotal);
                            } else {
                                labortotalgt = Double.parseDouble(str);
                                otherchargesgt = Float.parseFloat(pref.getString("OtherChargesAmt"));
                                workers = Integer.parseInt(numberofworkersrequiredET.getText().toString());
                                grosstotal = (workers * labortotalgt) + otherchargesgt;
                                gross_total_TV.setText("$" + grosstotal);
                            }
                        }


                        if (labordataList.size() == 0) {
                            laborchargesLL.setVisibility(View.GONE);
                        } else {
                            laborchargesLL.setVisibility(View.VISIBLE);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                pref.setString("Amount", String.valueOf(totalamount));
                String labordataJson = gson.toJson(labordataList);
                pref.setString("LaborDataSharedPref", labordataJson);
                laborAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(laborrecyclerView);

    }


    private void enableMMSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                MaterialData item = adapter.getData().get(position);
                adapter.removeItem(position);
                if (meterialdatList.size() == 0) {
                    materialheadingLL.setVisibility(View.GONE);
                    pref.setboolen("material", false);
                    material = false;
                } else {
                    materialheadingLL.setVisibility(View.VISIBLE);
                }
                Snackbar snackbar = Snackbar.make(materialheadingLL, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(item, position);
                        matrialrecyclerView.scrollToPosition(position);
                        adapter.notifyDataSetChanged();
                        if (meterialdatList.size() == 0) {
                            materialheadingLL.setVisibility(View.GONE);
                        } else {

                            materialheadingLL.setVisibility(View.VISIBLE);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                String materialJson = gson.toJson(meterialdatList);
//                pref.setString("MaterialItems", materialJson);
                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(matrialrecyclerView);
    }


}

