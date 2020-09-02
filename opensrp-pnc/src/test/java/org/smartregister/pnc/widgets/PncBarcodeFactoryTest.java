package org.smartregister.pnc.widgets;


import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.pnc.utils.PncConstants;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class PncBarcodeFactoryTest {

    @Mock
    private Context context;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private CommonListener commonListener;

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

        List<View> list = pncBarcodeFactory.getViewsFromJson(stepName, context, jsonFormFragment, jsonObject, commonListener);
        assertEquals(0, list.size());
    }

    @Test
    public void getViewsFromJsonShouldVerifyScenarioTwo() throws Exception {

        String stepName = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PncConstants.KeyConstants.LOOK_UP, "true");

        List<View> list = pncBarcodeFactory.getViewsFromJson(stepName, context, jsonFormFragment, jsonObject, commonListener, false);
        assertEquals(0, list.size());
    }

}
