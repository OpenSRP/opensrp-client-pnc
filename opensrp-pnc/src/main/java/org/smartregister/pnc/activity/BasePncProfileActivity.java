package org.smartregister.pnc.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.adapter.ViewPagerAdapter;
import org.smartregister.pnc.config.PncRegisterSwitcher;
import org.smartregister.pnc.contract.PncProfileActivityContract;
import org.smartregister.pnc.fragment.PncProfileOverviewFragment;
import org.smartregister.pnc.fragment.PncProfileVisitsFragment;
import org.smartregister.pnc.listener.OnSendActionToFragment;
import org.smartregister.pnc.listener.OngoingTaskCompleteListener;
import org.smartregister.pnc.pojo.OngoingTask;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.presenter.PncProfileActivityPresenter;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BasePncProfileActivity extends BaseProfileActivity implements PncProfileActivityContract.View {

    private TextView nameView;
    private TextView ageView;
    private TextView genderView;
    private TextView ancIdView;
    private ImageView imageView;
    private String baseEntityId;
    private OnSendActionToFragment sendActionListenerForProfileOverview;
    private OnSendActionToFragment sendActionListenerToVisitsFragment;

    private CommonPersonObjectClient commonPersonObjectClient;
    private Button switchRegBtn;

    @Override
    protected void initializePresenter() {
        presenter = new PncProfileActivityPresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        ageView = findViewById(R.id.textview_detail_two);
        genderView = findViewById(R.id.textview_detail_three);
        ancIdView = findViewById(R.id.textview_detail_one);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);
        switchRegBtn = findViewById(R.id.btn_pncActivityBaseProfile_switchRegView);

        setTitle("");
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        PncProfileOverviewFragment profileOverviewFragment = PncProfileOverviewFragment.newInstance(this.getIntent().getExtras());
        setSendActionListenerForProfileOverview(profileOverviewFragment);

        PncProfileVisitsFragment profileVisitsFragment = PncProfileVisitsFragment.newInstance(this.getIntent().getExtras());
        setSendActionListenerToVisitsFragment(profileVisitsFragment);

        adapter.addFragment(profileOverviewFragment, this.getString(R.string.overview));
        adapter.addFragment(profileVisitsFragment, this.getString(R.string.pnc_visits));

        viewPager.setAdapter(adapter);
        return viewPager;
    }

    public void setSendActionListenerForProfileOverview(OnSendActionToFragment sendActionListenerForProfileOverview) {
        this.sendActionListenerForProfileOverview = sendActionListenerForProfileOverview;
    }

    public void setSendActionListenerToVisitsFragment(OnSendActionToFragment sendActionListenerToVisitsFragment) {
        this.sendActionListenerToVisitsFragment = sendActionListenerToVisitsFragment;
    }

    @Override
    protected void fetchProfileData() {
        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                .getSerializableExtra(PncConstants.IntentKey.CLIENT_OBJECT);
        ((PncProfileActivityPresenter) presenter).refreshProfileTopSection(commonPersonObjectClient.getColumnmaps(), baseEntityId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.pnc_menu_item_close_client) {
            openPncCloseForm();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        PncProfileActivityContract.Presenter pncProfilePresenter = (PncProfileActivityPresenter) presenter;

        if (!pncProfilePresenter.hasOngoingTask()) {
            commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                    .getSerializableExtra(PncConstants.IntentKey.CLIENT_OBJECT);
            baseEntityId = commonPersonObjectClient.getCaseId();
            pncProfilePresenter.refreshProfileTopSection(commonPersonObjectClient.getColumnmaps(), baseEntityId);

            // Enable switcher
            configureRegisterSwitcher();

        } else {
            pncProfilePresenter.addOngoingTaskCompleteListener(new OngoingTaskCompleteListener() {
                @Override
                public void onTaskComplete(@NonNull OngoingTask ongoingTask) {
                    pncProfilePresenter.removeOngoingTaskCompleteListener(this);
                    onResumption();
                }
            });
        }
    }

    private void configureRegisterSwitcher() {
        Class<? extends PncRegisterSwitcher> pncRegisterSwitcherClass = PncLibrary.getInstance().getPncConfiguration().getPncRegisterSwitcher();
        if (pncRegisterSwitcherClass != null) {
            final PncRegisterSwitcher pncRegisterSwitcher = ConfigurationInstancesHelper.newInstance(pncRegisterSwitcherClass);

            switchRegBtn.setVisibility(pncRegisterSwitcher.showRegisterSwitcher(commonPersonObjectClient) ? View.VISIBLE : View.GONE);
            switchRegBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pncRegisterSwitcher.switchFromPncRegister(commonPersonObjectClient, BasePncProfileActivity.this);
                }
            });
        } else {
            switchRegBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());

    }

    @Override
    public void setProfileName(@NonNull String fullName) {
        this.patientName = fullName;
        nameView.setText(fullName);
    }

    @Override
    public void setProfileID(@NonNull String registerId) {
        ancIdView.setText(String.format(getString(R.string.id_detail), registerId));
    }

    @Override
    public void setProfileAge(@NonNull String age) {
        ageView.setText(String.format(getString(R.string.age_details), age));

    }

    @Override
    public void setProfileGender(@NonNull String deliveryDays) {
        genderView.setText(deliveryDays);
    }

    @Override
    public void setProfileImage(@NonNull String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, R.drawable.gender_insensitive_avatar);
    }

    public OnSendActionToFragment getActionListenerForVisitFragment() {
        return sendActionListenerToVisitsFragment;
    }

    public OnSendActionToFragment getActionListenerForProfileOverview() {
        return sendActionListenerForProfileOverview;
    }

    @Override
    public void openPncVisitForm() {
        if (commonPersonObjectClient != null) {
            ((PncProfileActivityPresenter) presenter).startForm(PncConstants.Form.PNC_VISIT, commonPersonObjectClient);
        }
    }

    @Override
    public void openPncMedicInfoForm() {
        if (commonPersonObjectClient != null) {
            ((PncProfileActivityPresenter) presenter).startForm(PncConstants.Form.PNC_MEDIC_INFORMATION, commonPersonObjectClient);
        }
    }

    @Override
    public void openPncCloseForm() {
        if (commonPersonObjectClient != null) {
            // FIXME paternity_close.json file not found
            ((PncProfileActivityPresenter) presenter).startForm(PncConstants.Form.PNC_CLOSE, commonPersonObjectClient);
        }
    }

    @Override
    public void startFormActivity(String entityId, @NonNull JSONObject form, @NonNull HashMap<String, String> intentData) {
        PncUtils.processPreChecks(entityId, form, intentData);
        Intent intent = PncUtils.buildFormActivityIntent(form, intentData, this);
        startActivityForResult(intent, PncJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pnc_profile_activity, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PncJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            /*try {
                String jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);
                Timber.d("JSON-Result : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(PncJsonFormUtils.ENCOUNTER_TYPE);

                OngoingTask ongoingTask = new OngoingTask();
                ongoingTask.setTaskType(OngoingTask.TaskType.PROCESS_FORM);
                ongoingTask.setTaskDetail(encounterType);

                addOngoingTask(ongoingTask);


            } catch (JSONException e) {
                Timber.e(e);
            }*/
            onActivityResultExtended(requestCode, resultCode, data);
        }
    }

    private void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

        try {
            String jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);
            data.putExtra(PncConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);

            Timber.d("JSONResult : %s", jsonString);

            JSONObject form = new JSONObject(jsonString);
            String encounterType = form.getString(PncJsonFormUtils.ENCOUNTER_TYPE);
           if (encounterType.equals(PncConstants.EventTypeConstants.PNC_VISIT)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    ((PncProfileActivityPresenter) this.presenter).savePncForm(encounterType, data);
                } else if (encounterType.equals(PncConstants.EventTypeConstants.PNC_OUTCOME)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    ((PncProfileActivityPresenter) this.presenter).savePncForm(encounterType, data);
                } else if (encounterType.equals(PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION)) {
                    showProgressDialog(R.string.saving_dialog_title);
                RegisterParams registerParam = new RegisterParams();
                registerParam.setEditMode(true);
                registerParam.setFormTag(PncJsonFormUtils.formTag(PncUtils.context().allSharedPreferences()));

                showProgressDialog(R.string.saving_dialog_title);
                ((PncProfileActivityPresenter) this.presenter).saveUpdateRegistrationForm(jsonString, registerParam);
            }
            else if (encounterType.equals(PncConstants.EventTypeConstants.PNC_CLOSE)) {
                showProgressDialog(R.string.saving_dialog_title);
                ((PncProfileActivityPresenter) this.presenter).savePncForm(encounterType, data);
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void addOngoingTask(@NonNull OngoingTask ongoingTask) {
        ((PncProfileActivityContract.Presenter) this.presenter).setOngoingTask(ongoingTask);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.pnc_activity_base_profile);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_profile_registration_info) {
            if (presenter instanceof PncProfileActivityContract.Presenter) {
                ((PncProfileActivityContract.Presenter) presenter).onUpdateRegistrationBtnCLicked(baseEntityId);
            }
        } else {
            super.onClick(view);
        }
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }

    @Nullable
    @Override
    public CommonPersonObjectClient getClient() {
        return commonPersonObjectClient;
    }

    @Override
    public void setClient(@NonNull CommonPersonObjectClient client) {
        this.commonPersonObjectClient = client;
    }

    @Override
    public void showMessage(@Nullable String text) {
        if (text != null) {
            Toast.makeText(this, text, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void closeView() {
        finish();
    }
}