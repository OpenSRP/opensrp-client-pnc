package org.smartregister.pnc.sample.fragment;


import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.smartregister.pnc.adapter.ClientLookUpListAdapter;
import org.smartregister.pnc.fragment.BasePncFormFragment;


public class PncFormFragment extends BasePncFormFragment implements ClientLookUpListAdapter.ClickListener {

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    public static PncFormFragment getFormFragment(String stepName) {
        PncFormFragment jsonFormFragment = new PncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

}
