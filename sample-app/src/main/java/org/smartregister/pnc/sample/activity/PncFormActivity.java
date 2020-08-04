package org.smartregister.pnc.sample.activity;


import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.sample.R;
import org.smartregister.pnc.sample.fragment.PncFormFragment;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;


public class PncFormActivity extends BasePncFormActivity {

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        PncFormFragment pncFormFragment = PncFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(R.id.container, pncFormFragment).commit();
    }

    @Override
    public void init(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (PncConstants.EventTypeConstants.PNC_VISIT.equals(jsonObject.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
                addRepeatingGroupFields(jsonObject.getJSONObject("step3"));
            }
            super.init(jsonObject.toString());
        }
        catch (Exception ex) {
            Timber.e(ex);
            super.init(json);
        }
    }

    private void addRepeatingGroupFields(JSONObject step) {

        try {
            String motherBaseEntityId = getIntent().getStringExtra(PncDbConstants.KEY.BASE_ENTITY_ID);

            Set<String> possibleJsonArrayKeys = new HashSet<>();
            possibleJsonArrayKeys.add("baby_first_name");
            possibleJsonArrayKeys.add("baby_last_name");

            String query = "SELECT baby_first_name, baby_last_name, dob, CAST(julianday('now') - julianday(datetime(substr(pb.dob, 7, 4) || '-' || substr(pb.dob, 4, 2) || '-' || substr(pb.dob, 1, 2))) AS INTEGER) AS delivery_days " +
                    "FROM pnc_baby AS pb " +
                    "WHERE pb.mother_base_entity_id = '" + motherBaseEntityId + "' AND delivery_days <= " + PncConstants.HOW_BABY_OLD_IN_DAYS;

            ArrayList<HashMap<String, String>> childData = getData(query);

            for (HashMap<String, String> childMap : childData) {

                JSONObject jsonObject = PncUtils.getJsonFormToJsonObject("pnc_visit_child_status_template");
                if (jsonObject != null) {
                    String jsonString = jsonObject.toString();

                    for (Map.Entry<String, String> entry : childMap.entrySet()) {

                        String key = entry.getKey();
                        String value = entry.getValue();
                        value = value == null ? "" : value;

                        if (possibleJsonArrayKeys.contains(entry.getKey())) {
                            if (key.equals("baby_first_name") || key.equals("baby_last_name")) {
                                jsonString = jsonString.replace("{" + entry.getKey() + "}", value);
                            }
                        }
                    }

                    jsonObject = new JSONObject(jsonString);
                    String randomBaseEntityId = JsonFormUtils.generateRandomUUIDString().replace("-","");
                    JSONArray fields = jsonObject.getJSONArray("fields");

                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        String key = field.getString("key");
                        String newKey = key + randomBaseEntityId;
                        jsonString = jsonString.replace("\"" + key + "\"", "\"" + newKey + "\"");
                        jsonString = jsonString.replace("\"step3:" + key + "\"", "\"step4:" + newKey + "\"");
                    }

                    jsonObject = new JSONObject(jsonString);
                    JSONArray fieldsArray = jsonObject.getJSONArray("fields");
                    for (int i = 0; i < fieldsArray.length(); i++) {
                        if (!fieldsArray.getJSONObject(i).getString("key").equals("child_registered_" + randomBaseEntityId)) {
                            step.getJSONArray("fields").put(fieldsArray.getJSONObject(i));
                        }
                    }
                }
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    private ArrayList<HashMap<String, String>> getData(String query) {
        BaseRepository repo = new BaseRepository();
        return repo.rawQuery(repo.getReadableDatabase(), query);
    }

}
