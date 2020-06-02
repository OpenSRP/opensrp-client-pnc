package org.smartregister.pnc.sample.activity;


import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.sample.R;
import org.smartregister.pnc.sample.fragment.PncFormFragment;


public class PncFormActivity extends BasePncFormActivity {

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        PncFormFragment pncFormFragment = PncFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(R.id.container, pncFormFragment).commit();
    }
}
