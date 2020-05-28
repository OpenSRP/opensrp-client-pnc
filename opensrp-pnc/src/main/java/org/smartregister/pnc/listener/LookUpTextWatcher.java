package org.smartregister.pnc.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import java.util.HashMap;
import java.util.Map;

public class LookUpTextWatcher implements TextWatcher {

    public LookUpTextWatcher(@NonNull JsonFormFragment jsonFormFragment, @NonNull View editText) {
        Map<String, String> lookUpFields = new HashMap<>();
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Do nothing
    }

    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        //Do nothing
    }

    public void afterTextChanged(Editable editable) {
        //Do nothing
    }

}