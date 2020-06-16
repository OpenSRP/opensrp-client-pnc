package org.smartregister.pnc.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.fragment.BasePncRegisterFragment;
import org.smartregister.pnc.model.PncRegisterActivityModel;
import org.smartregister.pnc.presenter.BasePncRegisterActivityPresenter;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public abstract class BasePncRegisterActivity extends BaseRegisterActivity implements PncRegisterActivityContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerBottomNavigation() {
        try {
            View bottomNavGeneralView = findViewById(org.smartregister.R.id.bottom_navigation);
            if (bottomNavGeneralView instanceof BottomNavigationView) {
                BottomNavigationView bottomNavigationView = (BottomNavigationView) bottomNavGeneralView;
                if (!PncLibrary.getInstance().getPncConfiguration().isBottomNavigationEnabled()) {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        } catch (NoSuchFieldError e) {
            // This error occurs because the ID cannot be found on some client applications because the layout
            // has been overriden
            Timber.e(e);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = createPresenter(this, createActivityModel());
    }

    abstract protected BasePncRegisterActivityPresenter createPresenter(@NonNull PncRegisterActivityContract.View view, @NonNull PncRegisterActivityContract.Model model);

    protected PncRegisterActivityContract.Model createActivityModel() {
        return new PncRegisterActivityModel();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, BasePncRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public PncRegisterActivityContract.Presenter presenter() {
        return (PncRegisterActivityContract.Presenter) presenter;
    }

    @Override
    public void startRegistration() {
        //do nothing
    }

    @Override
    public void startFormActivityFromFormName(@NonNull String formName, @Nullable String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (mBaseFragment instanceof BasePncRegisterFragment) {
            String locationId = PncUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, injectedFieldValues, entityTable);
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivityFromFormJson(@NonNull String entityId, @NonNull JSONObject jsonForm, @Nullable HashMap<String, String> intentData) {
        addGlobals(entityId, jsonForm);
        Intent intent = PncUtils.buildFormActivityIntent(jsonForm, intentData, this);
        if (intent != null) {
            startActivityForResult(intent, PncJsonFormUtils.REQUEST_CODE_GET_JSON);
        } else {
            Timber.e(new Exception(), "FormActivityConstants cannot be started because PncMetadata is NULL");
        }
    }

    private void addGlobals(String entityId, JSONObject form) {

        Map<String, String> detailMap = CoreLibrary.getInstance().context().detailsRepository().getAllDetailsForClient(entityId);

        try {
            JSONObject defaultGlobal = new JSONObject();

            for (Map.Entry<String, String> entry: detailMap.entrySet()) {
                defaultGlobal.put(entry.getKey(), entry.getValue());
            }

            LocalDate todayDate = LocalDate.now();
            if (detailMap.containsKey(PncConstants.FormGlobalConstants.DELIVERY_DATE)) {
                LocalDate deliveryDate = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE), DateTimeFormat.forPattern("dd-MM-yyyy"));
                int numberOfDays = Days.daysBetween(deliveryDate, todayDate).getDays();
                defaultGlobal.put(PncConstants.FormGlobalConstants.PNC_VISIT_PERIOD, numberOfDays);
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.BABY_DOB)) {
                LocalDate babyDob = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.BABY_DOB), DateTimeFormat.forPattern("dd-MM-yyyy"));
                int numberOfYears = new Period(babyDob, todayDate).getYears();
                defaultGlobal.put(PncConstants.FormGlobalConstants.BABY_AGE, numberOfYears);
            }

            form.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, defaultGlobal);
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }
}