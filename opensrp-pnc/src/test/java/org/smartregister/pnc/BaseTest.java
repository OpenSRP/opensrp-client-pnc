package org.smartregister.pnc;

import android.os.Build;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(MockitoJUnitRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public abstract class BaseTest {


    protected DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
    }
}
