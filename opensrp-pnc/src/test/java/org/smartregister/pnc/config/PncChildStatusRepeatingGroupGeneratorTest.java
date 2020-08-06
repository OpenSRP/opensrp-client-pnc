package org.smartregister.pnc.config;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PncChildStatusRepeatingGroupGeneratorTest {

    private PncChildStatusRepeatingGroupGenerator generator;

    @Before
    public void setUp() throws Exception {
        JSONArray values = new JSONArray();
        values.put(new JSONObject().put("key", "baby_first_name"));
        values.put(new JSONObject().put("key", "baby_last_name"));

        JSONArray fields = new JSONArray();
        fields.put(new JSONObject().put("key", "child_status").put("value", values));

        JSONObject step = new JSONObject();
        step.put("fields", fields);

        generator = new PncChildStatusRepeatingGroupGenerator(step,
                "child_status",
                visitColumnMap(),
                "base_entity_id",
                record());
    }

    @After
    public void tearDown() {
        generator = null;
    }

    @Test
    public void updateFieldShouldVerifyTheFirstName() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "baby_first_name");
        generator.updateField(jsonObject, record().get(0));
        assertEquals(jsonObject.getString("value"), "John");
    }

    @Test
    public void updateFieldShouldVerifyTheLastName() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "baby_last_name");
        generator.updateField(jsonObject, record().get(0));
        assertEquals(jsonObject.getString("value"), "Doe");
    }

    @NonNull
    private Map<String, String> visitColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("baby_first_name", "baby_first_name");
        map.put("baby_last_name", "baby_last_name");
        return map;
    }

    @NonNull
    private List<HashMap<String, String>> record() {
        List<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("base_entity_id", "343-3-43-sd-f3ssfsdf");
        map.put("baby_first_name", "John");
        map.put("baby_last_name", "Doe");
        data.add(map);
        return data;
    }
}
