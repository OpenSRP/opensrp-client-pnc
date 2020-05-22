package org.smartregister.pnc.presenter;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileActivityContract;
import org.smartregister.pnc.pojo.PncOutcomeForm;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
@RunWith(RobolectricTestRunner.class)
public class MaternityProfileActivityPresenterTest extends BaseTest {

    private PncProfileActivityPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PncProfileActivityContract.View view;
    private PncProfileActivityContract.Interactor interactor;

    @Before
    public void setUp() throws Exception {
        presenter = Mockito.spy(new PncProfileActivityPresenter(view));
        interactor = Mockito.spy((PncProfileActivityContract.Interactor) ReflectionHelpers.getField(presenter, "mProfileInteractor"));

        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);
    }

    @Test
    public void onDestroyWhenNotChangingConfigurationShouldCallInteractorOnDestoryNullifyInteractorIfInteractorIsNotNull() {
        presenter.onDestroy(false);

        Mockito.verify(interactor, Mockito.times(1)).onDestroy(Mockito.eq(false));
        Assert.assertNull(ReflectionHelpers.getField(presenter, "mProfileInteractor"));
    }

    @Test
    public void getProfileViewShouldReturnNullIfTheWeakReferenceObjectIsNull() {
        ReflectionHelpers.setField(presenter, "mProfileView", null);
        Assert.assertNull(presenter.getProfileView());
    }

    @Test
    public void onRegistrationSavedShouldCallViewHideProgressDialog() {
        presenter.onRegistrationSaved(null, false);
        Mockito.verify(view, Mockito.times(1)).hideProgressDialog();
    }

    @Test
    public void onFetchedSavedDiagnosisAndTreatmentFormShouldCallStartFormActivityWithEmptyFormWhenSavedFormIsNull() throws JSONException {
        ArgumentCaptor<JSONObject> formCaptor = ArgumentCaptor.forClass(JSONObject.class);
        JSONObject form = new JSONObject();
        form.put("value", "");
        form.put("question", "What is happening?");

        ReflectionHelpers.setField(presenter, "form", form);
        presenter.onFetchedSavedDiagnosisAndTreatmentForm(null, "caseId", "ec_child");
        Mockito.verify(presenter, Mockito.times(1)).startFormActivity(formCaptor.capture(), Mockito.anyString(), Mockito.nullable(String.class));

        Assert.assertEquals("", formCaptor.getValue().get("value"));
    }

    @Test
    public void onFetchedSavedDiagnosisAndTreatmentFormShouldCallStartFormActivityWithPrefilledFormWhenSavedFormIsNotNull() throws JSONException {
        ArgumentCaptor<JSONObject> formCaptor = ArgumentCaptor.forClass(JSONObject.class);
        JSONObject form = new JSONObject();
        form.put("value", "");
        form.put("question", "What is happening?");

        ReflectionHelpers.setField(presenter, "form", form);

        //Pre-filled form
        JSONObject prefilledForm = new JSONObject();
        prefilledForm.put("value", "I Don't Know");
        prefilledForm.put("question", "What is happening?");

        presenter.onFetchedSavedDiagnosisAndTreatmentForm(
                new PncOutcomeForm(8923, "bei", prefilledForm.toString(), "2019-05-01 11:11:11")
                , "caseId"
                , "ec_child");
        Mockito.verify(presenter, Mockito.times(1)).startFormActivity(formCaptor.capture(), Mockito.anyString(), Mockito.nullable(String.class));
        Assert.assertEquals("I Don't Know", formCaptor.getValue().get("value"));
    }

    @Test
    public void refreshProfileTopSectionShouldCallViewPropertySettersWhenProfileViewIsNotNull() {
        HashMap<String, String> client = new HashMap<>();
        String firstName = "John";
        String lastName = "Doe";
        //String clientDob = "1890-02-02";
        String gender = "Male";
        String registerId = "808920380";
        String clientId = "90239ds-4dfsdf-434rdsf";

        client.put(PncDbConstants.KEY.FIRST_NAME, firstName);
        client.put(PncDbConstants.KEY.LAST_NAME, lastName);
        client.put("gender", gender);
        client.put(PncDbConstants.KEY.REGISTER_ID, registerId);
        client.put(PncDbConstants.KEY.ID, clientId);

        presenter.refreshProfileTopSection(client);

        Mockito.verify(view, Mockito.times(1)).setProfileName(Mockito.eq(firstName + " " + lastName));
        Mockito.verify(view, Mockito.times(1)).setProfileGender(Mockito.eq(gender));
        Mockito.verify(view, Mockito.times(1)).setProfileID(Mockito.eq(registerId));
        Mockito.verify(view, Mockito.times(1)).setProfileImage(Mockito.eq(clientId));
    }

    @Test
    public void startFormShouldCallStartFormActivityWithInjectedClientGenderAndClientEntityTable() {
        String formName = "registration.json";
        String caseId = "90932-dsdf23-2342";
        String entityTable = "ec_client";

        HashMap<String, String> details = new HashMap<>();
        details.put(PncRegistrationDetails.Property.hiv_status_current.name(), "Positive");
        details.put(PncConstants.IntentKey.ENTITY_TABLE, entityTable);

        ArgumentCaptor<HashMap<String, String>> hashMapArgumentCaptor = ArgumentCaptor.forClass(HashMap.class);

        CommonPersonObjectClient client = new CommonPersonObjectClient(caseId, details, "Jane Doe");
        client.setColumnmaps(details);

        Mockito.doNothing().when(presenter).startFormActivity(Mockito.eq(formName), Mockito.eq(caseId), Mockito.eq(entityTable), Mockito.any(HashMap.class));
        presenter.startForm(formName, client);
        Mockito.verify(presenter, Mockito.times(1)).startFormActivity(Mockito.eq(formName), Mockito.eq(caseId), Mockito.eq(entityTable), hashMapArgumentCaptor.capture());

        Assert.assertEquals("Positive", hashMapArgumentCaptor.getValue().get(PncConstants.JsonFormField.MOTHER_HIV_STATUS));
    }

    @Test
    public void startFormActivityShouldCallProfileInteractorAndFetchSavedDiagnosisAndTreatmentForm() {
        String formName = PncConstants.Form.MATERNITY_OUTCOME;
        String caseId = "90932-dsdf23-2342";
        String entityTable = "ec_client";
        String locationId = "location-id";

        HashMap<String, String> injectedValues = new HashMap<>();

        PncLibrary maternityLibrary = Mockito.mock(PncLibrary.class);
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", maternityLibrary);
        Context context = Mockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.doReturn(context).when(maternityLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();

        // Mock call to MaternityUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID)
        Mockito.doReturn(locationId).when(allSharedPreferences).getPreference(Mockito.eq(AllConstants.CURRENT_LOCATION_ID));
        Mockito.doNothing().when(interactor).fetchSavedDiagnosisAndTreatmentForm(Mockito.eq(caseId), Mockito.eq(entityTable));

        presenter.startFormActivity(formName, caseId, entityTable, injectedValues);
        Mockito.verify(interactor, Mockito.times(1)).fetchSavedDiagnosisAndTreatmentForm(Mockito.eq(caseId), Mockito.eq(entityTable));
    }

    @Test
    public void saveVisitOrDiagnosisFormShouldCallMaternityLibraryProcessCheckInFormWhenEventTypeIsCheckIn() throws JSONException {
        String jsonString = "{}";

        PncLibrary maternityLibrary = Mockito.mock(PncLibrary.class);
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", maternityLibrary);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(maternityLibrary).processPncOutcomeForm(Mockito.anyString(), Mockito.eq(jsonString), Mockito.nullable(Intent.class));

        Intent intent = new Intent();
        intent.putExtra(PncConstants.JsonFormExtraConstants.JSON, jsonString);

        presenter.saveOutcomeForm(PncConstants.EventTypeConstants.MATERNITY_OUTCOME, intent);

        Mockito.verify(maternityLibrary, Mockito.times(1)).processPncOutcomeForm(Mockito.eq(
                PncConstants.EventTypeConstants.MATERNITY_OUTCOME)
                , Mockito.eq(jsonString)
                , Mockito.any(Intent.class));
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }
}