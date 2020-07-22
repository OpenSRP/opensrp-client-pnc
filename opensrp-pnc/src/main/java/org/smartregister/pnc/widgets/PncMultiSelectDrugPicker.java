package org.smartregister.pnc.widgets;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.smartregister.pnc.R;

public class PncMultiSelectDrugPicker extends MultiSelectListFactory implements TextWatcher {

    private Button saveDrugPncBtn;

    @Override
    protected void handleClickEventOnListData(@NonNull MultiSelectItem multiSelectItem) {
        // do nothing
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
            if (saveDrugPncBtn != null) {
                saveDrugPncBtn.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            }
        } else {
            if (saveDrugPncBtn != null) {
                saveDrugPncBtn.setTextColor(getContext().getResources().getColor(R.color.light_grey));
            }
        }
    }

}
