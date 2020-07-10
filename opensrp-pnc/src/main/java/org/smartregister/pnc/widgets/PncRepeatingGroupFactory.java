package org.smartregister.pnc.widgets;

import android.content.Context;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.RepeatingGroupFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.R;
import org.smartregister.pnc.utils.PncConstants;

import java.util.List;

import timber.log.Timber;

public class PncRepeatingGroupFactory extends RepeatingGroupFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        try {
            if (views.size() > 0) {
                MaterialEditText referenceEditText = views.get(0).findViewById(R.id.reference_edit_text);
                View actionView = views.get(0).findViewById(R.id.btn_repeating_group_done);

                if (PncConstants.JsonFormKeyConstants.CHILD_STATUS_GROUP.equals(jsonObject.optString("key")) && jsonObject.has(PncConstants.JsonFormKeyConstants.BABY_COUNT_ALIVE)) {
                    int numberOfBaby = jsonObject.getInt(PncConstants.JsonFormKeyConstants.BABY_COUNT_ALIVE);
                    if (numberOfBaby > 0) {
                        referenceEditText.setText(String.valueOf(numberOfBaby));
                    }
                    trigger(referenceEditText, actionView);
                }
                else if (PncConstants.JsonFormKeyConstants.LIVE_BIRTHS.equals(jsonObject.optString("key")) && jsonObject.has(PncConstants.JsonFormKeyConstants.CHILD_REGISTERED_COUNT)) {
                    //int numberOfChildRegisteredCount = jsonObject.getInt(PncConstants.JsonFormKeyConstants.CHILD_REGISTERED_COUNT);
                    //referenceEditText.setText(String.valueOf(numberOfChildRegisteredCount));
                    //trigger(referenceEditText, actionView);
                }
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
        return views;
    }

    private void trigger(MaterialEditText referenceEditText, View actionView) {
        //addOnDoneAction(referenceEditText);
        referenceEditText.setEnabled(false);
        actionView.setEnabled(false);
    }
}
