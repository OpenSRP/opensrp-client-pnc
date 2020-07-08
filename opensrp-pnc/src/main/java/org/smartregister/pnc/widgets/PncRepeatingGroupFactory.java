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
            if (jsonObject.optString("key").equals("child_status") && jsonObject.has(PncConstants.JsonFormKeyConstants.BABY_COUNT_ALIVE) && views.size() > 0) {
                int numberOfBaby = jsonObject.getInt(PncConstants.JsonFormKeyConstants.BABY_COUNT_ALIVE);
                MaterialEditText referenceEditText = views.get(0).findViewById(R.id.reference_edit_text);
                if (numberOfBaby > 0) {
                    formFragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            referenceEditText.setText(String.valueOf(numberOfBaby));
                            addOnDoneAction(referenceEditText);
                        }
                    });
                }
                referenceEditText.setEnabled(false);
                views.get(0).findViewById(R.id.btn_repeating_group_done).setEnabled(false);
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
        return views;
    }
}
