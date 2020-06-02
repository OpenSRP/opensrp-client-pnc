package org.smartregister.pnc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.R;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.listener.OnSendActionToFragment;
import org.smartregister.pnc.presenter.PncProfileOverviewFragmentPresenter;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.view.fragment.BaseProfileFragment;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileOverviewFragment extends BaseProfileFragment implements PncProfileOverviewFragmentContract.View, OnSendActionToFragment {

    public static PncProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        PncProfileOverviewFragment fragment = new PncProfileOverviewFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreation() {
        PncProfileOverviewFragmentContract.Presenter presenter = new PncProfileOverviewFragmentPresenter(this);

        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(PncConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                presenter.setClient(commonPersonObjectClient);
            }
        }
    }

    @Override
    protected void onResumption() {
        // do nothing
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.pnc_fragment_profile_overview, container, false);
        return view;
    }

    @Override
    public void onActionReceive() {
        onResumption();
    }

    @Override
    @Nullable
    public CommonPersonObjectClient getActivityClientMap() {
        if (getActivity() instanceof BasePncProfileActivity) {
            return ((BasePncProfileActivity) getActivity()).getClient();
        }

        return null;
    }
}