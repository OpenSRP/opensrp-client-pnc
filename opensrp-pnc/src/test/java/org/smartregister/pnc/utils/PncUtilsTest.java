package org.smartregister.pnc.utils;

import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.pojo.PncMetadata;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class PncUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PncLibrary pncLibrary;

    @Mock
    private PncConfiguration pncConfiguration;

    @Mock
    private PncMetadata pncMetadata;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", PncUtils.fillTemplate(template, facts));
    }

    @Test
    public void convertStringToDate() {
        Date date = PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, "2019-10-28 18:09:49");
        assertEquals("2019-10-28 18:09:49", PncUtils.convertDate(date, PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void generateNIds() {
        String result = PncUtils.generateNIds(0);
        assertEquals(result, "");

        String result1 = PncUtils.generateNIds(1);
        assertEquals(result1.split(",").length, 1);
    }

    @Test
    public void getIntentValue() {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.hasExtra("test")).thenReturn(false);
        assertNull(PncUtils.getIntentValue(intent, "test"));

        Mockito.when(intent.hasExtra("test")).thenReturn(true);
        Mockito.when(intent.getStringExtra("test")).thenReturn("test");
        assertEquals("test", PncUtils.getIntentValue(intent, "test"));
    }

    @Test
    public void getIntentValueReturnNull() {
        assertNull(PncUtils.getIntentValue(null, "test"));
    }

    @Test
    public void metadata() {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();
        Mockito.doReturn(pncMetadata).when(pncConfiguration).getPncMetadata();

        assertEquals(pncMetadata, PncUtils.metadata());

        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void testGetClientAge(){
        assertEquals("13", PncUtils.getClientAge("13y 4m", "y"));
        assertEquals("4m", PncUtils.getClientAge("4m", "y"));
        assertEquals("5", PncUtils.getClientAge("5y 4w", "y"));
        assertEquals("3y", PncUtils.getClientAge("3y", "y"));
        assertEquals("5w 6d", PncUtils.getClientAge("5w 6d", "y"));
        assertEquals("6d", PncUtils.getClientAge("6d", "y"));
    }

    @Test
    public void isTemplateShouldReturnFalseIfStringDoesNotContainMatchingBraces() {
        assertFalse(PncUtils.isTemplate("{ This is a sytling brace"));
        assertFalse(PncUtils.isTemplate("This is display text"));
    }

    @Test
    public void isTemplateShouldReturnTrueIfStringContainsMatchingBraces() {
        assertTrue(PncUtils.isTemplate("Project Name: {project_name}"));
    }

    @Test
    public void buildActivityFormIntentShouldCreateIntentWithWizardEnabledWhenFormHasMoreThanOneStep() throws JSONException {
        // Mock calls to PncLibrary
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();
        Mockito.doReturn(pncMetadata).when(pncConfiguration).getPncMetadata();
        Mockito.doReturn(BasePncFormActivity.class).when(pncMetadata).getPncFormActivity();

        JSONObject jsonForm = new JSONObject();
        jsonForm.put("step1", new JSONObject());
        jsonForm.put("step2", new JSONObject());
        jsonForm.put("step3", new JSONObject());

        jsonForm.put(PncJsonFormUtils.ENCOUNTER_TYPE, PncConstants.EventTypeConstants.PNC_OUTCOME);

        HashMap<String, String> parcelableData = new HashMap<>();
        String baseEntityId = "89283-23dsd-23sdf";
        parcelableData.put(PncConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);

        Intent actualResult = PncUtils.buildFormActivityIntent(jsonForm, parcelableData, Mockito.mock(Context.class));
        Form form = (Form) actualResult.getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM);

        assertTrue(form.isWizard());
        assertEquals(PncConstants.EventTypeConstants.PNC_OUTCOME, form.getName());
        assertEquals(baseEntityId, actualResult.getStringExtra(PncConstants.IntentKey.BASE_ENTITY_ID));
    }
}
