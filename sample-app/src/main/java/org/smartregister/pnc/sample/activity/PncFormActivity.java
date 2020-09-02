package org.smartregister.pnc.sample.activity;


import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.sample.R;
import org.smartregister.pnc.sample.config.PncChildStatusRepeatingGroupGenerator;
import org.smartregister.pnc.sample.fragment.PncFormFragment;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            generateRepeatingGrpFields(jsonObject);
            super.init(jsonObject.toString());
        } catch (Exception ex) {
            Timber.e(ex);
            super.init(json);
        }
    }

    public void generateRepeatingGrpFields(JSONObject json) {
        String motherBaseEntityId = getIntent().getStringExtra(PncDbConstants.KEY.BASE_ENTITY_ID);
        if (PncConstants.EventTypeConstants.PNC_VISIT.equals(json.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            try {
                new PncChildStatusRepeatingGroupGenerator(json.optJSONObject("step3"),
                        "child_status",
                        visitColumnMap(),
                        PncDbConstants.KEY.BASE_ENTITY_ID,
                        records(motherBaseEntityId)).init();
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private List<HashMap<String, String>> records(String motherBaseEntityId) {

        String query = "SELECT pb.base_entity_id, pb.baby_first_name, pb.baby_last_name, dob, CAST(julianday('now') - julianday(datetime(substr(pb.dob, 7, 4) || '-' || substr(pb.dob, 4, 2) || '-' || substr(pb.dob, 1, 2))) AS INTEGER) AS delivery_days " +
                "FROM pnc_baby AS pb " +
                "WHERE pb.mother_base_entity_id = '" + motherBaseEntityId + "' AND delivery_days <= " + PncConstants.HOW_BABY_OLD_IN_DAYS;

        return getData(query);
    }

    @NonNull
    public Map<String, String> visitColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("baby_first_name", "baby_first_name");
        map.put("baby_last_name", "baby_last_name");
        return map;
    }

    private ArrayList<HashMap<String, String>> getData(String query) {
        BaseRepository repo = new BaseRepository();
        return repo.rawQuery(repo.getReadableDatabase(), query);
    }

}
