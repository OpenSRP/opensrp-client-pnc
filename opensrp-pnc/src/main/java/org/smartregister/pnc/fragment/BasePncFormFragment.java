package org.smartregister.pnc.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.event.Listener;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.adapter.ClientLookUpListAdapter;
import org.smartregister.pnc.interactor.PncFormInteractor;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.presenter.PncFormFragmentPresenter;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.HashMap;
import java.util.List;

public class BasePncFormFragment extends JsonWizardFormFragment implements ClientLookUpListAdapter.ClickListener {

    private Snackbar snackbar = null;
    private AlertDialog alertDialog = null;
    private final Listener<List<CommonPersonObject>> lookUpListener =
            new Listener<List<CommonPersonObject>>() {
                @Override
                public void onEvent(List<CommonPersonObject> data) {
                    showClientLookUp(data);
                }
            };
    private PncFormFragmentPresenter presenter;

    public static JsonWizardFormFragment getFormFragment(String stepName) {
        BasePncFormFragment jsonFormFragment = new BasePncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Nullable
    private Form getForm() {
        return this.getActivity() != null && this.getActivity() instanceof JsonFormActivity ?
                ((JsonFormActivity) this.getActivity()).getForm() : null;
    }

    public PncFormFragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        super.updateVisibilityOfNextAndSave(next, save);
        Form form = getForm();
        PncMetadata maternityMetadata = PncLibrary.getInstance().getPncConfiguration().getMaternityMetadata();

        if (form != null && form.isWizard() && maternityMetadata != null
                && !maternityMetadata.isFormWizardValidateRequiredFieldsBefore()) {
            this.getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        }
    }

    private void showClientLookUp(List<CommonPersonObject> data) {
        if (!data.isEmpty()) {
            showInfoSnackBar(data);
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    private void showInfoSnackBar(final List<CommonPersonObject> data) {
        snackbar = Snackbar.make(getMainView(), getActivity().getString(R.string.client_matches, data.size()),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.tap_to_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResultDialog(data);
            }
        });
        show(snackbar, 30000);
    }

    private void updateResultDialog(final List<CommonPersonObject> data) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.maternity_lookup_results, null);
        RecyclerView recyclerView = view.findViewById(R.id.maternity_lookup_recyclerview);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaternityDialog);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setCancelable(true);
        alertDialog = builder.create();
        //
        setUpDialog(recyclerView, data);
        //
        alertDialog.show();
    }

    protected void setUpDialog(RecyclerView recyclerView, List<CommonPersonObject> data) {
        ClientLookUpListAdapter clientLookUpListAdapter = new ClientLookUpListAdapter(data, getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(clientLookUpListAdapter);
        clientLookUpListAdapter.notifyDataSetChanged();
        clientLookUpListAdapter.setOnClickListener(this);
    }

    protected void show(final Snackbar snackbar, int duration) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.accent);

        final Button actionView = snackbarView.findViewById(R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.white));

        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, duration);

    }

    public Listener<List<CommonPersonObject>> lookUpListener() {
        return lookUpListener;
    }

    @Override
    public void onItemClick(View view) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            CommonPersonObjectClient client = null;
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) view.getTag();
            }

            if (client != null) {
                startActivityOnLookUp(client);
            }
        }
    }

    protected void startActivityOnLookUp(@NonNull CommonPersonObjectClient client) {
        Intent intent = new Intent(getActivity(), PncLibrary.getInstance().getPncConfiguration().getMaternityMetadata().getProfileActivity());

        // Add register_id FROM opensrp_id
        String opensrpId = client.getColumnmaps().get(PncDbConstants.Column.Client.OPENSRP_ID);
        client.getColumnmaps().put(PncConstants.ColumnMapKey.REGISTER_ID, opensrpId);
        client.getDetails().put(PncConstants.ColumnMapKey.REGISTER_ID, opensrpId);

        intent.putExtra(PncConstants.IntentKey.CLIENT_OBJECT, client);
        startActivity(intent);
    }

    @Override
    public void finishWithResult(Intent returnIntent) {
        Activity activity = getActivity();

        if (activity instanceof BasePncFormActivity) {
            BasePncFormActivity maternityFormActivity = (BasePncFormActivity) activity;

            HashMap<String, String> parcelableData = maternityFormActivity.getParcelableData();

            for (String key : parcelableData.keySet()) {
                String value = parcelableData.get(key);

                if (value != null) {
                    returnIntent.putExtra(key, value);
                }
            }
        }

        if (activity != null) {
            activity.setResult(Activity.RESULT_OK, returnIntent);
            activity.finish();
        }
    }

    @Override
    protected JsonWizardFormFragmentPresenter createPresenter() {
        presenter = new PncFormFragmentPresenter(this, PncFormInteractor.getInstance());
        return presenter;
    }
}
