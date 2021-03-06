package org.smartregister.pnc.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.scheduler.VisitStatus;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

            if (PncConstants.EventTypeConstants.PNC_MEDIC_INFO.equals(jsonForm.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
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
    public static JSONObject getJsonFormToJsonObject(String formName) {
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
            if (jsonArray != null) {
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
                            HashMap<String, String> hashMap = repeatingGroupMap.get(fieldKeyId) == null ? new HashMap<>() : repeatingGroupMap.get(fieldKeyId);
                            hashMap.put(fieldKey, fieldValue);
                            repeatingGroupMap.put(fieldKeyId, hashMap);
                        }
                    }
                }
            }
        }
        return repeatingGroupMap;
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

    public static void setVisitButtonStatus(Button button, CommonPersonObjectClient client) {
        button.setTag(R.id.BUTTON_TYPE, R.string.complete_pnc_registration);
        button.setText(R.string.complete_pnc_registration);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, button.getResources().getDimension(R.dimen.pnc_register_due_button_size));
        button.setBackgroundResource(R.drawable.pnc_status_btn_bg);

        if (client.getColumnmaps().get(PncConstants.JsonFormKeyConstants.PMI_BASE_ENTITY_ID) != null) {

            String deliveryDateStr = client.getColumnmaps().get(PncConstants.FormGlobalConstants.DELIVERY_DATE);
            if (StringUtils.isNotBlank(deliveryDateStr)) {
                LocalDate deliveryDate = LocalDate.parse(deliveryDateStr, DateTimeFormat.forPattern(DatePickerFactory.DATE_FORMAT.toPattern()));
                PncVisitScheduler pncVisitScheduler = PncLibrary.getInstance().getPncVisitScheduler();
                pncVisitScheduler.setDeliveryDate(deliveryDate);
                String strLatestVisitDate = client.getColumnmaps().get(PncDbConstants.Column.PncVisitInfo.LATEST_VISIT_DATE);
                if (strLatestVisitDate != null) {
                    LocalDate latestVisitDate = LocalDate.parse(client.getColumnmaps().get(PncDbConstants.Column.PncVisitInfo.LATEST_VISIT_DATE), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                    pncVisitScheduler.setLatestVisitDateInMills(String.valueOf(latestVisitDate.toDate().getTime()));
                } else {
                    pncVisitScheduler.setLatestVisitDateInMills(null);
                }

                VisitStatus visitStatus = pncVisitScheduler.getStatus();
                if (visitStatus == VisitStatus.PNC_DUE) {
                    button.setText(R.string.pnc_due);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_due);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.due_color));
                    button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.pnc_btn_due_bg));
                } else if (visitStatus == VisitStatus.PNC_OVERDUE) {
                    button.setText(R.string.pnc_due);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_overdue);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.white));
                    button.setBackgroundColor(ContextCompat.getColor(button.getContext(), R.color.overdue_color));
                } else if (visitStatus == VisitStatus.RECORD_PNC) {
                    button.setText(R.string.record_pnc);
                    button.setTag(R.id.BUTTON_TYPE, R.string.record_pnc);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.due_color));
                    button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.pnc_btn_due_bg));
                } else if (visitStatus == VisitStatus.PNC_DONE_TODAY) {
                    button.setText(R.string.pnc_done_today);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_done_today);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.dark_grey));
                    button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.pnc_btn_done_today));
                } else if (visitStatus == VisitStatus.PNC_CLOSE) {
                    button.setText(R.string.pnc_close);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_close);
                }
            } else {
                Timber.e("deliveryStr is null");
            }

        }
        if (client.getColumnmaps().get(PncConstants.KeyConstants.PPF_ID) != null) {
            String formType = client.getColumnmaps().get(PncConstants.KeyConstants.PPF_FORM_TYPE);
            if (PncConstants.EventTypeConstants.PNC_MEDIC_INFO.equals(formType) && (client.getColumnmaps().get(PncConstants.JsonFormKeyConstants.PMI_BASE_ENTITY_ID) == null)) {
                button.setText(R.string.complete_pnc_registration);
            }
            button.setBackgroundResource(R.drawable.saved_form_bg);
            button.setTextColor(button.getContext().getResources().getColor(R.color.dark_grey_text));
        }
    }

    public static void addGlobals(String baseEntityId, JSONObject form) {

        String query = "SELECT pmi.delivery_date, pmi.hiv_status_previous, pmi.hiv_status_current FROM pnc_medic_info AS pmi \n" +
                "WHERE pmi.base_entity_id = '" + baseEntityId + "'";

        Map<String, String> detailMap = getMergedData(query);

        try {
            JSONObject defaultGlobal = new JSONObject();

            for (Map.Entry<String, String> entry : detailMap.entrySet()) {
                defaultGlobal.put(entry.getKey(), entry.getValue());
            }

            LocalDate todayDate = LocalDate.now();
            if (detailMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE) != null) {
                LocalDate deliveryDate = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.DELIVERY_DATE), DateTimeFormat.forPattern(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN));
                int numberOfDays = Days.daysBetween(deliveryDate, todayDate).getDays();
                defaultGlobal.put(PncConstants.FormGlobalConstants.PNC_VISIT_PERIOD, numberOfDays);
            }

            if (detailMap.get(PncConstants.FormGlobalConstants.BABY_DOB) != null) {
                LocalDate babyDob = LocalDate.parse(detailMap.get(PncConstants.FormGlobalConstants.BABY_DOB), DateTimeFormat.forPattern(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN));
                int numberOfYears = new Period(babyDob, todayDate).getYears();
                defaultGlobal.put(PncConstants.FormGlobalConstants.BABY_AGE, numberOfYears);
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.HIV_STATUS_PREVIOUS)) {
                defaultGlobal.put(PncConstants.FormGlobalConstants.HIV_STATUS_PREVIOUS, detailMap.get(PncConstants.FormGlobalConstants.HIV_STATUS_PREVIOUS));
            }

            if (detailMap.containsKey(PncConstants.FormGlobalConstants.HIV_STATUS_CURRENT)) {
                defaultGlobal.put(PncConstants.FormGlobalConstants.HIV_STATUS_CURRENT, detailMap.get(PncConstants.FormGlobalConstants.HIV_STATUS_CURRENT));
            }

            form.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, defaultGlobal);
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    public static int getDeliveryDays(@Nullable String deliveryDate) {
        if (deliveryDate != null) {
            LocalDate date = LocalDate.parse(deliveryDate, DateTimeFormat.forPattern(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN));
            return Days.daysBetween(date, LocalDate.now()).getDays();
        } else {
            return 0;
        }
    }

    public static void processPreChecks(@NonNull String entityId, @NonNull JSONObject jsonForm, @Nullable HashMap<String, String> intentData) {
        intentData.put(PncDbConstants.KEY.BASE_ENTITY_ID, entityId);

        if (PncConstants.EventTypeConstants.PNC_VISIT.equals(jsonForm.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            PncUtils.addGlobals(entityId, jsonForm);
        }

        if (PncConstants.EventTypeConstants.PNC_MEDIC_INFO.equals(jsonForm.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            PncUtils.putDataOnField(jsonForm, PncConstants.JsonFormKeyConstants.LIVE_BIRTHS, PncConstants.JsonFormKeyConstants.CHILD_REGISTERED_COUNT, intentData.get(PncConstants.JsonFormKeyConstants.CHILD_REGISTERED_COUNT));
        }
    }

    public static void putDataOnField(JSONObject form, String fieldKey, String key, String value) {
        try {
            Iterator<String> formKeys = form.keys();

            while (formKeys.hasNext()) {
                String formKey = formKeys.next();
                if (formKey != null && formKey.startsWith(JsonFormConstants.STEP)) {
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
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    public static HashMap<String, String> getMergedData(String... queries) {
        HashMap<String, String> mergedData = new HashMap<>();

        BaseRepository repo = new BaseRepository();
        for (String query : queries) {

            ArrayList<HashMap<String, String>> dataList = repo.rawQuery(repo.getReadableDatabase(), query);

            if (!dataList.isEmpty()) {
                HashMap<String, String> data = dataList.get(0);
                mergedData.putAll(data);
            }
        }

        return mergedData;
    }

    @NonNull
    public static String[] generateNIds(int n) {
        String[] strIds = new String[n];
        for (int i = 0; i < n; i++) {
            strIds[i] = JsonFormUtils.generateRandomUUIDString();
        }
        return strIds;
    }

    public static String getTodaysDate() {
        return convertDateFormat(DateTime.now());
    }

    public static String reverseHyphenSeparatedValues(@Nullable String rawString, @NonNull String outputSeparator) {
        if (StringUtils.isNotBlank(rawString)) {
            String resultString = rawString;
            String[] tokenArray = resultString.trim().split("-");
            ArrayUtils.reverse(tokenArray);
            resultString = StringUtils.join(tokenArray, outputSeparator);
            return resultString;
        }
        return "";
    }

    public static void saveImageAndCloseOutputStream(Bitmap image, File outputFile) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outputFile);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        image.compress(compressFormat, 100, os);
    }
}
