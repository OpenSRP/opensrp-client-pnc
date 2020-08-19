package org.smartregister.pnc.config;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PncCloseFormProcessingTest {

    private PncCloseFormProcessing pncCloseFormProcessing;

    @Before
    public void setUp() {

        CoreLibrary coreLibrary = mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        PncLibrary pncLibrary = mock(PncLibrary.class);
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);

        pncCloseFormProcessing = new PncCloseFormProcessing();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
        pncCloseFormProcessing = null;
    }

    @Test
    public void testProcessPncFormShouldReturnValidEventList() throws JSONException {
        String jsonString = "{\"encounter_type\":\"PNC Close\",\"entity_id\":\"\",\"metadata\":{\"encounter_location\":\"\"},\"step1\":{\"title\":\"PNC Close\",\"fields\":[{\"key\":\"pnc_close_reason\",\"value\":\"woman_died\"},{\"key\":\"date_of_death\",\"value\":\"09-08-2020\"},{\"key\":\"place_of death\",\"value\":\"Community\"},{\"key\":\"death_cause\",\"value\":\"Unknown\"}]}}";

        String strClient = "{\"attributes\":{}}";

        String baseEntityId = "3242-23-423-4-234-234";

        Context context = mock(Context.class);
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
        Intent intent = mock(Intent.class);
        EventClientRepository eventClientRepository = mock(EventClientRepository.class);
        PncConfiguration pncConfiguration = mock(PncConfiguration.class);
        PncMetadata pncMetadata = mock(PncMetadata.class);

        doReturn(context).when(CoreLibrary.getInstance()).context();
        doReturn(allSharedPreferences).when(context).allSharedPreferences();
        doReturn("").when(allSharedPreferences).fetchRegisteredANM();
        doReturn(0).when(PncLibrary.getInstance()).getApplicationVersion();
        doReturn(0).when(PncLibrary.getInstance()).getDatabaseVersion();
        doReturn(true).when(intent).hasExtra(eq(PncConstants.IntentKey.BASE_ENTITY_ID));
        doReturn(baseEntityId).when(intent).getStringExtra(eq(PncConstants.IntentKey.BASE_ENTITY_ID));
        doReturn(true).when(intent).hasExtra(eq(PncConstants.IntentKey.ENTITY_TABLE));
        doReturn("ec_client").when(intent).getStringExtra(eq(PncConstants.IntentKey.ENTITY_TABLE));
        doReturn(eventClientRepository).when(PncLibrary.getInstance()).eventClientRepository();

        doReturn(new JSONObject(strClient)).when(eventClientRepository).getClientByBaseEntityId(anyString());
        doReturn(pncConfiguration).when(PncLibrary.getInstance()).getPncConfiguration();
        doReturn(pncMetadata).when(pncConfiguration).getPncMetadata();
        doReturn(PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION).when(pncMetadata).getUpdateEventType();

        List<Event> events = pncCloseFormProcessing.processPncForm("PNC Close", jsonString, intent);

        assertEquals(1, events.size());
        assertEquals(PncConstants.EventTypeConstants.DEATH, events.get(0).getEventType());
        assertEquals(baseEntityId, events.get(0).getBaseEntityId());
        assertEquals("ec_client", events.get(0).getEntityType());

        verify(eventClientRepository, Mockito.times(2)).addEvent(Mockito.eq(baseEntityId), Mockito.any(JSONObject.class));

        verify(eventClientRepository, Mockito.times(1)).addorUpdateClient(Mockito.eq(baseEntityId), Mockito.any(JSONObject.class));
    }
}
