package org.smartregister.pnc.utils;

import android.graphics.Bitmap;

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
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.BuildConfig;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.provider.PncRegisterQueryProviderTest;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import id.zelory.compressor.Compressor;

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

    @Mock
    private DrishtiApplication drishtiApplication;

    @Mock
    private CoreLibrary coreLibrary;

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
    @PrepareForTest(PncLibrary.class)
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndEntityIdBlank() throws Exception {

        String jsonString = "{\"encounter_type\":\"PNC Registration\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"fields\":[{\"key\":\"OPENSRP_ID\"}]}}";

        /*PncConfiguration pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProviderTest.class)
                .setPncMetadata(pncMetadata)
                .build();*/

        PncMetadata pncMetadata = PowerMockito.mock(PncMetadata.class);
        PncConfiguration pncConfiguration = PowerMockito.mock(PncConfiguration.class);

        PowerMockito.when(pncMetadata.getPncRegistrationFormName()).thenReturn(PncConstants.Form.PNC_REGISTRATION);

        //PncLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), pncConfiguration,
        //BuildConfig.VERSION_CODE, 1);

        PowerMockito.mockStatic(PncLibrary.class);

        UniqueIdRepository uniqueIdRepository = PowerMockito.spy(new UniqueIdRepository());
        UniqueId uniqueId = PowerMockito.spy(new UniqueId());
        PowerMockito.when(PncLibrary.getInstance()).thenReturn(pncLibrary);
        PowerMockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(uniqueId);
        PowerMockito.when(uniqueId.getOpenmrsId()).thenReturn("123-dfcxv-3-sdf");
        PowerMockito.when(PncLibrary.getInstance().getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        PowerMockito.when(PncLibrary.getInstance().getPncConfiguration()).thenReturn(pncConfiguration);
        PowerMockito.when(PncLibrary.getInstance().getPncConfiguration().getPncMetadata()).thenReturn(pncMetadata);

        HashMap<String, String> injectedFields = new HashMap<>();
        injectedFields.put("OPENSRP_ID", "1");

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject result = PncJsonFormUtils.getFormAsJson(jsonObject, PncConstants.Form.PNC_REGISTRATION, "", "", injectedFields);
        Assert.assertNotNull(result);
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
        Assert.assertEquals(hierarchyString, jsonObject.optString(JsonFormConstants.DEFAULT));
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
        jsonObject.put("count", 1);
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

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        Context mockContext = PowerMockito.mock(Context.class);

        PowerMockito.doReturn(mockContext).when(coreLibrary).context();

        PowerMockito.doReturn(PowerMockito.mock(AllSharedPreferences.class)).when(mockContext).allSharedPreferences();

        Event event = PncJsonFormUtils.tagSyncMetadata(new Event());
        Assert.assertNotNull(event);

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
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

        String expected = "01-01-1987";

        PncJsonFormUtils.dobUnknownUpdateFromAge(jsonArrayFields);

        Assert.assertEquals(expected, JsonFormUtils.getFieldJSONObject(jsonArrayFields, PncConstants.JsonFormKeyConstants.DOB_ENTERED).getString(JsonFormUtils.VALUE));
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

        PncJsonFormUtils.processReminder(jsonArrayFields);

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

        PncJsonFormUtils.processReminder(jsonArrayFields);

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

    @Test
    public void testSaveImageShouldPassCorrectArgs() throws Exception {
        String providerId = "demo";
        String baseEntityId = "2323-wxdfd9-34";
        String imageLocation = "/";
        Compressor compressor = Mockito.mock(Compressor.class);
        PowerMockito.mockStatic(PncUtils.class);
        PowerMockito.doNothing().when(PncUtils.class, "saveImageAndCloseOutputStream", Mockito.any(Bitmap.class), Mockito.any(File.class));
        Bitmap bitmap = Mockito.mock(Bitmap.class);
        Mockito.when(compressor.compressToBitmap(Mockito.any(File.class))).thenReturn(bitmap);
        Mockito.when(pncLibrary.getCompressor()).thenReturn(compressor);
        android.content.Context context = Mockito.mock(android.content.Context.class);
        File file = Mockito.mock(File.class);
        Mockito.when(file.getAbsolutePath()).thenReturn("/home/opensrp");
        Mockito.when(context.getDir("opensrp", android.content.Context.MODE_PRIVATE)).thenReturn(file);
        Mockito.when(drishtiApplication.getApplicationContext()).thenReturn(context);
        Context opensrpContext = Mockito.mock(Context.class);
        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(opensrpContext.imageRepository()).thenReturn(imageRepository);
        PowerMockito.when(PncUtils.class, "context").thenReturn(opensrpContext);
        Mockito.when(pncLibrary.context()).thenReturn(opensrpContext);

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);

        PncJsonFormUtils.saveImage(providerId, baseEntityId, imageLocation);

        ArgumentCaptor<ProfileImage> profileImageArgumentCaptor = ArgumentCaptor.forClass(ProfileImage.class);

        Mockito.verify(imageRepository, Mockito.times(1)).add(profileImageArgumentCaptor.capture());

        ProfileImage profileImage = profileImageArgumentCaptor.getValue();
        Assert.assertNotNull(profileImage);
        Assert.assertEquals("demo", profileImage.getAnmId());
        Assert.assertEquals(baseEntityId, profileImage.getEntityID());
        Assert.assertEquals("/home/opensrp/2323-wxdfd9-34.JPEG", profileImage.getFilepath());
        Assert.assertEquals(ImageRepository.TYPE_Unsynced, profileImage.getSyncStatus());
    }
}
