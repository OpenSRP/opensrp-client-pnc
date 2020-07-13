package org.smartregister.pnc.config;

import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.util.Utils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT;

public class RepeatingGroupGenerator {
    private JSONObject step;
    private String repeatingGroupKey;
    private String uniqueKeyField;
    private List<HashMap<String, String>> storedValues;
    private Map<String, String> columnMap;
    private Set<String> fieldsWithoutRefreshLogic;
    private Set<String> fieldsWithoutSpecialViewValidation;
    private Set<String> hiddenFields;
    private Set<String> readOnlyFields;

    public RepeatingGroupGenerator(JSONObject step, String repeatingGroupKey,
                                   @NonNull Map<String, String> columnMap,
                                   String uniqueKeyField,
                                   List<HashMap<String, String>> storedValues) {
        this.repeatingGroupKey = repeatingGroupKey;
        this.uniqueKeyField = uniqueKeyField;
        this.storedValues = storedValues;
        this.step = step;
        this.columnMap = columnMap;
    }

    public JSONObject getStep() {
        return step;
    }

    public RepeatingGroupGenerator setStep(JSONObject step) {
        this.step = step;
        return this;
    }

    public void init() throws JSONException {
        JSONArray repeatingGrpValues = null;
        JSONArray stepFields = step.optJSONArray(JsonFormConstants.FIELDS);
        int pos = 0;
        for (int i = 0; i < stepFields.length(); i++) {
            JSONObject field = stepFields.optJSONObject(i);
            String key = field.optString(JsonFormConstants.KEY);
            if (key.equals(repeatingGroupKey)) {
                pos = i;
                repeatingGrpValues = field.optJSONArray(JsonFormConstants.VALUE);
                break;
            }
        }

        for (Map<String, String> entryMap : storedValues) {
            String baseEntityId = entryMap.get(uniqueKeyField).replaceAll("-", "");
            for (int i = 0; i < repeatingGrpValues.length(); i++) {
                JSONObject object = repeatingGrpValues.optJSONObject(i);
                JSONObject repeatingGrpField = new JSONObject(object.toString());
                String repeatingGrpFieldKey = repeatingGrpField.optString(JsonFormConstants.KEY);

                if (entryMap.containsKey(repeatingGroupKey)) {
                    if (repeatingGrpField.optString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL))
                        repeatingGrpField.put(JsonFormConstants.TEXT, processColumnValue(repeatingGroupKey, entryMap.get(repeatingGroupKey)));
                    else
                        repeatingGrpField.put(JsonFormConstants.VALUE, processColumnValue(repeatingGroupKey, entryMap.get(repeatingGroupKey)));
                } else if (columnMap.get(repeatingGrpFieldKey) != null && entryMap.containsKey(columnMap.get(repeatingGrpFieldKey))) {
                    if (repeatingGrpField.optString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL))
                        repeatingGrpField.put(JsonFormConstants.TEXT, processColumnValue(columnMap.get(repeatingGrpFieldKey), entryMap.get(columnMap.get(repeatingGrpFieldKey))));
                    else
                        repeatingGrpField.put(JsonFormConstants.VALUE, processColumnValue(columnMap.get(repeatingGrpFieldKey), entryMap.get(columnMap.get(repeatingGrpFieldKey))));
                }

                if (readOnlyFields().contains(repeatingGrpFieldKey))
                    repeatingGrpField.put(JsonFormConstants.READ_ONLY, "true");

                if (getFieldsWithoutRefreshLogic().contains(repeatingGrpFieldKey)) {
                    repeatingGrpField.remove(JsonFormConstants.RELEVANCE);
                    repeatingGrpField.remove(JsonFormConstants.CONSTRAINTS);
                    repeatingGrpField.remove(JsonFormConstants.CALCULATION);
                }

                if (getHiddenFields().contains(repeatingGrpFieldKey))
                    repeatingGrpField.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);

                if (repeatingGrpFieldKey.equals("generated_grp"))
                    repeatingGrpField.put(JsonFormConstants.VALUE, "true");

                if (getFieldsWithoutSpecialViewValidation().contains(repeatingGrpFieldKey)) {
                    repeatingGrpField.remove(JsonFormConstants.V_REQUIRED);
                    repeatingGrpField.remove(JsonFormConstants.V_NUMERIC);
                }

                updateField(repeatingGrpField);
                repeatingGrpField.put(JsonFormConstants.KEY, repeatingGrpFieldKey + "_" + baseEntityId);
                stepFields.put(++pos, repeatingGrpField);
            }
        }

    }

    public void updateField(JSONObject repeatingGrpField) throws JSONException {
        String key = repeatingGrpField.optString(JsonFormConstants.KEY);
        if (key.equals("baby_gender")) {
            repeatingGrpField.put(JsonFormConstants.VALUE, repeatingGrpField.optString(JsonFormConstants.VALUE).toLowerCase());
        }
    }

    public String processColumnValue(String columnName, String value) {
        String s = "";
        if (columnName.equals("dob")) {
            Date dob = Utils.dobStringToDate(value);
            if (dob != null) {
                s = DATE_FORMAT.format(dob);
            }
        } else {
            s = value;
        }
        return s;
    }

    public Set<String> readOnlyFields() {
        if (readOnlyFields == null) {
            readOnlyFields = new HashSet<>(Arrays.asList("baby_first_name", "baby_last_name", "baby_dob"));
        }
        return readOnlyFields;
    }

    public void setReadOnlyFields(Set<String> readOnlyFields) {
        this.readOnlyFields = readOnlyFields;
    }

    public Set<String> getFieldsWithoutRefreshLogic() {
        if (fieldsWithoutRefreshLogic == null) {
            fieldsWithoutRefreshLogic = new HashSet<>(Arrays.asList(""));
        }
        return fieldsWithoutRefreshLogic;
    }


    public void setFieldsWithoutRefreshLogic(Set<String> fieldsWithoutRefreshLogic) {
        this.fieldsWithoutRefreshLogic = fieldsWithoutRefreshLogic;
    }


    public Set<String> getFieldsWithoutSpecialViewValidation() {
        if (fieldsWithoutSpecialViewValidation == null) {
            fieldsWithoutSpecialViewValidation = new HashSet<>(Arrays.asList(""));
        }
        return fieldsWithoutSpecialViewValidation;
    }

    public void setFieldsWithoutSpecialViewValidation(Set<String> fieldsWithoutSpecialViewValidation) {
        this.fieldsWithoutSpecialViewValidation = fieldsWithoutSpecialViewValidation;
    }

    public void setHiddenFields(Set<String> hiddenFields) {
        this.hiddenFields = hiddenFields;
    }

    public Set<String> getHiddenFields() {
        if (hiddenFields == null) {
            hiddenFields = new HashSet<>(Arrays.asList(""));
        }
        return hiddenFields;
    }

}
