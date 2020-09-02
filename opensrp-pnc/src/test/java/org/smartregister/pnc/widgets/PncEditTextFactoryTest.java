package org.smartregister.pnc.widgets;


import android.content.Context;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.AppExecutors;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.pnc.utils.PncConstants;

import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class PncEditTextFactoryTest {

    private PncEditTextFactory pncEditTextFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        pncEditTextFactory = spy(new PncEditTextFactory());
    }

    @Test
    public void attachLayoutShouldVerify() throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openmrs_entity_parent", "");
        jsonObject.put("openmrs_entity", "");
        jsonObject.put("openmrs_entity_id", "");
        jsonObject.put("key", "");
        jsonObject.put("type", "");
        jsonObject.put(PncConstants.KeyConstants.LOOK_UP, "true");

        Context ctx = mock(Context.class, withSettings().extraInterfaces(JsonApi.class));
        JsonFormFragment jsonFormFragment = mock(JsonFormFragment.class);
        JsonApi jsonApi = mock(JsonApi.class);
        AppExecutors appExecutors = mock(AppExecutors.class);
        Executor executor = mock(Executor.class);
        MaterialEditText editText = mock(MaterialEditText.class);
        ImageView editable = mock(ImageView.class);

        when(jsonFormFragment.getJsonApi()).thenReturn(jsonApi);
        when(jsonApi.getAppExecutors()).thenReturn(appExecutors);
        when(appExecutors.mainThread()).thenReturn(executor);
        doNothing().when(executor).execute(any(Runnable.class));

        pncEditTextFactory.attachLayout("", ctx, jsonFormFragment, jsonObject, editText, editable);

        verify(editText, times(1)).setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
    }

}
