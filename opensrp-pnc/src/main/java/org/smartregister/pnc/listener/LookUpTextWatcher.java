package org.smartregister.pnc.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.event.Listener;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.utils.PncLookUpUtils;

import java.util.HashMap;
import java.util.List;
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
        String text = (String) editText.getTag(com.vijay.jsonwizard.R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        String key = (String) editText.getTag(com.vijay.jsonwizard.R.id.key);

        boolean afterLookUp = (Boolean) editText.getTag(com.vijay.jsonwizard.R.id.after_look_up);
        if (afterLookUp) {
            editText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
            return;
        }

        if (lookUpFields.containsKey(key) && text.trim().isEmpty()) {
            lookUpFields.remove(key);
            return;
        }

        lookUpFields.put(key, text);

        if (jsonFormFragment instanceof BasePncFormFragment) {
            BasePncFormFragment maternityFormFragment = (BasePncFormFragment) jsonFormFragment;
            Listener<List<CommonPersonObject>> listener = maternityFormFragment.lookUpListener();

            PncLookUpUtils.lookUp(PncLibrary.getInstance().context(), lookUpFields, listener);
        }

    }

}