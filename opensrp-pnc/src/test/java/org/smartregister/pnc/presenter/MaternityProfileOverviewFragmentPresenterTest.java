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

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
@RunWith(RobolectricTestRunner.class)
public class MaternityProfileOverviewFragmentPresenterTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PncProfileOverviewFragmentPresenter presenter;

    @Mock
    private PncProfileOverviewFragmentContract.View view;

    private PncProfileOverviewFragmentContract.Model model;

    @Before
    public void setUp() throws Exception {
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
        Mockito.doNothing().when(model).fetchMaternityOverviewDetails(Mockito.eq("bei"), Mockito.any(PncProfileOverviewFragmentContract.Model.OnFetchedCallback.class));

        presenter.loadOverviewFacts("bei", Mockito.mock(PncProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class));
        Mockito.verify(model, Mockito.times(1))
                .fetchMaternityOverviewDetails(Mockito.eq("bei"), Mockito.any(PncProfileOverviewFragmentContract.Model.OnFetchedCallback.class));
    }

    /*@Test
    public void loadOverviewFactsShouldCallLoadOverViewDataAndDisplayWhenModelCallIsSuccessful() {

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                MaternityProfileOverviewFragmentContract.Model.OnFetchedCallback onFetchedCallback = invocationOnMock.getArgument(1);
                onFetchedCallback.onFetched(Mockito.mock(OpdCheckIn.class), Mockito.mock(OpdVisit.class), Mockito.mock(MaternityDetails.class));
                return null;
            }
        }).when(model).fetchPregnancyDataAndHivStatus(Mockito.eq("bei"), Mockito.any(MaternityProfileOverviewFragmentContract.Model.OnFetchedCallback.class));
        Mockito.doNothing().when(presenter).loadOverviewDataAndDisplay(Mockito.any(OpdCheckIn.class), Mockito.any(OpdVisit.class), Mockito.any(MaternityDetails.class), Mockito.any(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class));

        presenter.loadOverviewFacts("bei", Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class));

        Mockito.verify(presenter, Mockito.times(1))
                .loadOverviewDataAndDisplay(Mockito.any(OpdCheckIn.class), Mockito.any(OpdVisit.class), Mockito.any(MaternityDetails.class), Mockito.any(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class));
    }
*/
    /*@Test
    public void loadOverviewDataAndDisplayShouldLoadHivUnknownForMaleWithoutCheckInOrVisits() {
        MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class);
        ArgumentCaptor<Facts> callbackArgumentCaptor = ArgumentCaptor.forClass(Facts.class);
        ArgumentCaptor<List<YamlConfigWrapper>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "Male");
        CommonPersonObjectClient client = new CommonPersonObjectClient("id", details, "John Doe");
        client.setColumnmaps(details);

        Context mockContext = Mockito.mock(Context.class);
        Mockito.doReturn(RuntimeEnvironment.systemContext).when(mockContext).applicationContext();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return RuntimeEnvironment.application.getString((int) invocationOnMock.getArgument(0));
            }
        }).when(view).getString(Mockito.anyInt());
        MaternityLibrary.init(mockContext, Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);
        presenter.setClient(client);
        presenter.loadOverviewDataAndDisplay(null, null, null, onFinishedCallback);
        Mockito.verify(onFinishedCallback, Mockito.times(1)).onFinished(callbackArgumentCaptor.capture(), listArgumentCaptor.capture());

        assertEquals(2, callbackArgumentCaptor.getValue().asMap().size());
        assertEquals("Unknown", callbackArgumentCaptor.getValue().get("hiv_status"));
        assertEquals(false, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PENDING_DIAGNOSE_AND_TREAT));
    }*/


    /*@Test
    public void loadOverviewDataAndDisplayShouldLoadPregnancyStatusUnknownForFemaleWithoutCheckInOrVisits() {
        MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class);
        ArgumentCaptor<Facts> callbackArgumentCaptor = ArgumentCaptor.forClass(Facts.class);
        ArgumentCaptor<List<YamlConfigWrapper>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "Female");
        CommonPersonObjectClient client = new CommonPersonObjectClient("id", details, "Jane Doe");
        client.setColumnmaps(details);

        Context mockContext = Mockito.mock(Context.class);
        Mockito.doReturn(RuntimeEnvironment.systemContext).when(mockContext).applicationContext();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return RuntimeEnvironment.application.getString((int) invocationOnMock.getArgument(0));
            }
        }).when(view).getString(Mockito.anyInt());
        MaternityLibrary.init(mockContext, Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);
        presenter.setClient(client);
        presenter.loadOverviewDataAndDisplay(null, onFinishedCallback);
        Mockito.verify(onFinishedCallback, Mockito.times(1)).onFinished(callbackArgumentCaptor.capture(), listArgumentCaptor.capture());

        assertEquals(2, callbackArgumentCaptor.getValue().asMap().size());
        assertEquals("Unknown", callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PREGNANCY_STATUS));
        assertEquals(false, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PENDING_DIAGNOSE_AND_TREAT));
    }*/


    /*@Test
    public void loadOverviewDataAndDisplayShouldLoadHivStatusAndPregnancyStatusForFemaleWithVisitsNotCheckedIn() {
        MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class);
        ArgumentCaptor<Facts> callbackArgumentCaptor = ArgumentCaptor.forClass(Facts.class);
        ArgumentCaptor<List<YamlConfigWrapper>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        String negative = "Negative";
        String hivResult = negative;
        String visitId = "visit-id";

        OpdCheckIn opdCheckIn = new OpdCheckIn();
        opdCheckIn.setPregnancyStatus(negative);
        opdCheckIn.setCurrentHivResult(hivResult);

        MaternityDetails maternityDetails = new MaternityDetails();
        maternityDetails.setPendingDiagnoseAndTreat(false);
        maternityDetails.setCurrentVisitStartDate(new Date());
        maternityDetails.setCurrentVisitEndDate(new Date());
        maternityDetails.setCurrentVisitId(visitId);

        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "Female");
        CommonPersonObjectClient client = new CommonPersonObjectClient("id", details, "Jane Doe");
        client.setColumnmaps(details);

        Context mockContext = Mockito.mock(Context.class);
        Mockito.doReturn(RuntimeEnvironment.systemContext).when(mockContext).applicationContext();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return RuntimeEnvironment.application.getString((int) invocationOnMock.getArgument(0));
            }
        }).when(view).getString(Mockito.anyInt());
        MaternityLibrary.init(mockContext, Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);
        presenter.setClient(client);
        presenter.loadOverviewDataAndDisplay(opdCheckIn, null, maternityDetails, onFinishedCallback);
        Mockito.verify(onFinishedCallback, Mockito.times(1)).onFinished(callbackArgumentCaptor.capture(), listArgumentCaptor.capture());

        assertEquals(3, callbackArgumentCaptor.getValue().asMap().size());
        assertEquals(negative, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PREGNANCY_STATUS));
        assertEquals(false, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PENDING_DIAGNOSE_AND_TREAT));
        assertEquals(hivResult, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.HIV_STATUS));
    }*/

    /*@Test
    public void loadOverviewDataAndDisplayShouldLoadPregnancystatusAndCurrentCheckDetailsForFemaleWithVisitsAndCheckedIn() {
        MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class);
        ArgumentCaptor<Facts> callbackArgumentCaptor = ArgumentCaptor.forClass(Facts.class);
        ArgumentCaptor<List<YamlConfigWrapper>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        String negative = "Negative";
        String hivResult = negative;
        String visitId = "visit-id";
        String visitType = "Revisit";
        String appointmentScheduled = "No";

        OpdVisit opdVisit = new OpdVisit();
        Date visitDate = new Date();
        opdVisit.setVisitDate(visitDate);
        opdVisit.setId(visitId);

        OpdCheckIn opdCheckIn = new OpdCheckIn();
        opdCheckIn.setPregnancyStatus(negative);
        opdCheckIn.setHasHivTestPreviously(negative);
        opdCheckIn.setHivResultsPreviously(negative);
        opdCheckIn.setCurrentHivResult(hivResult);
        opdCheckIn.setVisitType(visitType);
        opdCheckIn.setAppointmentScheduledPreviously(appointmentScheduled);

        MaternityDetails maternityDetails = new MaternityDetails();
        maternityDetails.setPendingDiagnoseAndTreat(false);
        maternityDetails.setCurrentVisitStartDate(visitDate);
        maternityDetails.setCurrentVisitId(visitId);

        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "Female");
        CommonPersonObjectClient client = new CommonPersonObjectClient("id", details, "Jane Doe");
        client.setColumnmaps(details);

        Context mockContext = Mockito.mock(Context.class);
        Mockito.doReturn(RuntimeEnvironment.systemContext).when(mockContext).applicationContext();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return RuntimeEnvironment.application.getString((int) invocationOnMock.getArgument(0));
            }
        }).when(view).getString(Mockito.anyInt());
        MaternityLibrary.init(mockContext, Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);
        presenter.setClient(client);
        presenter.loadOverviewDataAndDisplay(opdCheckIn, opdVisit, maternityDetails, onFinishedCallback);
        Mockito.verify(onFinishedCallback, Mockito.times(1)).onFinished(callbackArgumentCaptor.capture(), listArgumentCaptor.capture());

        assertEquals(7, callbackArgumentCaptor.getValue().asMap().size());
        assertEquals(true, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PENDING_DIAGNOSE_AND_TREAT));
        assertEquals(negative, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PREGNANCY_STATUS));
        assertEquals(negative, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.GRAVIDA));
        assertEquals(negative, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.GRAVIDA));
        assertEquals(hivResult, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.CURRENT_HIV_STATUS));
        assertEquals(visitType, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.VISIT_TYPE));
        assertEquals(appointmentScheduled, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.APPOINTMENT_SCHEDULED_PREVIOUSLY));
    }*/

    /*@Test
    public void loadOverviewDataAndDisplayShouldHivDetailsAndCurrentCheckDetailsForMaleWithVisitsAndCheckedIn() {
        MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class);
        ArgumentCaptor<Facts> callbackArgumentCaptor = ArgumentCaptor.forClass(Facts.class);
        ArgumentCaptor<List<YamlConfigWrapper>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        String negative = "Negative";
        String hivResult = negative;
        String visitId = "visit-id";
        String visitType = "Revisit";
        String appointmentScheduled = "No";

        OpdVisit opdVisit = new OpdVisit();
        Date visitDate = new Date();
        opdVisit.setVisitDate(visitDate);
        opdVisit.setId(visitId);

        OpdCheckIn opdCheckIn = new OpdCheckIn();
        opdCheckIn.setPregnancyStatus(negative);
        opdCheckIn.setHasHivTestPreviously(negative);
        opdCheckIn.setHivResultsPreviously(negative);
        opdCheckIn.setCurrentHivResult(hivResult);
        opdCheckIn.setVisitType(visitType);
        opdCheckIn.setAppointmentScheduledPreviously(appointmentScheduled);

        MaternityDetails maternityDetails = new MaternityDetails();
        maternityDetails.setPendingDiagnoseAndTreat(false);
        maternityDetails.setCurrentVisitStartDate(visitDate);
        maternityDetails.setCurrentVisitId(visitId);

        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "Male");
        CommonPersonObjectClient client = new CommonPersonObjectClient("id", details, "John Doe");
        client.setColumnmaps(details);

        Context mockContext = Mockito.mock(Context.class);
        Mockito.doReturn(RuntimeEnvironment.systemContext).when(mockContext).applicationContext();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return RuntimeEnvironment.application.getString((int) invocationOnMock.getArgument(0));
            }
        }).when(view).getString(Mockito.anyInt());

        MaternityLibrary.init(mockContext, Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);
        presenter.setClient(client);
        presenter.loadOverviewDataAndDisplay(opdCheckIn, opdVisit, maternityDetails, onFinishedCallback);
        Mockito.verify(onFinishedCallback, Mockito.times(1)).onFinished(callbackArgumentCaptor.capture(), listArgumentCaptor.capture());

        assertEquals(6, callbackArgumentCaptor.getValue().asMap().size());
        assertEquals(true, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.PENDING_DIAGNOSE_AND_TREAT));
        assertEquals(negative, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.GRAVIDA));
        assertEquals(negative, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.GRAVIDA));
        assertEquals(hivResult, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.CURRENT_HIV_STATUS));
        assertEquals(visitType, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.VISIT_TYPE));
        assertEquals(appointmentScheduled, callbackArgumentCaptor.getValue().get(MaternityConstants.FactKey.ProfileOverview.APPOINTMENT_SCHEDULED_PREVIOUSLY));
    }*/

}