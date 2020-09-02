package org.smartregister.pnc.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.sync.CloudantDataHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class PncReverseJsonFormUtilsTest extends BaseTest {

    @Mock
    private PncLibrary pncLibrary;

    @Mock
    private PncConfiguration pncConfiguration;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private CloudantDataHandler cloudantDataHandler;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context opensrpContext;

    @Mock
    private ImageRepository imageRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPrepareJsonEditPncRegistrationForm() throws JSONException {

        String baseEntityId = "234324-erw432e";

        Map<String, String> detailsMap = new HashMap<>();
        detailsMap.put("first_name", "John");
        detailsMap.put("last_name", "Doe");
        detailsMap.put(PncConstants.JsonFormKeyConstants.ENTITY_ID, baseEntityId);
        detailsMap.put(PncConstants.KeyConstants.BASE_ENTITY_ID, baseEntityId);


        PncMetadata maternityMetadata = new PncMetadata(PncConstants.Form.PNC_REGISTRATION
                , "table-name"
                , PncConstants.EventTypeConstants.PNC_REGISTRATION
                , PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION
                , "config"
                , BasePncFormActivity.class
                , BasePncProfileActivity.class
                , false);

        Mockito.doReturn(null).when(imageRepository).findByEntityId(baseEntityId);

        Mockito.doReturn(imageRepository).when(opensrpContext).imageRepository();

        Mockito.doReturn(maternityMetadata).when(pncConfiguration).getPncMetadata();

        Mockito.doReturn("Location A").when(allSharedPreferences).fetchCurrentLocality();

        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();

        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();

        Mockito.doReturn(opensrpContext).when(coreLibrary).context();

        ReflectionHelpers.setStaticField(CloudantDataHandler.class, "instance", cloudantDataHandler);

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);

        String strForm = PncReverseJsonFormUtils
                .prepareJsonEditPncRegistrationForm(
                        detailsMap,
                        new ArrayList<>(),
                        RuntimeEnvironment.application.getBaseContext());

        JSONObject form = new JSONObject(strForm);
        JSONArray fields = FormUtils.getMultiStepFormFields(form);

        JSONObject jsonFirstNameObject = FormUtils.getFieldJSONObject(fields, "first_name");

        JSONObject jsonLastNameObject = FormUtils.getFieldJSONObject(fields, "last_name");

        Assert.assertEquals(detailsMap.get("first_name"), jsonFirstNameObject.optString(JsonFormConstants.VALUE));
        Assert.assertEquals(detailsMap.get("last_name"), jsonLastNameObject.optString(JsonFormConstants.VALUE));
        Assert.assertEquals(baseEntityId, form.optString(PncConstants.JsonFormKeyConstants.ENTITY_ID));
    }
}