package org.smartregister.pnc.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.BuildConfig;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.provider.PncRegisterQueryProviderTest;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@PrepareForTest(PncUtils.class)
@RunWith(PowerMockRunner.class)
public class PncJsonFormUtilsTest {

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private PncLibrary pncLibrary;

    private PncMetadata pncMetadata;

    @Mock
    private PncConfiguration pncConfiguration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pncMetadata = new PncMetadata(PncConstants.Form.PNC_REGISTRATION
                , PncDbConstants.KEY.TABLE
                , PncConstants.EventTypeConstants.PNC_REGISTRATION
                , PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION
                , PncConstants.CONFIG
                , Class.class
                , Class.class
                , true);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndEntityIdBlank() throws Exception {
        PncConfiguration pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProviderTest.class)
                .setPncMetadata(pncMetadata)
                .build();

        PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), pncConfiguration,
                BuildConfig.VERSION_CODE, 1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("metadata", new JSONObject());
        JSONObject result = PncJsonFormUtils.getFormAsJson(jsonObject, PncConstants.Form.PNC_REGISTRATION, "", "");
        Assert.assertNull(result);
    }

    @Test
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndEntityIdNonEmpty() throws Exception {
        PncConfiguration pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProviderTest.class)
                .setPncMetadata(pncMetadata)
                .build();

        PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), pncConfiguration,
                BuildConfig.VERSION_CODE, 1);

        JSONObject jsonArrayFieldsJsonObject = new JSONObject();
        jsonArrayFieldsJsonObject.put(PncJsonFormUtils.KEY, PncJsonFormUtils.OPENSRP_ID);

        JSONArray jsonArrayFields = new JSONArray();
        jsonArrayFields.put(jsonArrayFieldsJsonObject);

        JSONObject jsonObjectForFields = new JSONObject();
        jsonObjectForFields.put(PncJsonFormUtils.FIELDS, jsonArrayFields);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("metadata", new JSONObject());
        jsonObject.put(PncJsonFormUtils.STEP1, jsonObjectForFields);

        JSONObject result = PncJsonFormUtils.getFormAsJson(jsonObject, PncConstants.Form.PNC_REGISTRATION, "23", "currentLocation");
        Assert.assertEquals(result, jsonObject);
    }

    @Test
    public void testUpdateLocationStringShouldPopulateTreeAndDefaultAttributeUsingLocationHierarchyTree() throws Exception {
        pncMetadata.setFieldsWithLocationHierarchy(new HashSet<>(Arrays.asList("village")));
        Mockito.when(pncConfiguration.getPncMetadata()).thenReturn(pncMetadata);
        Mockito.when(pncLibrary.getPncConfiguration()).thenReturn(pncConfiguration);
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "village");
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TREE);
        jsonArray.put(jsonObject);
        String hierarchyString = "[\"Kenya\",\"Central\"]";
        String entireTreeString = "[{\"nodes\":[{\"level\":\"Province\",\"name\":\"Central\",\"key\":\"1\"}],\"level\":\"Country\",\"name\":\"Kenya\",\"key\":\"0\"}]";
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");

        List<FormLocation> entireTree = new ArrayList<>();
        FormLocation formLocationCountry = new FormLocation();
        formLocationCountry.level = "Country";
        formLocationCountry.name = "Kenya";
        formLocationCountry.key = "0";
        FormLocation formLocationProvince = new FormLocation();
        formLocationProvince.level = "Province";
        formLocationProvince.name = "Central";
        formLocationProvince.key = "1";

        List<FormLocation> entireTreeCountryNode = new ArrayList<>();
        entireTreeCountryNode.add(formLocationProvince);
        formLocationCountry.nodes = entireTreeCountryNode;
        entireTree.add(formLocationCountry);

        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(healthFacilities));

        WhiteboxImpl.invokeMethod(PncJsonFormUtils.class, "updateLocationTree", jsonArray, hierarchyString, entireTreeString, entireTreeString);
        Assert.assertTrue(jsonObject.has(JsonFormConstants.TREE));
        Assert.assertTrue(jsonObject.has(JsonFormConstants.DEFAULT));
        //Assert.assertEquals(hierarchyString, jsonObject.optString(JsonFormConstants.DEFAULT));
        JSONArray resultTreeObject = new JSONArray(jsonObject.optString(JsonFormConstants.TREE));
        Assert.assertTrue(resultTreeObject.optJSONObject(0).has("nodes"));
        Assert.assertEquals("Kenya", resultTreeObject.optJSONObject(0).optString("name"));
        Assert.assertEquals("Country", resultTreeObject.optJSONObject(0).optString("level"));
        Assert.assertEquals("0", resultTreeObject.optJSONObject(0).optString("key"));
        Assert.assertEquals("Central", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("name"));
        Assert.assertEquals("1", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("key"));
        Assert.assertEquals("Province", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("level"));
    }

    @Test
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndInjectableFields() throws Exception {
        PncMetadata pncMetadata = new PncMetadata(PncConstants.Form.PNC_REGISTRATION
                , PncDbConstants.KEY.TABLE
                , PncConstants.EventTypeConstants.PNC_REGISTRATION
                , PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION
                , PncConstants.CONFIG
                , Class.class
                , Class.class
                , true);

        PncConfiguration pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProviderTest.class)
                .setPncMetadata(pncMetadata)
                .build();

        PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), pncConfiguration,
                BuildConfig.VERSION_CODE, 1);

        JSONObject jsonArrayFieldsJsonObject = new JSONObject();
        jsonArrayFieldsJsonObject.put(PncJsonFormUtils.KEY, PncJsonFormUtils.OPENSRP_ID);

        JSONObject injectableField = new JSONObject();
        injectableField.put(PncJsonFormUtils.KEY, "Injectable");

        JSONArray jsonArrayFields = new JSONArray();
        jsonArrayFields.put(jsonArrayFieldsJsonObject);
        jsonArrayFields.put(injectableField);

        JSONObject jsonObjectForFields = new JSONObject();
        jsonObjectForFields.put(PncJsonFormUtils.FIELDS, jsonArrayFields);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("metadata", new JSONObject());
        jsonObject.put(PncJsonFormUtils.STEP1, jsonObjectForFields);

        HashMap<String, String> injectableFields = new HashMap<>();
        injectableFields.put("Injectable", "Injectable value");
        JSONObject result = PncJsonFormUtils.getFormAsJson(jsonObject, PncConstants.Form.PNC_REGISTRATION, "23", "currentLocation", injectableFields);
        Assert.assertEquals(result, jsonObject);
        Assert.assertEquals("Injectable value", injectableField.getString(PncJsonFormUtils.VALUE));
    }

    @Test
    public void testTagSyncMetadataWithEmptyEvent() throws Exception {
        PncMetadata pncMetadata = new PncMetadata(PncConstants.Form.PNC_REGISTRATION
                , PncDbConstants.KEY.TABLE
                , PncConstants.EventTypeConstants.PNC_REGISTRATION
                , PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION
                , PncConstants.CONFIG
                , Class.class
                , Class.class
                , true);
        PncConfiguration pncConfiguration = new PncConfiguration
                .Builder(null)
                .setPncMetadata(pncMetadata)
                .build();
        PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), pncConfiguration,
                BuildConfig.VERSION_CODE, 1);
        CoreLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(SyncConfiguration.class));

        PowerMockito.when(PncUtils.class, "getAllSharedPreferences").thenReturn(PowerMockito.mock(AllSharedPreferences.class));

        Event event = PncJsonFormUtils.tagSyncMetadata(new Event());
        Assert.assertNotNull(event);
    }

    @Test
    public void testGetLocationIdWithCurrentLocalityIsNotNull() throws Exception {
        PncMetadata pncMetadata = new PncMetadata(PncConstants.Form.PNC_REGISTRATION
                , PncDbConstants.KEY.TABLE
                , PncConstants.EventTypeConstants.PNC_REGISTRATION
                , PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION
                , PncConstants.CONFIG
                , Class.class
                , Class.class
                , true);
        pncMetadata.setHealthFacilityLevels(new ArrayList<String>());
        PncConfiguration pncConfiguration = new PncConfiguration
                .Builder(PncRegisterQueryProviderTest.class)
                .setPncMetadata(pncMetadata)
                .build();
        PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), pncConfiguration,
                BuildConfig.VERSION_CODE, 1);
        CoreLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(SyncConfiguration.class));

        ArrayList<String> defaultLocations = new ArrayList<>();
        defaultLocations.add("Country");
        LocationHelper.init(defaultLocations,
                "Country");
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("Place");
        Assert.assertNotNull(LocationHelper.getInstance());
        String result = PncJsonFormUtils.getLocationId("Country", allSharedPreferences);
        Assert.assertEquals("Place", result);
    }

    @Test
    public void testValidateParameters() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormUtils.KEY, new JSONArray());
        Triple<Boolean, JSONObject, JSONArray> result = PncJsonFormUtils.validateParameters(jsonObject.toString());
        Assert.assertNotNull(result);
    }

    @Test
    public void testProcessGenderReplaceMwithMale() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.KEY, PncConstants.SEX);
        jsonObject.put(PncConstants.KeyConstants.VALUE, "m");
        jsonArray.put(jsonObject);
        PncJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals("Male", jsonArray.getJSONObject(0).get("value"));
    }

    @Test
    public void testProcessGenderReplaceFwithFemale() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.KEY, PncConstants.SEX);
        jsonObject.put(PncConstants.KeyConstants.VALUE, "f");
        jsonArray.put(jsonObject);
        PncJsonFormUtils.processGender(jsonArray);

        Assert.assertEquals("Female", jsonArray.getJSONObject(0).get("value"));
    }

    @Test
    public void testProcessGenderShouldReplaceNothing() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.KEY, PncConstants.SEX);
        jsonObject.put(PncConstants.KeyConstants.VALUE, "L");
        jsonArray.put(jsonObject);
        PncJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals("", jsonArray.getJSONObject(0).get("value"));
    }

    @Test
    public void testProcessGenderCheckNullOnGenderJsonObject() {
        JSONArray jsonArray = new JSONArray();
        PncJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals(jsonArray.length(), 0);
    }

    @Test
    public void testProcessGenderShouldThrowJSONException() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.KEY, PncConstants.SEX);
        jsonArray.put(jsonObject);
        PncJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals(jsonArray.getJSONObject(0).length(), 1);
    }

    @Test
    public void testProcessLocationFields() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TREE);
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put("test");
        jsonObject.put(JsonFormConstants.VALUE, jsonArray1.toString());
        jsonArray.put(jsonObject);
        ArrayList<String> defaultLocations = new ArrayList<>();
        defaultLocations.add("Country");
        CoreLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(SyncConfiguration.class));
        LocationHelper.init(defaultLocations,
                "Country");
        PncJsonFormUtils.processLocationFields(jsonArray);
        Assert.assertEquals(jsonArray.getJSONObject(0).getString(JsonFormConstants.VALUE), "test");
    }

    @Test
    public void testLastInteractedWithEmpty() {
        JSONArray jsonArray = new JSONArray();
        PncJsonFormUtils.lastInteractedWith(jsonArray);
        Assert.assertEquals(jsonArray.length(), 1);
    }

    @Test
    public void testDobUnknownUpdateFromAge() throws JSONException {
        JSONArray jsonArrayFields = new JSONArray();

        JSONArray jsonArrayDobUnknown = new JSONArray();
        JSONObject jsonObjectOptions = new JSONObject();
        jsonObjectOptions.put(PncConstants.KeyConstants.VALUE, Boolean.TRUE.toString());
        jsonArrayDobUnknown.put(jsonObjectOptions);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormUtils.KEY, PncConstants.JsonFormKeyConstants.DOB_UNKNOWN);
        jsonObject.put(PncConstants.JsonFormKeyConstants.OPTIONS, jsonArrayDobUnknown);

        JSONObject jsonObjectDob = new JSONObject();
        jsonObjectDob.put(JsonFormUtils.KEY, PncConstants.JsonFormKeyConstants.DOB_ENTERED);

        JSONObject jsonObjectAgeEntered = new JSONObject();
        jsonObjectAgeEntered.put(JsonFormUtils.KEY, PncConstants.JsonFormKeyConstants.AGE_ENTERED);
        jsonObjectAgeEntered.put(JsonFormUtils.VALUE, "34");


        jsonArrayFields.put(jsonObject);
        jsonArrayFields.put(jsonObjectAgeEntered);
        jsonArrayFields.put(jsonObjectDob);

        String expected = "[{\"options\":[{\"value\":\"true\"}],\"key\":\"dob_unknown\"},{\"value\":\"34\",\"key\":\"age_entered\"}," +
                "{\"value\":\"01-01-1986\",\"key\":\"dob_entered\"},{\"openmrs_entity\":\"person\"," +
                "\"openmrs_entity_id\":\"birthdate_estimated\",\"value\":1,\"key\":\"birthdate_estimated\"}]";

        PncJsonFormUtils.dobUnknownUpdateFromAge(jsonArrayFields);

        Assert.assertEquals(expected, jsonArrayFields.toString());
    }

    @Test
    public void testProcessReminderSetToTrue() throws Exception {
        JSONArray jsonArrayFields = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.KEY, PncConstants.JsonFormKeyConstants.REMINDERS);

        JSONArray jsonArrayOptions = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(PncConstants.KeyConstants.VALUE, Boolean.toString(true));
        jsonArrayOptions.put(jsonObject1);

        jsonObject.put(PncConstants.JsonFormKeyConstants.OPTIONS, jsonArrayOptions);
        jsonArrayFields.put(jsonObject);

        Whitebox.invokeMethod(PncJsonFormUtils.class, "processReminder", jsonArrayFields);

        String expected = "[{\"options\":[{\"value\":\"true\"}],\"value\":1,\"key\":\"reminders\"}]";
        Assert.assertEquals(expected, jsonArrayFields.toString());

    }

    @Test
    public void testProcessReminderSetToFalse() throws Exception {
        JSONArray jsonArrayFields = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.KEY, PncConstants.JsonFormKeyConstants.REMINDERS);

        JSONArray jsonArrayOptions = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(PncConstants.KeyConstants.VALUE, Boolean.toString(false));
        jsonArrayOptions.put(jsonObject1);

        jsonObject.put(PncConstants.JsonFormKeyConstants.OPTIONS, jsonArrayOptions);
        jsonArrayFields.put(jsonObject);

        Whitebox.invokeMethod(PncJsonFormUtils.class, "processReminder", jsonArrayFields);

        String expected = "[{\"options\":[{\"value\":\"false\"}],\"value\":0,\"key\":\"reminders\"}]";
        Assert.assertEquals(expected, jsonArrayFields.toString());

    }

    @Test
    public void testFieldsHasEmptyStep() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = PncJsonFormUtils.fields(jsonObject, "");
        Assert.assertNull(jsonArray);
    }

    @Test
    public void testFieldHasStep() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String step = "STEP1";
        JSONObject jsonObjectWithFields = new JSONObject();
        jsonObjectWithFields.put(PncJsonFormUtils.FIELDS, new JSONArray());
        jsonObject.put(step, jsonObjectWithFields);
        JSONArray jsonArray = PncJsonFormUtils.fields(jsonObject, step);
        Assert.assertNotNull(jsonArray);
    }

    @Test
    public void testFormTagShouldReturnValidFormTagObject() {
        PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), Mockito.mock(PncConfiguration.class),
                BuildConfig.VERSION_CODE, 1);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("1");
        FormTag formTag = PncJsonFormUtils.formTag(allSharedPreferences);
        Assert.assertTrue((BuildConfig.VERSION_CODE == formTag.appVersion));
        Assert.assertTrue((formTag.databaseVersion == 1));
        Assert.assertEquals("1", formTag.providerId);
    }

    @Test
    public void testGetFieldValueShouldReturnNullWithInvalidJsonString() {
        Assert.assertNull(PncJsonFormUtils.getFieldValue("", "", ""));
    }

    @Test
    public void testGetFieldValueShouldReturnNullWithValidJsonStringWithoutStepKey() throws JSONException {
        JSONObject jsonForm = new JSONObject();
        JSONObject jsonStep = new JSONObject();
        jsonStep.put(PncJsonFormUtils.FIELDS, new JSONArray());
        Assert.assertNull(PncJsonFormUtils.getFieldValue(jsonForm.toString(), PncJsonFormUtils.STEP1, ""));

    }

    @Test
    public void testGetFieldValueShouldReturnPassedValue() throws JSONException {
        JSONObject jsonForm = new JSONObject();
        JSONObject jsonStep = new JSONObject();
        JSONArray jsonArrayFields = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncJsonFormUtils.KEY, PncConstants.JsonFormKeyConstants.REMINDERS);
        jsonObject.put(PncJsonFormUtils.VALUE, "some reminder");
        jsonArrayFields.put(jsonObject);
        jsonStep.put(PncJsonFormUtils.FIELDS, jsonArrayFields);
        jsonForm.put(PncJsonFormUtils.STEP1, jsonStep);

        Assert.assertEquals("some reminder", PncJsonFormUtils.getFieldValue(jsonForm.toString(), PncJsonFormUtils.STEP1, PncConstants.JsonFormKeyConstants.REMINDERS));
    }

    @Test
    public void testProcessPncDetailsFormShouldReturnNullJsonFormNull() {
        Assert.assertNull(PncJsonFormUtils.processPncRegistrationForm("", Mockito.mock(FormTag.class)));
    }
}
