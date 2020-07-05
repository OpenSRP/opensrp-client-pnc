package org.smartregister.pnc.presenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncRegisterActivityContract;

import java.lang.ref.WeakReference;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PncLibrary.class)
public class BasePncRegisterActivityPresenterTest {

    @Mock
    private PncLibrary pncLib;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void startFormShouldPassEntityTableAndBaseEntityIdToActivity() throws JSONException {
        PowerMockito.mockStatic(PncLibrary.class);
        PowerMockito.when(PncLibrary.getInstance()).thenReturn(pncLib);
        //PowerMockito.when(pncLib.getPncChildRepository()).thenReturn(maternityOutcomeFormRepository);

        PncRegisterActivityContract.View view = Mockito.mock(PncRegisterActivityContract.View.class);
        PncRegisterActivityContract.Model model = Mockito.mock(PncRegisterActivityContract.Model.class);

        BasePncRegisterActivityPresenter baseMaternityRegisterActivityPresenter = new PncRegisterActivityPresenter(view, model);

        Mockito.doReturn(new JSONObject()).when(model).getFormAsJson(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.nullable(HashMap.class));

        ReflectionHelpers.setField(baseMaternityRegisterActivityPresenter, "viewReference", new WeakReference<PncRegisterActivityContract.View>(view));
        baseMaternityRegisterActivityPresenter.setModel(model);

        baseMaternityRegisterActivityPresenter.startForm("check_in.json", "90923-dsfds", "meta", "location-id", null, "ec_child");

        Mockito.verify(view, Mockito.times(1))
                .startFormActivityFromFormJson(Mockito.anyString(), Mockito.any(JSONObject.class), Mockito.any(HashMap.class));
    }
}
