package org.smartregister.pnc.provider;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.BasePncRegisterProviderMetadata;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;
import org.smartregister.pnc.config.PncRegisterRowOptions;
import org.smartregister.pnc.holder.FooterViewHolder;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.pnc.repository.PncPartialFormRepository;
import org.smartregister.pnc.repository.PncRegistrationDetailsRepository;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class PncRegisterProviderTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PncRegisterProvider pncRegisterProvider;

    @Mock
    private Context context;

    @Mock
    private View.OnClickListener onClickListener;

    @Mock
    private View.OnClickListener paginationClickListener;

    @Mock
    private View mockedView;

    @Mock
    private LayoutInflater inflator;

    @Mock
    private Resources resources;

    @Mock
    private PncLibrary pncLibrary;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        BasePncRegisterProviderMetadata pncRegisterProviderMetadata = Mockito.spy(new BasePncRegisterProviderMetadata());
        Mockito.doReturn(mockedView).when(inflator).inflate(Mockito.anyInt(), Mockito.any(ViewGroup.class), Mockito.anyBoolean());
        Mockito.doReturn(inflator).when(context).getSystemService(Mockito.eq(Context.LAYOUT_INFLATER_SERVICE));
        PowerMockito.doReturn(resources).when(context).getResources();

        PncRegisterRowOptions<? extends PncRegisterViewHolder> pncRegisterRowOption = mock(PncRegisterRowOptions.class);

        PncConfiguration pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProvider.class)
                .setPncRegisterProviderMetadata(BasePncRegisterProviderMetadata.class)
                .setPncRegisterRowOptions(pncRegisterRowOption.getClass())
                .build();

        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();

        ReflectionHelpers.setStaticField(PncLibrary.class,"instance", pncLibrary);

        pncRegisterProvider = new PncRegisterProvider(context, onClickListener, paginationClickListener);
        ReflectionHelpers.setField(pncRegisterProvider, "pncRegisterProviderMetadata", pncRegisterProviderMetadata);

        PncRegistrationDetailsRepository pncRegistrationDetailsRepository = mock(PncRegistrationDetailsRepository.class);
        ReflectionHelpers.setField(PncLibrary.getInstance(), "pncRegistrationDetailsRepository", pncRegistrationDetailsRepository);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void verifyFillValue() {

        TextView textView = mock(TextView.class);
        PowerMockito.doNothing().when(textView).setText(anyString());

        PncRegisterProvider.fillValue(textView, "value");

        verify(textView, times(1)).setText("value");

    }

    // TODO: Fix this test
    /*@Test
    public void populatePatientColumnShouldCallProviderMetadataForDataValues() {
        CoreLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(SyncConfiguration.class));
        //PowerMockito.mockStatic(Utils.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        Mockito.doReturn("2016-07-24T03:00:00.000+03:00")
                .when(pncRegisterProviderMetadata)
                .getDob(Mockito.any(Map.class));
        //PowerMockito.when(Utils.getDuration("2016-07-24T03:00:00.000+03:00")).thenReturn("3y 4m");
        Resources resources = Mockito.mock(Resources.class);
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn("CG").when(resources).getString(R.string.care_giver_initials);
        Mockito.doReturn("y").when(resources).getString(R.string.abbrv_years);
        Mockito.doReturn("Age: %s").when(resources).getString(R.string.patient_age_holder);
        PncRegisterViewHolder viewHolder = Mockito.mock(PncRegisterViewHolder.class);
        viewHolder.patientColumn = Mockito.mock(View.class);
        viewHolder.dueButton = Mockito.mock(Button.class);
        pncRegisterProvider.populatePatientColumn(client, viewHolder);
        Mockito.verify(pncRegisterProviderMetadata, Mockito.times(1))
                .getClientFirstName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(pncRegisterProviderMetadata, Mockito.times(1))
                .getClientMiddleName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(pncRegisterProviderMetadata, Mockito.times(1))
                .getClientLastName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(pncRegisterProviderMetadata, Mockito.times(1))
                .getDob(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(pncRegisterProviderMetadata, Mockito.times(1))
                .getGA(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(pncRegisterProviderMetadata, Mockito.times(1))
                .getPatientID(Mockito.eq(client.getColumnmaps()));
    }*/

    @Test
    public void createViewHolderShouldUseCustomViewHolderinRowOptions() {
        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(pncRegisterProvider, "pncRegisterRowOptions", rowOptions);
        Mockito.doReturn(true).when(rowOptions).isCustomViewHolder();

        pncRegisterProvider.createViewHolder(Mockito.mock(ViewGroup.class));

        verify(rowOptions, times(1)).createCustomViewHolder(Mockito.any(View.class));
    }

    @Test
    public void createViewHolderShouldUseCustomLayoutIdProvided() {
        int layoutId = 49834;

        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(pncRegisterProvider, "pncRegisterRowOptions", rowOptions);
        Mockito.doReturn(true).when(rowOptions).useCustomViewLayout();
        Mockito.doReturn(layoutId).when(rowOptions).getCustomViewLayoutId();

        pncRegisterProvider.createViewHolder(Mockito.mock(ViewGroup.class));

        verify(rowOptions, times(2)).getCustomViewLayoutId();
        verify(inflator, times(1)).inflate(Mockito.eq(layoutId), Mockito.any(ViewGroup.class), Mockito.anyBoolean());
    }

    @Test
    public void getViewShouldCallRowOptionsPopulateClientRowWhenDefaultCustomImplementationIsProvided() {
        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(pncRegisterProvider, "pncRegisterRowOptions", rowOptions);

        Mockito.doReturn(true).when(rowOptions).isDefaultPopulatePatientColumn();

        pncRegisterProvider.getView(Mockito.mock(Cursor.class)
                , Mockito.mock(CommonPersonObjectClient.class)
                , Mockito.mock(PncRegisterViewHolder.class));

        verify(rowOptions, times(1)).populateClientRow(
                Mockito.any(Cursor.class)
                , Mockito.any(CommonPersonObjectClient.class)
                , Mockito.any(SmartRegisterClient.class)
                , Mockito.any(PncRegisterViewHolder.class));
    }

    @Test
    public void getViewShouldCallPopulatePatientColumn() {

        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(pncRegisterProvider, "pncRegisterRowOptions", rowOptions);
        PncRegisterViewHolder pncRegisterViewHolder = Mockito.mock(PncRegisterViewHolder.class);
        CommonPersonObjectClient commonPersonObjectClient = mock(CommonPersonObjectClient.class);
        Cursor cursor = mock(Cursor.class);
        View view = mock(View.class);
        Button button = mock(Button.class);
        View.OnClickListener onClickListener = mock(View.OnClickListener.class);

        PowerMockito.doReturn("y").when(resources).getString(anyInt());
        PowerMockito.doReturn("Age: %s").when(context).getString(anyInt());
        ReflectionHelpers.setField(pncRegisterViewHolder, "patientColumn", view);
        ReflectionHelpers.setField(pncRegisterViewHolder, "dueButton", button);
        ReflectionHelpers.setField(pncRegisterProvider, "onClickListener", onClickListener);

        PncPartialFormRepository pncPartialFormRepository = mock(PncPartialFormRepository.class);
        ReflectionHelpers.setField(PncLibrary.getInstance(), "pncPartialFormRepository", pncPartialFormRepository);

        pncRegisterProvider.getView(cursor
                , commonPersonObjectClient
                , pncRegisterViewHolder);

        verify(rowOptions, times(1)).populateClientRow(cursor, commonPersonObjectClient, commonPersonObjectClient, pncRegisterViewHolder);
    }

    @Test
    public void getFooterViewShouldVerifyClickListenerAndShouldVisiblePreviousAndNextPageView() {

        FooterViewHolder footerViewHolder = mock(FooterViewHolder.class);
        TextView pageInfoView = mock(TextView.class);
        Button nextPageView = mock(Button.class);
        Button previousPageView = mock(Button.class);
        View.OnClickListener paginationClickListener = mock(View.OnClickListener.class);

        ReflectionHelpers.setField(footerViewHolder, "pageInfoView", pageInfoView);
        ReflectionHelpers.setField(footerViewHolder, "nextPageView", nextPageView);
        ReflectionHelpers.setField(footerViewHolder, "previousPageView", previousPageView);
        ReflectionHelpers.setField(pncRegisterProvider, "paginationClickListener", paginationClickListener);

        PowerMockito.doReturn("Page {0} of {1}").when(context).getString(anyInt());


        pncRegisterProvider.getFooterView(footerViewHolder, 1, 10, true, true);

        verify(pageInfoView, times(1)).setText("Page 1 of 10");
        verify(nextPageView, times(1)).setVisibility(View.VISIBLE);
        verify(previousPageView, times(1)).setVisibility(View.VISIBLE);
        verify(nextPageView, times(1)).setOnClickListener(paginationClickListener);
        verify(previousPageView, times(1)).setOnClickListener(paginationClickListener);
    }

    @Test
    public void getFooterShouldInvisiblePreviousAndNextPageView() {

        FooterViewHolder footerViewHolder = mock(FooterViewHolder.class);
        TextView pageInfoView = mock(TextView.class);
        Button nextPageView = mock(Button.class);
        Button previousPageView = mock(Button.class);
        View.OnClickListener paginationClickListener = mock(View.OnClickListener.class);

        ReflectionHelpers.setField(footerViewHolder, "pageInfoView", pageInfoView);
        ReflectionHelpers.setField(footerViewHolder, "nextPageView", nextPageView);
        ReflectionHelpers.setField(footerViewHolder, "previousPageView", previousPageView);
        ReflectionHelpers.setField(pncRegisterProvider, "paginationClickListener", paginationClickListener);

        PowerMockito.doReturn("Page {0} of {1}").when(context).getString(anyInt());

        pncRegisterProvider.getFooterView(footerViewHolder, 1, 10, false, false);

        verify(nextPageView, times(1)).setVisibility(View.INVISIBLE);
        verify(previousPageView, times(1)).setVisibility(View.INVISIBLE);
    }

    @Test
    public void updateClientsShouldReturnNull() {
        SmartRegisterClients client = pncRegisterProvider.updateClients(
                mock(FilterOption.class),
                mock(ServiceModeOption.class),
                mock(FilterOption.class),
                mock(SortOption.class)
        );

        Assert.assertNull(client);
    }

    @Test
    public void newFormLauncherShouldReturnNull() {
        OnClickFormLauncher formLauncher = pncRegisterProvider.newFormLauncher("", "", "");
        Assert.assertNull(formLauncher);
    }

    @Test
    public void inflaterShouldReturnMockedInflater() {
        LayoutInflater layoutInflater = pncRegisterProvider.inflater();
        Assert.assertEquals(inflator, layoutInflater);
    }

    @Test
    public void createFooterHolderShouldReturnFooterViewHolder() throws Exception {

        ViewGroup parent = mock(ViewGroup.class);
        View view = mock(View.class);

        PowerMockito.doReturn(view).when(inflator).inflate(anyInt(), any(ViewGroup.class), anyBoolean());

        RecyclerView.ViewHolder footerViewHolder = pncRegisterProvider.createFooterHolder(parent);

        Assert.assertThat(footerViewHolder, instanceOf(RecyclerView.ViewHolder.class));
    }

    @Test
    public void isFooterViewHolderShouldReturnTrue() {
        FooterViewHolder footerViewHolder = mock(FooterViewHolder.class);
        boolean result = pncRegisterProvider.isFooterViewHolder(footerViewHolder);
        Assert.assertTrue(result);
    }

    static class PncRegisterQueryProvider extends PncRegisterQueryProviderContract {

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
