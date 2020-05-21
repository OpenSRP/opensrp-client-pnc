package org.smartregister.pnc.processor;


import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.exception.PncCloseEventProcessException;
import org.smartregister.pnc.utils.PncConstants;

import java.util.ArrayList;
import java.util.HashSet;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PncLibrary.class)
public class MaternityMiniClientProcessorForJavaTest extends BaseTest {

    private PncMiniClientProcessorForJava maternityMiniClientProcessorForJava;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        maternityMiniClientProcessorForJava = Mockito.spy(new PncMiniClientProcessorForJava(Mockito.mock(Context.class)));
        Event event = new Event();
        event.addDetails(PncConstants.JsonFormKey.VISIT_ID, "visitId");
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void getEventTypesShouldReturnAtLeast6EventTypesAllStartingWithMaternity() {
        HashSet<String> eventTypes = maternityMiniClientProcessorForJava.getEventTypes();

        Assert.assertEquals(4, eventTypes.size());
        for (String eventType: eventTypes) {
            Assert.assertTrue(eventType.contains("Maternity"));
        }
    }

    @Test
    public void processEventClientShouldThrowExceptionWhenClientIsNull() throws Exception {
        expectedException.expect(PncCloseEventProcessException.class);
        expectedException.expectMessage("Could not process this Maternity Close Event because Client bei referenced by Maternity Close event does not exist");

        Event event = new Event().withEventType(PncConstants.EventType.MATERNITY_CLOSE).withBaseEntityId("bei");
        event.addDetails("d", "d");

        EventClient eventClient = new EventClient(event, null);

        maternityMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<>(), null);
    }
}
