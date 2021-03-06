package org.smartregister.pnc.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentHostCallback;
import android.view.View;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.BuildConfig;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.config.BasePncRegisterProviderMetadata;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.config.PncRegisterQueryProviderContract;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.repository.Repository;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(MockitoJUnitRunner.class)
public class BasePncFormFragmentTest {

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void startActivityOnLookUpShouldCallStartActivity() {
        PncConfiguration pncConfiguration = new PncConfiguration.Builder(PncRegisterQueryProvider.class)
                .setPncRegisterProviderMetadata(BasePncRegisterProviderMetadata.class)
                .setPncMetadata(new PncMetadata("form-name"
                        , "table-name"
                        , "register-event-type"
                        , "update-event-type"
                        , "config"
                        , BasePncFormActivity.class
                        , BasePncProfileActivity.class
                        , false))
                .build();

        PncLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), pncConfiguration, BuildConfig.VERSION_CODE, 1);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        BasePncFormFragment basePncFormFragment = new BasePncFormFragment();

        FragmentHostCallback host = Mockito.mock(FragmentHostCallback.class);

        ReflectionHelpers.setField(basePncFormFragment, "mHost", host);
        basePncFormFragment.startActivityOnLookUp(client);

        Mockito.verify(host, Mockito.times(1))
                .onStartActivityFromFragment(Mockito.any(Fragment.class)
                        , Mockito.any(Intent.class)
                        , Mockito.eq(-1)
                        , Mockito.nullable(Bundle.class));
    }

    @Test
    public void onItemClickShouldCallStartActivityOnLookupWithTheCorrectClient() {
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        BasePncFormFragment basePncFormFragment = Mockito.spy(new BasePncFormFragment());
        Mockito.doNothing().when(basePncFormFragment).startActivityOnLookUp(Mockito.any(CommonPersonObjectClient.class));

        AlertDialog alertDialog = Mockito.mock(AlertDialog.class);
        Mockito.doReturn(true).when(alertDialog).isShowing();
        Mockito.doNothing().when(alertDialog).dismiss();
        ReflectionHelpers.setField(basePncFormFragment, "alertDialog", alertDialog);

        View clickedView = Mockito.mock(View.class);
        Mockito.doReturn(client).when(clickedView).getTag();

        // The actual method call
        basePncFormFragment.onItemClick(clickedView);

        // Verification
        Mockito.verify(basePncFormFragment, Mockito.times(1))
                .startActivityOnLookUp(Mockito.eq(client));
    }

    public static class PncRegisterQueryProvider extends PncRegisterQueryProviderContract {

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