package org.smartregister.pnc.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.pojo.PncPartialForm;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.LangUtils;
import org.smartregister.util.Utils;

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
            String eventType = mJSONObject.optString(PncJsonFormUtils.ENCOUNTER_TYPE);
            if (eventType.equals(PncConstants.EventTypeConstants.PNC_MEDIC_INFO) || eventType.equals(PncConstants.EventTypeConstants.PNC_VISIT)) {
                AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog).setTitle(confirmCloseTitle)
                        .setMessage(getString(R.string.save_form_fill_session))
                        .setNegativeButton(R.string.yes, (dialog1, which) -> {
                            saveFormFillSession(eventType);
                            BasePncFormActivity.this.finish();
                        })
                        .setPositiveButton(R.string.no, (dialog12, which) -> Timber.d("No button on dialog in %s", JsonFormActivity.class.getCanonicalName()))
                        .setNeutralButton(getString(R.string.end_session), (dialog13, which) -> {
                            deleteSession(eventType);
                            BasePncFormActivity.this.finish();
                        }).create();


                dialog.show();
            } else {
                super.onBackPressed();
            }

        } else {
            BasePncFormActivity.this.finish();
        }
    }

    private void saveFormFillSession(String eventType) {
        JSONObject jsonObject = getmJSONObject();
        final PncPartialForm pncPartialForm = new PncPartialForm(0, PncUtils.getIntentValue(getIntent(), PncConstants.IntentKey.BASE_ENTITY_ID),
                jsonObject.toString(), eventType, Utils.convertDateFormat(new DateTime()));

        PncLibrary.getInstance().getAppExecutors().diskIO().execute(() -> PncLibrary.getInstance().getPncPartialFormRepository().saveOrUpdate(pncPartialForm));
    }

    private void deleteSession(String eventType) {
        PncLibrary.getInstance().getAppExecutors().diskIO().execute(() -> PncLibrary.getInstance().getPncPartialFormRepository().delete(new PncPartialForm(PncUtils.getIntentValue(getIntent(), PncConstants.IntentKey.BASE_ENTITY_ID), eventType)));
    }

    @NonNull
    public HashMap<String, String> getParcelableData() {
        return parcelableData;
    }

}
