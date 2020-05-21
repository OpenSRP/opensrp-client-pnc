package org.smartregister.pnc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.R;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.adapter.PncProfileOverviewAdapter;
import org.smartregister.pnc.contract.PncProfileOverviewFragmentContract;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.listener.OnSendActionToFragment;
import org.smartregister.pnc.presenter.PncProfileOverviewFragmentPresenter;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.List;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileOverviewFragment extends BaseProfileFragment implements PncProfileOverviewFragmentContract.View, OnSendActionToFragment {

    private String baseEntityId;
    private PncProfileOverviewFragmentContract.Presenter presenter;

    private LinearLayout maternityOutcomeSectionLayout;
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
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
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
            presenter.loadOverviewFacts(baseEntityId, new PncProfileOverviewFragmentContract.Presenter.OnFinishedCallback() {

                @Override
                public void onFinished(@Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal) {
                    if (getActivity() != null && facts != null && yamlConfigListGlobal != null) {
                        showOutcomeBtn();

                        PncProfileOverviewAdapter adapter = new PncProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
                        adapter.notifyDataSetChanged();

                        // set up the RecyclerView
                        RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);
                    }
                }

            });
        }
    }

    private void showOutcomeBtn() {
        if (getActivity() != null) {
            maternityOutcomeSectionLayout.setVisibility(View.VISIBLE);
            recordOutcomeBtn.setText(R.string.outcome);
            recordOutcomeBtn.setBackgroundResource(R.drawable.outcome_btn_overview_bg);
            recordOutcomeBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.check_in_btn_overview_text_color));
            recordOutcomeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();

                    if (activity instanceof BasePncProfileActivity) {
                        ((BasePncProfileActivity) activity).openMaternityOutcomeForm();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.maternity_fragment_profile_overview, container, false);

        maternityOutcomeSectionLayout = view.findViewById(R.id.ll_maternityFragmentProfileOverview_outcomeLayout);
        recordOutcomeBtn = view.findViewById(R.id.btn_maternityFragmentProfileOverview_outcome);

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