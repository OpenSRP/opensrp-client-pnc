package org.smartregister.pnc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.dao.PncOutcomeFormDao;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.pojo.PncOutcomeForm;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.AppExecutors;
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
        enableOnCloseDialog = getIntent().getBooleanExtra(PncConstants.FormActivity.ENABLE_ON_CLOSE_DIALOG, true);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            Set<String> keySet = extras.keySet();

            for (String key : keySet) {
                if (!key.equals(PncConstants.JsonFormExtra.JSON)) {
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
        BasePncFormFragment maternityFormFragment = (BasePncFormFragment) BasePncFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, maternityFormFragment).commit();
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
            if (mJSONObject.optString(PncJsonFormUtils.ENCOUNTER_TYPE).equals(PncConstants.EventType.MATERNITY_OUTCOME)) {
                AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog).setTitle(confirmCloseTitle)
                        .setMessage(getString(R.string.save_form_fill_session))
                        .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveFormFillSession();
                                BasePncFormActivity.this.finish();
                            }
                        }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Timber.d("No button on dialog in %s", JsonFormActivity.class.getCanonicalName());
                            }
                        }).setNeutralButton(getString(R.string.end_session), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSession();
                                BasePncFormActivity.this.finish();
                            }
                        }).create();

                dialog.show();
            } else {
                super.onBackPressed();
            }

        } else {
            BasePncFormActivity.this.finish();
        }
    }

    private void saveFormFillSession() {
        JSONObject jsonObject = getmJSONObject();
        final PncOutcomeForm maternityOutcomeForm = new PncOutcomeForm(0, PncUtils.getIntentValue(getIntent(), PncConstants.IntentKey.BASE_ENTITY_ID),
                jsonObject.toString(), Utils.convertDateFormat(new DateTime()));
        final PncOutcomeFormDao maternityOutcomeFormDao = PncLibrary.getInstance().getPncOutcomeFormRepository();
        new AppExecutors().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                maternityOutcomeFormDao.saveOrUpdate(maternityOutcomeForm);
            }
        });
    }

    private void deleteSession() {
        JSONObject jsonObject = getmJSONObject();
        final PncOutcomeForm maternityOutcomeForm = new PncOutcomeForm(0, PncUtils.getIntentValue(getIntent(), PncConstants.IntentKey.BASE_ENTITY_ID),
                jsonObject.toString(), Utils.convertDateFormat(new DateTime()));
        final PncOutcomeFormDao maternityOutcomeFormDao = PncLibrary.getInstance().getPncOutcomeFormRepository();
        new AppExecutors().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                maternityOutcomeFormDao.delete(maternityOutcomeForm);
            }
        });
    }

    @NonNull
    public HashMap<String, String> getParcelableData() {
        return parcelableData;
    }
}
