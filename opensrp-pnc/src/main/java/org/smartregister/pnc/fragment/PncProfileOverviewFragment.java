package org.smartregister.pnc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileOverviewFragment extends BaseProfileFragment implements PncProfileOverviewFragmentContract.View, OnSendActionToFragment {

    private String baseEntityId;
    private PncProfileOverviewFragmentContract.Presenter presenter;
    private CommonPersonObjectClient commonPersonObjectClient;

    private LinearLayout pncProfileOverviewLayout;
    private Button actionBtn;

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
            presenter.loadOverviewFacts(baseEntityId, ((facts, items) -> {
                if (getActivity() != null) {

                    populateActionButtonRefreshFields(facts.asMap());

                    showRecordFormBtn();

                    PncProfileOverviewAdapter adapter = new PncProfileOverviewAdapter(getActivity(), items);
                    adapter.notifyDataSetChanged();

                    // set up the RecyclerView
                    RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                }
            }));
        }
    }

    protected void populateActionButtonRefreshFields(Map<String, Object> factsObjectMap) {
        if (commonPersonObjectClient != null) {
            commonPersonObjectClient.getColumnmaps().put(PncConstants.JsonFormKeyConstants.PMI_BASE_ENTITY_ID, (String) factsObjectMap.get(PncConstants.JsonFormKeyConstants.PMI_BASE_ENTITY_ID));
            commonPersonObjectClient.getColumnmaps().put(PncConstants.KeyConstants.PPF_ID, (String) factsObjectMap.get(PncConstants.KeyConstants.PPF_ID));
            commonPersonObjectClient.getColumnmaps().put(PncConstants.KeyConstants.PPF_FORM_TYPE, (String) factsObjectMap.get(PncConstants.KeyConstants.PPF_FORM_TYPE));
            commonPersonObjectClient.getColumnmaps().put(PncDbConstants.Column.PncVisitInfo.LATEST_VISIT_DATE, (String) factsObjectMap.get(PncDbConstants.Column.PncVisitInfo.LATEST_VISIT_DATE));
            commonPersonObjectClient.getColumnmaps().put(PncConstants.FormGlobalConstants.DELIVERY_DATE, (String) factsObjectMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE));
        }
    }

    private void showRecordFormBtn() {
        if (getActivity() != null) {
            updateActionButtonStatus();
            pncProfileOverviewLayout.setVisibility(View.VISIBLE);
            actionBtn.setOnClickListener(v -> {
                Object buttonType = v.getTag(R.id.BUTTON_TYPE);
                if (buttonType != null) {
                    BasePncProfileActivity profileActivity = (BasePncProfileActivity) getActivity();
                    if (buttonType.equals(R.string.pnc_due) || buttonType.equals(R.string.pnc_overdue) || buttonType.equals(R.string.record_pnc)) {
                        profileActivity.openPncVisitForm();
                    } else if (buttonType.equals(R.string.complete_pnc_registration)) {
                        profileActivity.openPncMedicInfoForm();
                    }
                }
            });
        }
    }

    protected void updateActionButtonStatus() {
        PncUtils.setVisitButtonStatus(actionBtn, commonPersonObjectClient);
        actionBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBtn.getResources().getDimension(R.dimen.pnc_profile_action_button_text_size));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pnc_fragment_profile_overview, container, false);
        pncProfileOverviewLayout = view.findViewById(R.id.ll_pncFragmentProfileOverview);
        actionBtn = view.findViewById(R.id.btn_pncFragmentProfileOverview);
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