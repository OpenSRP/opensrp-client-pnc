package org.smartregister.pnc.processor;

import android.content.Context;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.Obs;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncStillBorn;
import org.smartregister.pnc.repository.PncChildRepository;
import org.smartregister.pnc.repository.PncStillBornRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PncMiniClientProcessorForJavaTest extends BaseTest {

    private PncMiniClientProcessorForJava pncMiniClientProcessorForJava;

    @Mock
    private PncLibrary pncLibrary;

    @Before
    public void setUp() {
        pncMiniClientProcessorForJava = new PncMiniClientProcessorForJava(Mockito.mock(Context.class));
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateKeyValuesFromEventShouldFillMapCorrectly() throws Exception {
        Event event = new Event();

        Obs obs1 = new Obs();
        obs1.setHumanReadableValue("one");
        obs1.setFormSubmissionField("count1");

        Obs obs2 = new Obs();
        obs2.setHumanReadableValues(new ArrayList<>());
        obs2.setValues(Arrays.asList("huma", "man"));
        obs2.setFormSubmissionField("count2");

        event.addObs(obs1);
        event.addObs(obs2);

        HashMap<String, String> map = new HashMap<>();

        Whitebox.invokeMethod(pncMiniClientProcessorForJava,
                "generateKeyValuesFromEvent", event,
                map);

        Assert.assertEquals(2, map.size());
    }

    @Test
    public void testProcessBabiesBornShouldCallRepositoryIfBaseEntityIdExists() throws Exception {
        String babiesBorn = "{\"3b562659b3f64f998dccfae199f7ea0d\":" +
                "{\"baby_care_mgt\":\"[\\\"kmc\\\",\\\"antibiotics\\\"]\",\"apgar\":\"10\",\"base_entity_id\":\"2323-2323-sds\",\"child_hiv_status\":\"Exposed\",\"nvp_administration\":\"Yes\",\"baby_first_cry\":\"Yes\",\"baby_complications\":\"[\\\"premature\\\",\\\"asphyxia\\\"]\",\"baby_first_name\":\"Nameless\",\"baby_last_name\":\"Master\",\"baby_dob\":\"03-06-2020\",\"discharged_alive\":\"Yes\",\"birth_weight_entered\":\"2300\",\"birth_height_entered\":\"54\",\"baby_gender\":\"Male\",\"bf_first_hour\":\"Yes\"}}";
        PncChildRepository pncChildRepository = Mockito.spy(new PncChildRepository());
        Mockito.doReturn(pncChildRepository).when(pncLibrary).getPncChildRepository();
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        Mockito.doReturn(true).when(pncChildRepository).saveOrUpdate(Mockito.any(PncChild.class));
        Event event = new Event();
        event.setBaseEntityId("232-wew3-23");
        event.setEventDate(new DateTime());
        Whitebox.invokeMethod(pncMiniClientProcessorForJava,
                "processBabiesBorn", babiesBorn,
                event);

        Mockito.verify(pncChildRepository, Mockito.times(1))

                .saveOrUpdate(Mockito.any(PncChild.class));
    }

    @Test
    public void testProcessStillBornShouldCallRepositoryIfMotherBaseEntityIdExists() throws Exception {
        String stillBorn = "{\"cabdeffcdca64a63800c8718f94d72ee\":{\"stillbirth_condition\":\"Fresh\"},\"70bb07814ded40a59f1346928909d134\":{\"stillbirth_condition\":\"Macerated\"}}";
        PncStillBornRepository pncStillBornRepository = Mockito.spy(new PncStillBornRepository());
        Mockito.doReturn(pncStillBornRepository).when(pncLibrary).getPncStillBornRepository();
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        Mockito.doReturn(true).when(pncStillBornRepository).saveOrUpdate(Mockito.any(PncStillBorn.class));
        Event event = new Event();
        event.setBaseEntityId("232-wew3-23");
        event.setEventDate(new DateTime());
        Whitebox.invokeMethod(pncMiniClientProcessorForJava,
                "processStillBorn", stillBorn,
                event);

        Mockito.verify(pncStillBornRepository, Mockito.times(2))
                .saveOrUpdate(Mockito.any(PncStillBorn.class));
    }

}