package com.poseidonapp.workorder.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.poseidonapp.ui.base.BaseActivity;
import com.poseidonapp.workorder.util.CircularProgressDialog;

public class BaseFragment extends Fragment {


    public BaseActivity baseActivity;
    CircularProgressDialog progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = new BaseActivity();
         progressBar = new CircularProgressDialog(requireActivity());
    }

    /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = new CircularProgressDialog(requireActivity());


    }*/

    protected void showProgessBar() {
        if (progressBar == null) {
            progressBar = new CircularProgressDialog(requireContext());
        }
        if (!progressBar.isShowing()) {
            progressBar.show();
        }
    }

    protected void hideProgessBar() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
    }
//    public void showProgessBar(){
//        progressBar.show();
//    }
//
//    public void hideProgessBar(){
//        progressBar.hide();
//    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
