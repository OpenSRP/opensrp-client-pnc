package org.smartregister.pnc.presenter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.pnc.R;
import org.smartregister.pnc.contract.PncProfileActivityContract;
import org.smartregister.pnc.interactor.PncProfileInteractor;
import org.smartregister.pnc.listener.OngoingTaskCompleteListener;
import org.smartregister.pnc.listener.PncEventActionCallBack;
import org.smartregister.pnc.model.PncProfileActivityModel;
import org.smartregister.pnc.pojo.OngoingTask;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.pojo.PncOutcomeForm;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.tasks.FetchRegistrationDataTask;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileActivityPresenter implements PncProfileActivityContract.Presenter, PncProfileActivityContract.InteractorCallBack, PncEventActionCallBack {

    private WeakReference<PncProfileActivityContract.View> mProfileView;
    private PncProfileActivityContract.Interactor mProfileInteractor;

    private PncProfileActivityModel model;
    private JSONObject form = null;

    private OngoingTask ongoingTask = null;
    private ArrayList<OngoingTaskCompleteListener> ongoingTaskCompleteListeners = new ArrayList<>();

    public PncProfileActivityPresenter(PncProfileActivityContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new PncProfileInteractor();
        model = new PncProfileActivityModel();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Nullable
    @Override
    public PncProfileActivityContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        }

        return null;
    }

    @Override
    public void onRegistrationSaved(@Nullable CommonPersonObjectClient client, boolean isEdit) {
        CommonPersonObjectClient reassignableClient = client;
        if (getProfileView() != null) {
            getProfileView().hideProgressDialog();

            if (reassignableClient != null) {
                getProfileView().setClient(reassignableClient);
            } else {
                reassignableClient = getProfileView().getClient();
            }

            if (isEdit && reassignableClient != null) {
                refreshProfileTopSection(reassignableClient.getColumnmaps());
            }
        }
    }

    @Override
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable PncOutcomeForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable) {
        try {
            if (diagnosisAndTreatmentForm != null) {
                form = new JSONObject(diagnosisAndTreatmentForm.getForm());
            }

            startFormActivity(form, caseId, entityTable);
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void startFormActivity(@Nullable JSONObject form, @NonNull String caseId, @NonNull String entityTable) {
        if (getProfileView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(PncConstants.IntentKey.BASE_ENTITY_ID, caseId);
            intentKeys.put(PncConstants.IntentKey.ENTITY_TABLE, entityTable);
            getProfileView().startFormActivity(form, intentKeys);
        }
    }

    @Override
    public void refreshProfileTopSection(@NonNull Map<String, String> client) {
        PncProfileActivityContract.View profileView = getProfileView();
        if (profileView != null) {
            profileView.setProfileName(client.get(PncDbConstants.KEY.FIRST_NAME) + " " + client.get(PncDbConstants.KEY.LAST_NAME));
            String translatedYearInitial = profileView.getString(R.string.abbrv_years);
            String dobString = client.get(PncConstants.KeyConstants.DOB);

            if (dobString != null) {
                String clientAge = PncUtils.getClientAge(Utils.getDuration(dobString), translatedYearInitial);
                profileView.setProfileAge(clientAge);
            }

            profileView.setProfileID(Utils.getValue(client, PncDbConstants.KEY.REGISTER_ID, false));
            profileView.setProfileImage(Utils.getValue(client, PncDbConstants.KEY.ID, false));

            profileView.setDeliveryDays("Day P" + PncUtils.getDeliveryDays(client.get("_id")));
        }
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientMap = commonPersonObjectClient.getColumnmaps();
        HashMap<String, String> injectedValues = new HashMap<>();

        injectedValues.put(PncConstants.JsonFormField.MOTHER_HIV_STATUS, clientMap.get(PncRegistrationDetails.Property.hiv_status_current.name()));
        String entityTable = clientMap.get(PncConstants.IntentKey.ENTITY_TABLE);

        startFormActivity(formName, commonPersonObjectClient.getCaseId(), entityTable, injectedValues);
    }

    public void startFormActivity(@NonNull String formName, @NonNull String caseId, @NonNull String entityTable, @Nullable HashMap<String, String> injectedValues) {
        if (mProfileView != null) {
            form = null;
            try {
                String locationId = PncUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                form = model.getFormAsJson(formName, caseId, locationId, injectedValues);

                // Fetch saved form & continue editing
                if (formName.equals(PncConstants.Form.PNC_VISIT)) {
                    mProfileInteractor.fetchSavedDiagnosisAndTreatmentForm(caseId, entityTable);
                } else {
                    startFormActivity(form, caseId, entityTable);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveUpdateRegistrationForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        try {
            if (registerParams.getFormTag() == null) {
                registerParams.setFormTag(PncJsonFormUtils.formTag(PncUtils.getAllSharedPreferences()));
            }

            PncEventClient pncEventClient = processRegistration(jsonString, registerParams.getFormTag());
            if (pncEventClient == null) {
                return;
            }

            mProfileInteractor.saveRegistration(pncEventClient, jsonString, registerParams, this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Nullable
    @Override
    public PncEventClient processRegistration(@NonNull String jsonString, @NonNull FormTag formTag) {
        PncEventClient pncEventClient = PncJsonFormUtils.processPncRegistrationForm(jsonString, formTag);
        //TODO: Show the user this error toast
        //showErrorToast();

        if (pncEventClient == null) {
            return null;
        }

        return pncEventClient;
    }

    @Override
    public void onPncEventSaved() {
        PncProfileActivityContract.View view = getProfileView();
        if (view != null) {
            view.hideProgressDialog();

            if(getOngoingTask() != null) {
                view.showMessage(view.getString(R.string.pnc_client_close_message));
                view.closeView();

                removeOngoingTask(ongoingTask);
            }

        }
    }

    @Override
    public void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId) {
        if (getProfileView() != null) {
            Utils.startAsyncTask(new FetchRegistrationDataTask(new WeakReference<Context>(getProfileView().getContext()), new FetchRegistrationDataTask.OnTaskComplete() {
                @Override
                public void onSuccess(@Nullable String jsonForm) {
                    PncMetadata metadata = PncUtils.metadata();

                    PncProfileActivityContract.View profileView = getProfileView();
                    if (profileView != null && metadata != null && jsonForm != null) {
                        Context context = profileView.getContext();
                        Intent intent = new Intent(context, metadata.getPncFormActivity());
                        Form formParam = new Form();
                        formParam.setWizard(false);
                        formParam.setHideSaveLabel(true);
                        formParam.setNextLabel("");
                        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
                        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm);
                        profileView.startActivityForResult(intent, PncJsonFormUtils.REQUEST_CODE_GET_JSON);
                    }
                }
            }), new String[]{baseEntityId});
        }
    }

    @Override
    public boolean hasOngoingTask() {
        return ongoingTask != null;
    }

    @Override
    public OngoingTask getOngoingTask() {
        return ongoingTask;
    }

    @Override
    public boolean setOngoingTask(@NonNull OngoingTask ongoingTask) {
        if (this.ongoingTask == null) {
            this.ongoingTask = ongoingTask;
            return true;
        }

        return false;
    }

    @Override
    public boolean removeOngoingTask(@NonNull OngoingTask ongoingTask) {
        if (this.ongoingTask == ongoingTask) {
            for (OngoingTaskCompleteListener ongoingTaskCompleteListener: ongoingTaskCompleteListeners) {
                ongoingTaskCompleteListener.onTaskComplete(ongoingTask);
            }

            this.ongoingTask = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean addOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener) {
        return ongoingTaskCompleteListeners.add(ongoingTaskCompleteListener);
    }

    @Override
    public boolean removeOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener) {
        return ongoingTaskCompleteListeners.remove(ongoingTaskCompleteListener);
    }
}