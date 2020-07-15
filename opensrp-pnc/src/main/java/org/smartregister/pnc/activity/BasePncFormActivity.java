package org.smartregister.pnc.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.R;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.util.LangUtils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class BasePncFormActivity extends JsonWizardFormActivity {

    private boolean enableOnCloseDialog = true;
    private HashMap<String, String> parcelableData = new HashMap<>();

    @Override
    protected void attachBaseContext(android.content.Context base) {
        String language = LangUtils.getLanguage(base);
        super.attachBaseContext(LangUtils.setAppLocale(base, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableOnCloseDialog = getIntent().getBooleanExtra(PncConstants.FormActivityConstants.ENABLE_ON_CLOSE_DIALOG, true);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            Set<String> keySet = extras.keySet();

            for (String key : keySet) {
                if (!key.equals(PncConstants.JsonFormExtraConstants.JSON)) {
                    Object objectValue = extras.get(key);

                    if (objectValue instanceof String) {
                        String value = (String) objectValue;
                        parcelableData.put(key, value);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            String encounterType = mJSONObject.getString(PncJsonFormUtils.ENCOUNTER_TYPE);
            confirmCloseTitle = getString(R.string.confirm_form_close);
            confirmCloseMessage = encounterType.trim().toLowerCase().contains("update") ? this.getString(R.string.any_changes_you_make) : this.getString(R.string.confirm_form_close_explanation);
            setConfirmCloseTitle(confirmCloseTitle);
            setConfirmCloseMessage(confirmCloseMessage);

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        BasePncFormFragment pncFormFragment = (BasePncFormFragment) BasePncFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, pncFormFragment).commit();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
        }
        super.setSupportActionBar(toolbar);
    }

    /**
     * Conditionaly display the confirmation dialog
     */
    @Override
    public void onBackPressed() {
        if (enableOnCloseDialog) {
            super.onBackPressed();
        } else {
            BasePncFormActivity.this.finish();
        }
    }

    @NonNull
    public HashMap<String, String> getParcelableData() {
        return parcelableData;
    }

    @Override
    protected void toggleViewVisibility(View view, boolean visible, boolean popup) {
        try {
            String addressString = (String) view.getTag(R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address, popup);
            JSONArray values = null;
            if (PncDbConstants.Column.PncVisit.OTHER_VISIT_DATE.equals(object.get("key"))) {
                values = object.getJSONArray("value");
                super.toggleViewVisibility(view, visible, popup);
                object.put("value", values);
            } else {
                super.toggleViewVisibility(view, visible, popup);
            }
        } catch (JSONException ex) {
            super.toggleViewVisibility(view, visible, popup);
            Timber.e(ex);
        }
    }
}
