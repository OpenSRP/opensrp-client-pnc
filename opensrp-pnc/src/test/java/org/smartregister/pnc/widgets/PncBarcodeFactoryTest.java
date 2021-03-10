package org.smartregister.pnc.widgets;


import android.app.Application;
import android.view.View;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.AppExecutors;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.pnc.BaseRobolectricTest;
import org.smartregister.pnc.utils.PncConstants;

import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

public class PncBarcodeFactoryTest extends BaseRobolectricTest {

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private CommonListener commonListener;

    @Mock
    private AppExecutors appExecutors;

    private PncBarcodeFactory pncBarcodeFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        pncBarcodeFactory = spy(new PncBarcodeFactory());
    }

    @Test
    public void getViewsFromJsonShouldVerifyScenarioOne() throws Exception {

        String stepName = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.LOOK_UP, "true");
        Application application = RuntimeEnvironment.application;

        JsonApi jsonApi = mock(JsonApi.class);
        RelativeLayout relativeLayout = new RelativeLayout(application);
        doReturn(relativeLayout).when(pncBarcodeFactory).getRootLayout(ArgumentMatchers.eq(application));
        doReturn(jsonApi).when(jsonFormFragment).getJsonApi();
        doReturn(appExecutors).when(jsonApi).getAppExecutors();
        doReturn(mock(Executor.class)).when(appExecutors).mainThread();
        List<View> list = pncBarcodeFactory.getViewsFromJson(stepName, application, jsonFormFragment, jsonObject, commonListener);
        assertEquals(1, list.size());
    }

}
