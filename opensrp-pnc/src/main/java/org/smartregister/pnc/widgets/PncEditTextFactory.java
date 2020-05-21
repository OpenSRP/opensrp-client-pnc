package org.smartregister.pnc.widgets;

import android.content.Context;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.EditTextFactory;

import org.json.JSONObject;
import org.smartregister.pnc.listener.LookUpTextWatcher;
import org.smartregister.pnc.utils.PncConstants;


public class PncEditTextFactory extends EditTextFactory {

    @Override
    public void attachLayout(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                             MaterialEditText editText, ImageView editable) throws Exception {
        super.attachLayout(stepName, context, formFragment, jsonObject, editText, editable);

        if (jsonObject.has(PncConstants.Key.LOOK_UP) &&
                jsonObject.get(PncConstants.Key.LOOK_UP).toString().equalsIgnoreCase(Boolean.TRUE.toString())) {

            editText.addTextChangedListener(new LookUpTextWatcher(formFragment, editText));
            editText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
        }

    }
}
