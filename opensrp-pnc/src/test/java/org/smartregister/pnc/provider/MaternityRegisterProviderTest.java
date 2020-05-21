package org.smartregister.pnc.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.BuildConfig;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.BasePncRegisterProviderMetadata;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;
import org.smartregister.pnc.config.PncRegisterRowOptions;
import org.smartregister.pnc.fragment.BaseMaternityFormFragmentTest;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.repository.Repository;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
@RunWith(RobolectricTestRunner.class)
public class MaternityRegisterProviderTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PncRegisterProvider maternityRegisterProvider;

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

    @Before
    public void setUp() throws Exception {
        BasePncRegisterProviderMetadata maternityRegisterProviderMetadata = Mockito.spy(new BasePncRegisterProviderMetadata());
        Mockito.doReturn(mockedView).when(inflator).inflate(Mockito.anyInt(), Mockito.any(ViewGroup.class), Mockito.anyBoolean());
        Mockito.doReturn(inflator).when(context).getSystemService(Mockito.eq(Context.LAYOUT_INFLATER_SERVICE));

        PncConfiguration maternityConfiguration = new PncConfiguration.Builder(BaseMaternityFormFragmentTest.PncRegisterQueryProvider.class)
                .setMaternityRegisterProviderMetadata(BasePncRegisterProviderMetadata.class)
                .build();

        PncLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(Repository.class), maternityConfiguration, BuildConfig.VERSION_CODE, 1);

        maternityRegisterProvider = new PncRegisterProvider(context, onClickListener, paginationClickListener);
        ReflectionHelpers.setField(maternityRegisterProvider, "maternityRegisterProviderMetadata", maternityRegisterProviderMetadata);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    // TODO: Fix this test
    /*@Test
    public void populatePatientColumnShouldCallProviderMetadataForDataValues() {
        CoreLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(SyncConfiguration.class));
        //PowerMockito.mockStatic(Utils.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        Mockito.doReturn("2016-07-24T03:00:00.000+03:00")
                .when(maternityRegisterProviderMetadata)
                .getDob(Mockito.any(Map.class));
        //PowerMockito.when(Utils.getDuration("2016-07-24T03:00:00.000+03:00")).thenReturn("3y 4m");

        Resources resources = Mockito.mock(Resources.class);
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn("CG").when(resources).getString(R.string.care_giver_initials);
        Mockito.doReturn("y").when(resources).getString(R.string.abbrv_years);
        Mockito.doReturn("Age: %s").when(resources).getString(R.string.patient_age_holder);

        MaternityRegisterViewHolder viewHolder = Mockito.mock(MaternityRegisterViewHolder.class);
        viewHolder.patientColumn = Mockito.mock(View.class);
        viewHolder.dueButton = Mockito.mock(Button.class);

        maternityRegisterProvider.populatePatientColumn(client, viewHolder);

        Mockito.verify(maternityRegisterProviderMetadata, Mockito.times(1))
                .getClientFirstName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(maternityRegisterProviderMetadata, Mockito.times(1))
                .getClientMiddleName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(maternityRegisterProviderMetadata, Mockito.times(1))
                .getClientLastName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(maternityRegisterProviderMetadata, Mockito.times(1))
                .getDob(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(maternityRegisterProviderMetadata, Mockito.times(1))
                .getGA(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(maternityRegisterProviderMetadata, Mockito.times(1))
                .getPatientID(Mockito.eq(client.getColumnmaps()));
    }*/

    @Test
    public void createViewHolderShouldUseCustomViewHolderinRowOptions() {
        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(maternityRegisterProvider, "maternityRegisterRowOptions", rowOptions);
        Mockito.doReturn(true).when(rowOptions).isCustomViewHolder();

        maternityRegisterProvider.createViewHolder(Mockito.mock(ViewGroup.class));

        Mockito.verify(rowOptions, Mockito.times(1)).createCustomViewHolder(Mockito.any(View.class));
    }

    @Test
    public void createViewHolderShouldUseCustomLayoutIdProvided() {
        int layoutId = 49834;

        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(maternityRegisterProvider, "maternityRegisterRowOptions", rowOptions);
        Mockito.doReturn(true).when(rowOptions).useCustomViewLayout();
        Mockito.doReturn(layoutId).when(rowOptions).getCustomViewLayoutId();

        maternityRegisterProvider.createViewHolder(Mockito.mock(ViewGroup.class));

        Mockito.verify(rowOptions, Mockito.times(2)).getCustomViewLayoutId();
        Mockito.verify(inflator, Mockito.times(1)).inflate(Mockito.eq(layoutId), Mockito.any(ViewGroup.class), Mockito.anyBoolean());
    }

    @Test
    public void getViewShouldCallRowOptionsPopulateClientRowWhenDefaultCustomImplementationIsProvided() {
        PncRegisterRowOptions rowOptions = Mockito.mock(PncRegisterRowOptions.class);
        ReflectionHelpers.setField(maternityRegisterProvider, "maternityRegisterRowOptions", rowOptions);

        Mockito.doReturn(true).when(rowOptions).isDefaultPopulatePatientColumn();

        maternityRegisterProvider.getView(Mockito.mock(Cursor.class)
                , Mockito.mock(CommonPersonObjectClient.class)
                , Mockito.mock(PncRegisterViewHolder.class));

        Mockito.verify(rowOptions, Mockito.times(1)).populateClientRow(
                Mockito.any(Cursor.class)
                , Mockito.any(CommonPersonObjectClient.class)
                , Mockito.any(SmartRegisterClient.class)
                , Mockito.any(PncRegisterViewHolder.class));
    }

    static class MaternityRegisterQueryProvider extends PncRegisterQueryProviderContract {

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