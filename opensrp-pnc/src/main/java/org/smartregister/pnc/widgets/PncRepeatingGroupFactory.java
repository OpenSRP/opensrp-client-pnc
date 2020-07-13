package org.smartregister.pnc.widgets;

import android.content.Context;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.RepeatingGroupFactory;

import org.json.JSONObject;
import org.smartregister.pnc.R;
import org.smartregister.pnc.utils.PncConstants;

import java.util.List;

public class PncRepeatingGroupFactory extends RepeatingGroupFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        if (views.size() > 0) {
            MaterialEditText referenceEditText = views.get(0).findViewById(R.id.reference_edit_text);
            if (PncConstants.JsonFormKeyConstants.CHILD_STATUS_GROUP.equals(jsonObject.optString("key"))) {
                referenceEditText.clearValidators();
                referenceEditText.setVisibility(View.GONE);
            }
        }
        return views;
    }
}
