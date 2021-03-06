package org.smartregister.pnc.sample.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.activity.BasePncRegisterActivity;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.fragment.BasePncRegisterFragment;
import org.smartregister.pnc.model.PncRegisterActivityModel;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.presenter.BasePncRegisterActivityPresenter;
import org.smartregister.pnc.presenter.PncRegisterActivityPresenter;
import org.smartregister.pnc.sample.R;
import org.smartregister.pnc.sample.fragment.PncRegisterFragment;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterActivity extends BasePncRegisterActivity {

    @Override
    protected BasePncRegisterActivityPresenter createPresenter(@NonNull PncRegisterActivityContract.View view, @NonNull PncRegisterActivityContract.Model model) {
        return new PncRegisterActivityPresenter(view, model);
    }

    @Override
    protected PncRegisterActivityContract.Model createActivityModel() {
        return new PncRegisterActivityModel();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PncRegisterFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        //Do nothing
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

        if (requestCode == PncJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(PncJsonFormUtils.ENCOUNTER_TYPE);
                if (PncUtils.metadata() != null && encounterType.equals(PncUtils.metadata().getRegisterEventType())) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(PncJsonFormUtils.formTag(PncUtils.context().allSharedPreferences()));

                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                } else if (encounterType.equals(PncConstants.EventTypeConstants.PNC_MEDIC_INFO) || encounterType.equals(PncConstants.EventTypeConstants.PNC_VISIT)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().savePncForm(encounterType, data);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public void startFormActivity(@NonNull String formName, @Nullable String entityId, @Nullable String metaData) {
        if (mBaseFragment instanceof BasePncRegisterFragment) {
            String locationId = PncUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, null, PncDbConstants.KEY.TABLE);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, PncLibrary.getInstance().getPncConfiguration().getPncMetadata().getPncFormActivity());

        intent.putExtra(PncConstants.JsonFormExtraConstants.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(true);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, PncJsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, BasePncRegisterActivity.class);
        startActivity(intent);
        finish();
    }
/*
    @Override
    public PncRegisterActivityContract.Presenter presenter() {
        return (PncRegisterActivityContract.Presenter) presenter;
    }


    @Override
    public void startRegistration() {
        //Do nothing
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }*/

}
