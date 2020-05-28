package org.smartregister.pnc.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import java.util.HashMap;
import java.util.Map;

public class LookUpTextWatcher implements TextWatcher {

    private Map<String, String> lookUpFields;
    private final View editText;
    private final JsonFormFragment jsonFormFragment;

    public LookUpTextWatcher(@NonNull JsonFormFragment jsonFormFragment, @NonNull View editText) {
        this.jsonFormFragment = jsonFormFragment;
        this.editText = editText;
        lookUpFields = new HashMap<>();
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