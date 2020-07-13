package org.smartregister.pnc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.pnc.widgets.PncBarcodeFactory;
import org.smartregister.pnc.widgets.PncEditTextFactory;
import org.smartregister.pnc.widgets.PncRepeatingGroupFactory;


public class PncFormInteractor extends JsonFormInteractor {


    private static final PncFormInteractor INSTANCE = new PncFormInteractor();

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new PncEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new PncBarcodeFactory());
        map.put(JsonFormConstants.REPEATING_GROUP, new PncRepeatingGroupFactory());
    }
}
