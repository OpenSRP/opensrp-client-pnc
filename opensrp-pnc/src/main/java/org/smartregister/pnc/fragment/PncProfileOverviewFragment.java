package org.smartregister.pnc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.R;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.adapter.PncProfileOverviewAdapter;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.listener.OnSendActionToFragment;
import org.smartregister.pnc.presenter.PncProfileOverviewFragmentPresenter;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.fragment.BaseProfileFragment;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileOverviewFragment extends BaseProfileFragment implements PncProfileOverviewFragmentContract.View, OnSendActionToFragment {

    private String baseEntityId;
    private PncProfileOverviewFragmentContract.Presenter presenter;
    private CommonPersonObjectClient commonPersonObjectClient;

    private LinearLayout pncOutcomeSectionLayout;
    private Button recordOutcomeBtn;

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
        presenter = new PncProfileOverviewFragmentPresenter(this);

        if (getArguments() != null) {
            commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(PncConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                presenter.setClient(commonPersonObjectClient);
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    protected void onResumption() {
        if (baseEntityId != null) {
            presenter.loadOverviewFacts(baseEntityId, (facts, yamlConfigListGlobal) -> {
                if (getActivity() != null && facts != null && yamlConfigListGlobal != null) {
                    showOutcomeBtn();

                    PncProfileOverviewAdapter adapter = new PncProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
                    adapter.notifyDataSetChanged();

                    // set up the RecyclerView
                    RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }

    private void showOutcomeBtn() {
        if (getActivity() != null) {
            PncUtils.setVisitButtonStatus(recordOutcomeBtn, baseEntityId);
            pncOutcomeSectionLayout.setVisibility(View.VISIBLE);
            recordOutcomeBtn.setOnClickListener(v -> {
                Object buttonType = v.getTag(R.id.BUTTON_TYPE);
                if (buttonType != null) {
                    BasePncProfileActivity profileActivity = (BasePncProfileActivity) getActivity();
                    if (buttonType.equals(R.string.pnc_due) || buttonType.equals(R.string.pnc_overdue) || buttonType.equals(R.string.record_pnc)) {
                        profileActivity.performPatientAction(commonPersonObjectClient, PncConstants.Form.PNC_VISIT);
                    }
                    else if(buttonType.equals(R.string.pnc_close)) {
                        //profileActivity.performPatientAction((CommonPersonObjectClient) viewClient, PncConstants.Form.PNC_CLOSE);
                    }
                    else if (buttonType.equals(R.string.start_pnc)){
                        profileActivity.performPatientAction(commonPersonObjectClient, PncConstants.Form.PNC_OUTCOME);
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.pnc_fragment_profile_overview, container, false);
        pncOutcomeSectionLayout = view.findViewById(R.id.ll_pncFragmentProfileOverview_outcomeLayout);
        recordOutcomeBtn = view.findViewById(R.id.btn_pncFragmentProfileOverview_outcome);
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