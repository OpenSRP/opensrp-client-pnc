package org.smartregister.pnc;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.shadows.ShadowPncLibrary;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowPncLibrary.class})
public class PncLibraryTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void initShouldCreateNewLibraryInstanceWhenInstanceIsNull() {
        assertNull(ReflectionHelpers.getStaticField(PncLibrary.class, "instance"));

        PncLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(PncConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(ReflectionHelpers.getStaticField(PncLibrary.class, "instance"));
    }

    @Test
    public void getInstanceShouldThrowIllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Instance does not exist!!! Call org.smartregister.pnc.PncLibrary"
                + ".init method in the onCreate method of "
                + "your Application class");

        PncLibrary.getInstance();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void getPncRulesEngineHelperShouldReturnNonNull() {
        PncLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(PncConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(PncLibrary.getInstance().getPncRulesEngineHelper());
    }

    @Test
    public void getLatestValidCheckInDateShouldReturn1DayFromNow() {
        PncLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(PncConfiguration.class), BuildConfig.VERSION_CODE, 1);

        long timeNow = new Date().getTime();

        assertEquals(timeNow - 24 * 60 * 60 * 1000, PncLibrary.getInstance().getLatestValidCheckInDate().getTime(), 100);
    }


//TODO: Fix Robolectric not mocking calendar & date class using shadow class and then these two tests are going to be feasible

    /*@Test
    public void isPatientInTreatedStateShouldReturnTrueWhenCurrentDateIsBeforeMidnightOfTreatmentDate() throws ParseException {
        PncLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(PncConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PncDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-04 20:23:20");

        ShadowPncLibrary.setMockedTime(date.getTime());
        Assert.assertTrue(PncLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }*/

    @Test
    public void isPatientInTreatedStateShouldReturnFalseWhenCurrentDateIsAfterMidnightOfTreatmentDate() throws ParseException {
        PncLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(PncConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PncDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-05 20:23:20");

        ShadowPncLibrary.setMockedTime(date.getTime());
        Assert.assertFalse(PncLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }
}
