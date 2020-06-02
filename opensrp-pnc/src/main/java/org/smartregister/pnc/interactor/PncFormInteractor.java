package org.smartregister.pnc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.widgets.PncBarcodeFactory;
import org.smartregister.pnc.widgets.PncEditTextFactory;
import org.smartregister.pnc.widgets.PncMultiSelectDrugPicker;
import org.smartregister.pnc.widgets.PncMultiSelectList;


public class PncFormInteractor extends JsonFormInteractor {


    public static JsonFormInteractor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PncFormInteractor();
        }
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new PncEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new PncBarcodeFactory());
        map.put(PncConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, new PncMultiSelectDrugPicker());
        map.put(JsonFormConstants.MULTI_SELECT_LIST, new PncMultiSelectList());
    }
}
