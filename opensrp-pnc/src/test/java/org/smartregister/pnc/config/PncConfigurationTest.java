package org.smartregister.pnc.config;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PncConfigurationTest {

    private PncConfiguration pncConfiguration;

    @Before
    public void setUp() {

        PncMetadata pncMetadata = new PncMetadata(PncConstants.Form.PNC_REGISTRATION
                , PncDbConstants.KEY.TABLE
                , PncConstants.EventTypeConstants.PNC_REGISTRATION
                , PncConstants.EventTypeConstants.UPDATE_PNC_REGISTRATION
                , PncConstants.CONFIG
                , null
                , BasePncProfileActivity.class
                ,true);

        pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProvider.class)
                .setPncMetadata(pncMetadata)
                .setPncVisitScheduler(null)
                .setPncRegisterProviderMetadata(null)
                .setPncRegisterRowOptions(PncRegisterRowOptions.class)
                .setPncRegisterSwitcher(PncRegisterSwitcher.class)
                .setBottomNavigationEnabled(true)
                .setMaxCheckInDurationInMinutes(1)
                .addPncFormProcessingTask("test", PncFormProcessingTask.class)
                .build();
    }

    @After
    public void tearDown() {
        pncConfiguration = null;
    }

    @Test
    public void getPncVisitSchedulerShouldNotNull() {
        assertNotNull(pncConfiguration.getPncVisitScheduler());
    }

    @Test
    public void getPncMetadataShouldNotNull() {
        assertNotNull(pncConfiguration.getPncMetadata());
    }

    @Test
    public void getPncRegisterProviderMetadataShouldNotNull() {
        assertNotNull(pncConfiguration.getPncRegisterProviderMetadata());
    }

    @Test
    public void getPncRegisterRowOptionsShouldNotNull() {
        assertNotNull(pncConfiguration.getPncRegisterRowOptions());
    }

    @Test
    public void getPncRegisterQueryProviderShouldNotNull() {
        assertNotNull(pncConfiguration.getPncRegisterQueryProvider());
    }

    @Test
    public void getPncRegisterSwitcherShouldNotNull() {
        assertNotNull(pncConfiguration.getPncRegisterSwitcher());
    }

    @Test
    public void getPncFormProcessingTasksShouldNotNull() {
        assertNotNull(pncConfiguration.getPncFormProcessingTasks());
    }

    @Test
    public void getMaxCheckInDurationInMinutesShouldReturnOne() {
        assertEquals(1, pncConfiguration.getMaxCheckInDurationInMinutes());
    }

    @Test
    public void isBottomNavigationEnabledShouldReturnTrue() {
        assertTrue(pncConfiguration.isBottomNavigationEnabled());
    }

    private static class PncRegisterQueryProvider extends PncRegisterQueryProviderContract {

        @NonNull
        @Override
        public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
            return null;
        }

        @NonNull
        @Override
        public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
            return new String[0];
        }

        @NonNull
        @Override
        public String mainSelectWhereIDsIn() {
            return null;
        }
    }

}
