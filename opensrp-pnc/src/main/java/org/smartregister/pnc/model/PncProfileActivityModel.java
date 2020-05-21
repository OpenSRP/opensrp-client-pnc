package org.smartregister.pnc.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;

import java.util.HashMap;

public class PncProfileActivityModel {
    public JSONObject getFormAsJson(String formName, String caseId, String locationId, HashMap<String, String> injectedValues) throws JSONException {
        JSONObject form = PncUtils.getJsonFormToJsonObject(formName);
        if (form != null) {
            return PncJsonFormUtils.getFormAsJson(form, formName, caseId, locationId, injectedValues);
        }
        return null;
    }
}
