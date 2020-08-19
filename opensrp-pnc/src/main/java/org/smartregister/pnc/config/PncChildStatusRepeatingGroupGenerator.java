package org.smartregister.pnc.config;

import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PncChildStatusRepeatingGroupGenerator extends RepeatingGroupGenerator {

    public PncChildStatusRepeatingGroupGenerator(@NonNull JSONObject step, @NonNull String repeatingGroupKey, @NonNull Map<String, String> columnMap, @NonNull String uniqueKeyField, @NonNull List<HashMap<String, String>> storedValues) {
        super(step, "step4", repeatingGroupKey, columnMap, uniqueKeyField, storedValues);
    }

    @Override
    public void updateField(JSONObject repeatingGrpField, Map<String, String> entryMap) throws JSONException {
        super.updateField(repeatingGrpField, entryMap);
        String key = repeatingGrpField.optString(JsonFormConstants.KEY);
        if ("baby_first_name".equals(key)) {
            repeatingGrpField.put(JsonFormConstants.VALUE, String.format("%s", entryMap.get("baby_first_name")));
        } else if ("baby_last_name".equals(key)) {
            repeatingGrpField.put(JsonFormConstants.VALUE, String.format("%s", entryMap.get("baby_last_name")));
        }
    }
}
