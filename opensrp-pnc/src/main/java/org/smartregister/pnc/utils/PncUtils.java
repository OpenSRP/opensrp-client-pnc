package org.smartregister.pnc.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.listener.PncEventActionCallBack;
import org.smartregister.pnc.model.PncRegisterActivityModel;
import org.smartregister.pnc.pojo.PncBaseDetails;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.presenter.PncRegisterActivityPresenter;
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.scheduler.VisitScheduler;
import org.smartregister.pnc.scheduler.VisitStatus;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncUtils extends org.smartregister.util.Utils {

    private static final String OTHER_SUFFIX = ", other]";
    private static FormUtils formUtils;

    public static float convertDpToPixel(float dp, @NonNull Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @NonNull
    public static String fillTemplate(boolean isHtml, @NonNull String stringValue, @NonNull Facts facts) {
        String stringValueResult = stringValue;
        while (stringValueResult.contains("{")) {
            String key = stringValueResult.substring(stringValueResult.indexOf("{") + 1, stringValueResult.indexOf("}"));
            String value = processValue(key, facts);
            stringValueResult = stringValueResult.replace("{" + key + "}", value).replaceAll(", $", "").trim();
        }

        //Remove unnecessary commas by cleaning the returned string
        return isHtml ? stringValueResult : cleanValueResult(stringValueResult);
    }

    public static String fillTemplate(@NonNull String stringValue, @NonNull Facts facts) {
        return fillTemplate(false, stringValue, facts);
    }

    public static boolean isTemplate(@NonNull String stringValue) {
        return stringValue.contains("{") && stringValue.contains("}");
    }

    public static void setTextAsHtml(@NonNull TextView textView, @NonNull String html) {
        textView.setAllCaps(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(html));
        }
    }

    @NonNull
    private static String processValue(@NonNull String key, @NonNull Facts facts) {
        String value = "";
        if (facts.get(key) instanceof String) {
            value = facts.get(key);
            if (value != null && value.endsWith(OTHER_SUFFIX)) {
                Object otherValue = value.endsWith(OTHER_SUFFIX) ? facts.get(key + ConstantsUtils.SuffixUtils.OTHER) : "";
                value = otherValue != null ?
                        value.substring(0, value.lastIndexOf(",")) + ", " + otherValue.toString() + "]" :
                        value.substring(0, value.lastIndexOf(",")) + "]";

            }
        }

        return keyToValueConverter(value);
    }

    @NonNull
    private static String cleanValueResult(@NonNull String result) {
        List<String> nonEmptyItems = new ArrayList<>();

        for (String item : result.split(",")) {
            if (item.length() > 0) {
                nonEmptyItems.add(item);
            }
        }
        //Get the first item that usually  has a colon and remove it form list, if list has one item append separator
        String itemLabel = "";
        if (!nonEmptyItems.isEmpty() && nonEmptyItems.get(0).contains(":")) {
            String[] separatedLabel = nonEmptyItems.get(0).split(":");
            itemLabel = separatedLabel[0];
            if (separatedLabel.length > 1) {
                nonEmptyItems.set(0, nonEmptyItems.get(0).split(":")[1]);
            }//replace with extracted value
        }
        return itemLabel + (!TextUtils.isEmpty(itemLabel) ? ": " : "") + StringUtils.join(nonEmptyItems.toArray(), ",");
    }

    @NonNull
    public static org.smartregister.Context context() {
        return PncLibrary.getInstance().context();
    }

    @Nullable
    public static PncMetadata metadata() {
        return PncLibrary.getInstance().getPncConfiguration().getPncMetadata();
    }

    @Nullable
    public static String getIntentValue(@Nullable Intent data, @NonNull String key) {
        if (data == null) {
            return null;
        }

        return data.hasExtra(key) ? data.getStringExtra(key) : null;
    }

    @NonNull
    public static String convertDate(@NonNull Date date, @NonNull String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        return simpleDateFormat.format(date);
    }

    @Nullable
    public static Date convertStringToDate(@NonNull String pattern, @NonNull String dateString) {
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);

        if (!TextUtils.isEmpty(dateString) && !TextUtils.isEmpty(pattern)) {
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }
        return date;
    }

    @NonNull
    public static String generateNIds(int n) {
        StringBuilder strIds = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if ((i + 1) == n) {
                strIds.append(JsonFormUtils.generateRandomUUIDString());
            } else {
                strIds.append(JsonFormUtils.generateRandomUUIDString()).append(",");
            }
        }
        return strIds.toString();
    }

    @NotNull
    public static String getClientAge(String dobString, String translatedYearInitial) {
        String age = dobString;
        if (dobString.contains(translatedYearInitial)) {
            String extractedYear = dobString.substring(0, dobString.indexOf(translatedYearInitial));
            int year = dobString.contains(translatedYearInitial) ? Integer.parseInt(extractedYear) : 0;
            if (year >= 5) {
                age = extractedYear;
            }
        }
        return age;
    }

    @Nullable
    public static Intent buildFormActivityIntent(JSONObject jsonForm, HashMap<String, String> intentData, Context context) {
        PncMetadata pncMetadata = PncLibrary.getInstance().getPncConfiguration().getPncMetadata();
        if (pncMetadata != null) {
            Intent intent = new Intent(context, pncMetadata.getPncFormActivity());
            intent.putExtra(PncConstants.JsonFormExtraConstants.JSON, jsonForm.toString());

            Form form = new Form();
            form.setWizard(false);
            form.setName("");
            String encounterType = jsonForm.optString(PncJsonFormUtils.ENCOUNTER_TYPE);
            form.setName(encounterType);

            // If the form has more than one step, enable the form wizard
            for (Iterator<String> objectKeys = jsonForm.keys(); objectKeys.hasNext(); ) {
                String key = objectKeys.next();
                if (!TextUtils.isEmpty(key) && key.contains("step") && !"step2".equalsIgnoreCase(key)) {
                    form.setWizard(true);
                    break;
                }
            }

            form.setHideSaveLabel(true);
            form.setPreviousLabel("");
            form.setNextLabel("");
            form.setHideNextButton(false);
            form.setHidePreviousButton(false);

            if (PncConstants.EventTypeConstants.PNC_OUTCOME.equals(jsonForm.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
                form.setSaveLabel(context.getString(R.string.submit_and_close_pnc));
            }

            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            if (intentData != null) {
                for (String intentKey : intentData.keySet()) {
                    intent.putExtra(intentKey, intentData.get(intentKey));
                }
            }
            return intent;
        }

        return null;
    }

    @Nullable
    public static JSONObject getJsonFormToJsonObject(String formName){
        if (getFormUtils() == null) {
            return null;
        }

        return getFormUtils().getFormJson(formName);
    }


    @Nullable
    public static FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(PncLibrary.getInstance().context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }


    @NonNull
    public static String keyToValueConverter(String keys) {
        if (!TextUtils.isEmpty(keys)) {
            String cleanKey;

            //If this contains html then don't capitalize it because it fails and the output is in lowercase
            if (keys.contains("<") && keys.contains(">")) {
                cleanKey = keys;
            } else {
                cleanKey = WordUtils.capitalizeFully(cleanValue(keys), ',');
            }

            return cleanKey.replaceAll("_", " ");
        } else {
            return "";
        }
    }

    public static String cleanValue(String raw) {
        if (raw.length() > 0 && raw.charAt(0) == '[') {
            return raw.substring(1, raw.length() - 1);
        } else {
            return raw;
        }
    }

    @NonNull
    public static JSONArray generateFieldsFromJsonForm(@NonNull JSONObject jsonFormObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        Iterator<String> formKeys = jsonFormObject.keys();

        while (formKeys.hasNext()) {
            String formKey = formKeys.next();
            if (formKey != null && formKey.startsWith("step")) {
                JSONObject stepJSONObject = jsonFormObject.getJSONObject(formKey);
                JSONArray fieldsArray = stepJSONObject.getJSONArray(PncJsonFormUtils.FIELDS);
                for (int i = 0; i < fieldsArray.length(); i++) {
                    jsonArray.put(fieldsArray.get(i));
                }
            }
        }

        return jsonArray;
    }

    public static HashMap<String, HashMap<String, String>> buildRepeatingGroup(@NonNull JSONObject stepJsonObject, String fieldName) throws JSONException {
        ArrayList<String> keysArrayList = new ArrayList<>();
        JSONArray fields = stepJsonObject.optJSONArray(JsonFormConstants.FIELDS);
        JSONObject jsonObject = JsonFormUtils.getFieldJSONObject(fields, fieldName);
        HashMap<String, HashMap<String, String>> repeatingGroupMap = new HashMap<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray(JsonFormConstants.VALUE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject valueField = jsonArray.optJSONObject(i);
                String fieldKey = valueField.optString(JsonFormConstants.KEY);
                keysArrayList.add(fieldKey);
                jsonObject.remove(JsonFormConstants.VALUE);
            }

            for (int k = 0; k < fields.length(); k++) {
                JSONObject valueField = fields.optJSONObject(k);
                String fieldKey = valueField.optString(JsonFormConstants.KEY);
                String fieldValue = valueField.optString(JsonFormConstants.VALUE);

                if (fieldKey.contains("_")) {
                    fieldKey = fieldKey.substring(0, fieldKey.lastIndexOf("_"));
                    if (keysArrayList.contains(fieldKey) && StringUtils.isNotBlank(fieldValue)) {
                        String fieldKeyId = valueField.optString(JsonFormConstants.KEY).substring(fieldKey.length() + 1);
                        valueField.put(JsonFormConstants.KEY, fieldKey);
                        HashMap<String, String> hashMap = repeatingGroupMap.get(fieldKeyId) == null ? new HashMap<>() : repeatingGroupMap.get(fieldKeyId);
                        hashMap.put(fieldKey, fieldValue);
                        repeatingGroupMap.put(fieldKeyId, hashMap);
                    }
                }
            }
        }
        return repeatingGroupMap;
    }

    public static HashMap<String, String> getPncClient(String baseEntityId) {
        ArrayList<HashMap<String, String>> hashMap = CoreLibrary.getInstance().context().getEventClientRepository().rawQuery(PncLibrary.getInstance().getRepository().getReadableDatabase(),
                "select * from " + metadata().getTableName() +
                        " where " + metadata().getTableName() + ".id = '" + baseEntityId + "' limit 1");
        if (!hashMap.isEmpty()) {
            return hashMap.get(0);
        }
        return null;
    }

    public static String getNextUniqueId() {
        UniqueIdRepository uniqueIdRepo = PncLibrary.getInstance().getUniqueIdRepository();
        return uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
    }

    public static void processEvents(@NonNull List<PncEventClient> pncEventClients) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();
            for (PncEventClient eventClient : pncEventClients) {
                try {
                    Client baseClient = eventClient.getClient();
                    Event baseEvent = eventClient.getEvent();
                    JSONObject clientJson = new JSONObject(PncJsonFormUtils.gson.toJson(baseClient));
                    PncLibrary.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                    JSONObject eventJson = new JSONObject(PncJsonFormUtils.gson.toJson(baseEvent));
                    PncLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
                    currentFormSubmissionIds.add(baseEvent.getFormSubmissionId());
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            PncLibrary.getInstance().getClientProcessorForJava().processClient(PncLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void setVisitButtonStatus(Button button, String baseEntityId) {
        button.setTag(R.id.BUTTON_TYPE, R.string.start_pnc);
        button.setText(R.string.start_pnc);
        button.setBackgroundResource(R.drawable.pnc_outcome_bg);

        PncBaseDetails pncBaseDetails = new PncBaseDetails();
        pncBaseDetails.setBaseEntityId(baseEntityId);
        pncBaseDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findOne(pncBaseDetails);
        if (pncBaseDetails != null && pncBaseDetails.getProperties() != null) {
            HashMap<String, String> data = pncBaseDetails.getProperties();

            if ("1".equals(data.get(PncConstants.JsonFormKeyConstants.OUTCOME_SUBMITTED))) {

                String deliveryDateStr = data.get(PncConstants.FormGlobalConstants.DELIVERY_DATE);
                LocalDate deliveryDate = LocalDate.parse(deliveryDateStr, DateTimeFormat.forPattern("dd-MM-yyyy"));
                VisitScheduler pncVisitScheduler = new PncVisitScheduler(deliveryDate, baseEntityId);

                if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DUE) {
                    button.setText(R.string.pnc_due);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_due);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_OVERDUE) {
                    button.setText(R.string.pnc_due);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_overdue);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.pnc_circle_red));
                    button.setBackgroundResource(R.drawable.pnc_overdue_bg);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.RECORD_PNC) {
                    button.setText(R.string.record_pnc);
                    button.setTag(R.id.BUTTON_TYPE, R.string.record_pnc);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DONE_TODAY) {
                    button.setText(R.string.pnc_done_today);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_done_today);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.dark_grey));
                    button.setBackground(null);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_CLOSE) {
                    button.setText(R.string.pnc_close);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_close);
                }
            }
        }
    }

    public static void addGlobals(String baseEntityId, JSONObject form) {

        Map<String, String> detailMap = CoreLibrary.getInstance().context().detailsRepository().getAllDetailsForClient(baseEntityId);

        try {
            JSONObject defaultGlobal = new JSONObject();

            for (Map.Entry<String, String> entry: detailMap.entrySet()) {
                defaultGlobal.put(entry.getKey(), entry.getValue());
            }

            LocalDate todayDate = LocalDate.now();
            if (detailMap.containsKey(PncConstants.FormGlobalConstants.DELIVERY_DATE)) {
                LocalDate deliveryDate = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE), DateTimeFormat.forPattern("dd-MM-yyyy"));
                int numberOfDays = Days.daysBetween(deliveryDate, todayDate).getDays();
                defaultGlobal.put(PncConstants.FormGlobalConstants.PNC_VISIT_PERIOD, numberOfDays);
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.BABY_DOB)) {
                LocalDate babyDob = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.BABY_DOB), DateTimeFormat.forPattern("dd-MM-yyyy"));
                int numberOfYears = new Period(babyDob, todayDate).getYears();
                defaultGlobal.put(PncConstants.FormGlobalConstants.BABY_AGE, numberOfYears);
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.HIV_STATUS_PREVIOUS)) {
                defaultGlobal.put(PncConstants.FormGlobalConstants.HIV_STATUS_PREVIOUS, detailMap.get(PncConstants.FormGlobalConstants.HIV_STATUS_PREVIOUS));
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.HIV_STATUS_CURRENT)) {
                defaultGlobal.put(PncConstants.FormGlobalConstants.HIV_STATUS_CURRENT, detailMap.get(PncConstants.FormGlobalConstants.HIV_STATUS_CURRENT));
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.BABY_COMPLICATIONS)) {
                defaultGlobal.put(PncConstants.FormGlobalConstants.BABY_COMPLICATIONS, detailMap.get(PncConstants.FormGlobalConstants.BABY_COMPLICATIONS));
            }

            defaultGlobal.put("child_registered_count", 2);

            form.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, defaultGlobal);
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    public static int getDeliveryDays(String baseEntityId) {
        Map<String, String> detailMap = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findByBaseEntityId(baseEntityId);
        if (StringUtils.isNotBlank(detailMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE))) {
            LocalDate deliveryDate = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE), DateTimeFormat.forPattern("dd-MM-yyyy"));
            return Days.daysBetween(deliveryDate, LocalDate.now()).getDays();
        }
        else {
            return 0;
        }
    }

    public static void saveRegistrationFormSilent(String jsonString, RegisterParams registerParams, PncEventActionCallBack callBack) {

        PncRegisterActivityPresenter presenter = new PncRegisterActivityPresenter(null, new PncRegisterActivityModel()){
            @Override
            public void onRegistrationSaved(boolean isEdit) {
                super.onRegistrationSaved(isEdit);
                callBack.onPncEventSaved();
            }
        };

        presenter.saveForm(jsonString, registerParams);

    }

    public static void saveOutcomeAndVisitFormSilent(String jsonString, Intent data, PncEventActionCallBack callBack) {

        PncRegisterActivityPresenter presenter = new PncRegisterActivityPresenter(null, new PncRegisterActivityModel()){
            @Override
            public void onEventSaved() {
                super.onEventSaved();
                callBack.onPncEventSaved();
            }
        };

        presenter.savePncForm(jsonString, data);

    }

    public static void processPreChecks(@NonNull String entityId, @NonNull JSONObject jsonForm, @Nullable HashMap<String, String> intentData) {
        intentData.put(PncDbConstants.KEY.BASE_ENTITY_ID, entityId);
        PncUtils.addGlobals(entityId, jsonForm);
        if (PncConstants.EventTypeConstants.PNC_VISIT.equals(jsonForm.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))){
            PncUtils.addNumberOfBabyCount(entityId, jsonForm);
        }

        if (PncConstants.EventTypeConstants.PNC_OUTCOME.equals(jsonForm.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            PncUtils.putDataOnField(jsonForm, PncConstants.JsonFormKeyConstants.LIVE_BIRTHS, PncConstants.JsonFormKeyConstants.CHILD_REGISTERED_COUNT, intentData.get(PncConstants.JsonFormKeyConstants.CHILD_REGISTERED_COUNT));
        }
    }

    public static void addNumberOfBabyCount(String baseEntityId, JSONObject form) {
        int numberOfCount = PncLibrary.getInstance().getPncChildRepository().countBaby28DaysOld(baseEntityId, 28);
        PncUtils.putDataOnField(form, PncConstants.JsonFormKeyConstants.CHILD_STATUS_GROUP, PncConstants.JsonFormKeyConstants.BABY_COUNT_ALIVE, String.valueOf(numberOfCount));
    }

    public static void putDataOnField(JSONObject form, String fieldKey, String key, String value) {
        try {
            Iterator<String> formKeys = form.keys();

            while (formKeys.hasNext()) {
                String formKey = formKeys.next();
                if (formKey != null && formKey.startsWith("step")) {
                    JSONObject stepJSONObject = form.getJSONObject(formKey);
                    JSONArray fieldsArray = stepJSONObject.getJSONArray(PncJsonFormUtils.FIELDS);
                    for (int i = 0; i < fieldsArray.length(); i++) {
                        JSONObject comObject = fieldsArray.getJSONObject(i);
                        if (fieldKey.equals(comObject.getString(PncJsonFormUtils.KEY))) {
                            comObject.put(key, value);
                        }
                    }
                }
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }
}
