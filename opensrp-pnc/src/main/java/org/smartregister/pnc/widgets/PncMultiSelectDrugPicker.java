package org.smartregister.pnc.widgets;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.R;
import org.smartregister.pnc.utils.PncConstants;

import timber.log.Timber;

public class PncMultiSelectDrugPicker extends MultiSelectListFactory implements TextWatcher {

    private Button saveDrugMaternityBtn;

    @Override
    protected void handleClickEventOnListData(@NonNull MultiSelectItem multiSelectItem) {
        createAdditionalDetailsDialog(multiSelectItem);
    }

    private void createAdditionalDetailsDialog(@NonNull final MultiSelectItem multiSelectItem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dosage_and_duration_layout, null);
        ImageView imgClose = view.findViewById(R.id.multiSelectListCloseDialog);
        saveDrugMaternityBtn = view.findViewById(R.id.maternity_btn_save_drug);

        TextView txtSelectedItemInMultiSelectList = view.findViewById(R.id.txtSelectedItemInMultiSelectList);
        txtSelectedItemInMultiSelectList.setText(multiSelectItem.getText());
        final EditText edtTreatmentDuration = view.findViewById(R.id.edtTreatmentDuration);
        final EditText edtTreatmentDosage = view.findViewById(R.id.edtTreatmentDosage);
        edtTreatmentDosage.addTextChangedListener(this);
        edtTreatmentDuration.addTextChangedListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialogStyle);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

        saveDrugMaternityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editableTreatmentDuration = edtTreatmentDuration.getText();
                Editable editableTreatmentDosage = edtTreatmentDosage.getText();

                String duration = "";
                if (editableTreatmentDuration != null) {
                    duration = editableTreatmentDuration.toString();
                }

                String dosage = "";
                if (editableTreatmentDuration != null) {
                    dosage = editableTreatmentDosage.toString();
                }

                writeAdditionalDetails(duration, dosage, multiSelectItem);

                writeToForm();

                alertDialog.dismiss();

                getAlertDialog().dismiss();
            }
        });
    }

    private void writeAdditionalDetails(@NonNull String duration, @NonNull String dosage, @NonNull MultiSelectItem multiSelectItem) {
        String multiSelectValue = multiSelectItem.getValue();
        String msg = String.format(context.getString(R.string.maternity_dosage_duration_text), dosage, duration);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = new JSONObject(multiSelectValue);
            JSONObject jsonAdditionalObject = new JSONObject();
            jsonAdditionalObject.put(PncConstants.JsonFormKeyConstants.DURATION, duration);
            jsonAdditionalObject.put(PncConstants.JsonFormKeyConstants.DOSAGE, dosage);
            jsonAdditionalObject.put(JsonFormConstants.MultiSelectUtils.INFO, msg);
            jsonObject.put(JsonFormConstants.MultiSelectUtils.META, jsonAdditionalObject);
        } catch (JSONException e) {
            Timber.e(e);
        }

        multiSelectItem.setValue(jsonObject.toString());
        updateSelectedData(multiSelectItem, false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().isEmpty()) {
            if (saveDrugMaternityBtn != null) {
                saveDrugMaternityBtn.setTextColor(context.getResources().getColor(R.color.primary_text));
            }
        } else {
            if (saveDrugMaternityBtn != null) {
                saveDrugMaternityBtn.setTextColor(context.getResources().getColor(R.color.light_grey));
            }
        }
    }

}
