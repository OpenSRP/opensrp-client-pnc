package org.smartregister.pnc.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;

@RunWith(RobolectricTestRunner.class)
public class PncProfileOverviewFragmentPresenterTest extends BaseTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PncProfileOverviewFragmentPresenter presenter;

    @Mock
    private PncProfileOverviewFragmentContract.View view;

    @Mock
    private PncLibrary pncLibrary;

    private PncProfileOverviewFragmentContract.Model model;

    @Before
    public void setUp() throws Exception {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        presenter = Mockito.spy(new PncProfileOverviewFragmentPresenter(view));
        PncProfileOverviewFragmentContract.Model model = ReflectionHelpers.getField(presenter, "model");
        this.model = Mockito.spy(model);
        ReflectionHelpers.setField(presenter, "model", this.model);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void loadOverviewFactsShouldCallModelFetchLastCheckAndVisit() {
        Mockito.doNothing().when(model).fetchPncOverviewDetails(Mockito.eq("bei"), Mockito.any(PncProfileOverviewFragmentContract.Model.OnFetchedCallback.class));

        presenter.loadOverviewFacts("bei", Mockito.mock(PncProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class));
        Mockito.verify(model, Mockito.times(1))
                .fetchPncOverviewDetails(Mockito.eq("bei"), Mockito.any(PncProfileOverviewFragmentContract.Model.OnFetchedCallback.class));
    }
}
