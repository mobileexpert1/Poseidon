package com.poseidonapp.workorder.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.poseidonapp.R;
import com.poseidonapp.workorder.ApiClient;
import com.poseidonapp.workorder.RetrofitAPI;
import com.poseidonapp.workorder.model.pdf.PdfResponse;
import com.poseidonapp.workorder.util.CircularProgressDialog;
import com.poseidonapp.workorder.util.SharedPref;
import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PDFFragment extends BaseFragment implements OnPageChangeListener {



    ImageView shareIV;
    LinearLayout backPdfTV;
    RequestBody pdfbody;
    String pdfnAME;
    Uri photoURI;
    File file;
    String filename, workOrderName;
    int  pageNumber = 0;
    SharedPref pref;
    CircularProgressDialog progressBar;

    Boolean sentEmail=true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pdfnAME = getArguments().getString("pdfName");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_p_d_f, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();

    }

    public void initUI(){
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

        hideProgessBar();
        backPdfTV=requireView().findViewById(R.id.backPdfTV);
        backPdfTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getBaseActivity().onBackPressed();
                requireActivity().onBackPressed();
            }
        });

        pref = new SharedPref(requireContext());
        workOrderName = pref.getString("JobPhoneNumber");


//        Bundle bundle = getIntent().getExtras();
//        pdfnAME = bundle.getString("pdfName");

        filename = "workorder.pdf";
        file = new File(requireContext().getCacheDir(), filename);
        postPdf();
        if (pref.getboolen("sentEmail")){
//            emailSent();
        }


        photoURI = Uri.fromFile(file);

        // Create New Blank Document
        PDFView pdfView = (PDFView) requireView().findViewById(R.id.pdfView);

        pdfView.fromUri(photoURI)
                .spacing(6)
                .autoSpacing(false)
                .fitEachPage(true)
                .pageSnap(true)
                .enableSwipe(true)
                .pageFling(true) // make a fling change only a single page like ViewPager
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .enableDoubletap(true)
                .load();

        shareIV=requireView().findViewById(R.id.shareIV);
        shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();

            }
        });




    }
    private void emailSent(){


        Uri contentUri = FileProvider.getUriForFile(requireActivity(), "com.poseidonapp.fileprovider", file);
        pref.setboolen("sentEmail",false);

        if (contentUri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("message/rfc822"); // Use this MIME type for email
            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@24-7fireprotection.com"});
            shareIntent.setDataAndType(contentUri, requireActivity().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            final PackageManager pm = requireActivity().getPackageManager();
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setPackage("com.google.android.gm");
            startActivity(shareIntent);
        }
    }

    /* private void emailSent(){
        Uri contentUri = FileProvider.getUriForFile(requireActivity(), "com.poseidonapp.fileprovider", file);

        if (contentUri != null) {

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@24-7fireprotection.com"});
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, requireActivity().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            final PackageManager pm = requireActivity().getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(shareIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") ||
                        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
            if (best != null)
                shareIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(shareIntent);
        }

    }*/



    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(requireActivity());
        pictureDialog.setTitle("Select an Option");
        String[] pictureDialogItems = {
                "Share with mail",
                "Other sharing options",
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Latest updated
                                Uri contentUri = FileProvider.getUriForFile(requireActivity(), "com.poseidonapp.fileprovider", file);

                                if (contentUri != null) {

                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                    shareIntent.setDataAndType(contentUri, requireActivity().getContentResolver().getType(contentUri));
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                    final PackageManager pm = requireActivity().getPackageManager();
                                    final List<ResolveInfo> matches = pm.queryIntentActivities(shareIntent, 0);
                                    ResolveInfo best = null;
                                    for (final ResolveInfo info : matches)
                                        if (info.activityInfo.packageName.endsWith(".gm") ||
                                                info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                                    if (best != null)
                                        shareIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                                    startActivity(shareIntent);
                                }

                                break;

                            case 1:

                                // Latest updated

                                Uri contentUri_ = FileProvider.getUriForFile(requireActivity(), "com.poseidonapp.fileprovider", file);
                                if (contentUri_ != null) {

                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                    shareIntent.setDataAndType(contentUri_, requireActivity().getContentResolver().getType(contentUri_));
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri_);
                                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                                }

                                break;
                        }
                    }
                });

        AlertDialog dialog = pictureDialog.create();

        // Prevent progress bar visibility on dismiss
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
        pictureDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }




    public void postPdf() {


        Log.e("post ", "runs");

        //   pdfbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =MultipartBody.Part.createFormData("pdf", file.getName(), requestFile);

        ApiClient apiClient = new ApiClient();
        RetrofitAPI apiRequest = apiClient.getApiClient();
        Log.e("@@@", workOrderName);
        Call<PdfResponse> call=apiRequest.uploadPdf(workOrderName, body);

        call.enqueue(new Callback<PdfResponse>() {
            @Override
            public void onResponse(Call<PdfResponse> call, Response<PdfResponse> response) {
                Log.e("call","response: "+response.toString());
                if (response.body().getStatus()){
                    /*Toast.makeText(PDFActivity.this, "Submit successful!", Toast.LENGTH_SHORT).show();*/
                }
            }

            @Override
            public void onFailure(Call<PdfResponse> call, Throwable t) {
                Log.e("failed ", t.toString());

            }
        });


        /*DataModel dataModel1 = new DataModel(idET.getText().toString());*/

/*        Call<responsePdf> call = retrofitAPI.uploadPdf(filename, pdfbody);

        call.enqueue(new Callback<responsePdf>() {
            @Override
            public void onResponse(Call<responsePdf> call, Response<responsePdf> response) {
                Toast.makeText(PDFActivity.this, "abd "+response.code(), Toast.LENGTH_SHORT).show();
                Log.e("onResponse   ", String.valueOf(response.code()));
                Log.e("onResponse   ", response.body().getStatus().toString());
                Log.e("onResponse   ", response.body().getError());

            }

            @Override
            public void onFailure(Call<responsePdf> call, Throwable t) {

            }






        });*/
    }




}