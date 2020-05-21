package org.smartregister.pnc.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Photo;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.enums.LocationHierarchy;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PncReverseJsonFormUtils {

    @Nullable
    public static String prepareJsonEditMaternityRegistrationForm(@NonNull Map<String, String> detailsMap, @NonNull List<String> nonEditableFields, @NonNull Context context) {
        try {
            PncMetadata maternityMetadata = PncUtils.metadata();

            if (maternityMetadata != null) {
                JSONObject form = new FormUtils(context).getFormJson(maternityMetadata.getMaternityRegistrationFormName());
                Timber.d("Original Form %s", form);
                if (form != null) {
                    PncJsonFormUtils.addRegLocHierarchyQuestions(form, PncConstants.JsonFormKey.HOME_ADDRESS_WIDGET_KEY, LocationHierarchy.ENTIRE_TREE);
                    form.put(PncConstants.JsonFormKey.ENTITY_ID, detailsMap.get(PncConstants.Key.BASE_ENTITY_ID));

                    form.put(PncConstants.JsonFormKey.ENCOUNTER_TYPE, maternityMetadata.getUpdateEventType());
                    form.put(PncJsonFormUtils.CURRENT_ZEIR_ID, Utils.getValue(detailsMap, PncConstants.Key.OPENSRP_ID, true).replace("-", ""));

                    form.getJSONObject(PncJsonFormUtils.STEP1).put(PncConstants.JsonFormKey.FORM_TITLE, PncConstants.JsonFormKey.MATERNITY_EDIT_FORM_TITLE);

                    JSONObject metadata = form.getJSONObject(PncJsonFormUtils.METADATA);
                    metadata.put(PncJsonFormUtils.ENCOUNTER_LOCATION, PncUtils.getAllSharedPreferences().fetchCurrentLocality());
                    JSONObject stepOne = form.getJSONObject(PncJsonFormUtils.STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(PncJsonFormUtils.FIELDS);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        setFormFieldValues(detailsMap, nonEditableFields, jsonObject);
                    }
                    Timber.d("Final Form %s", form);
                    return form.toString();
                } else {
                    Timber.e("Form cannot be found");
                }
            } else {
                Timber.e(new Exception(), "Could not start MATERNITY Edit Registration Form because MaternityMetadata is null");
            }
        } catch (Exception e) {
            Timber.e(e, "MaternityJsonFormUtils --> getMetadataForEditForm");
        }
        return null;
    }

    private static void setFormFieldValues(@NonNull Map<String, String> maternityDetails, @NonNull List<String> nonEditableFields, @NonNull JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.Key.PHOTO)) {
            reversePhoto(maternityDetails.get(PncConstants.Key.BASE_ENTITY_ID), jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKey.DOB_UNKNOWN)) {
            reverseDobUnknown(maternityDetails, jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKey.AGE_ENTERED)) {
            reverseAge(Utils.getValue(maternityDetails, PncConstants.JsonFormKey.AGE, false), jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKey.DOB_ENTERED)) {
            reverseDobEntered(maternityDetails, jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(PncJsonFormUtils.PERSON_IDENTIFIER)) {
            if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncJsonFormUtils.OPENSRP_ID)) {
                jsonObject.put(PncJsonFormUtils.VALUE, maternityDetails.get(PncConstants.Key.OPENSRP_ID));
            } else {
                jsonObject.put(PncJsonFormUtils.VALUE, Utils.getValue(maternityDetails, jsonObject.getString(PncJsonFormUtils.OPENMRS_ENTITY_ID)
                        .toLowerCase(), false).replace("-", ""));
            }
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKey.HOME_ADDRESS)) {
            reverseHomeAddress(jsonObject, maternityDetails.get(PncConstants.JsonFormKey.HOME_ADDRESS));
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKey.REMINDERS)) {
            reverseReminders(maternityDetails, jsonObject);
        } else {
            jsonObject.put(PncJsonFormUtils.VALUE, getMappedValue(jsonObject.getString(PncJsonFormUtils.OPENMRS_ENTITY_ID), maternityDetails));
        }
        jsonObject.put(PncJsonFormUtils.READ_ONLY, nonEditableFields.contains(jsonObject.getString(PncJsonFormUtils.KEY)));
    }

    private static void reverseReminders(@NonNull Map<String, String> maternityDetails, @NonNull JSONObject jsonObject) throws JSONException {
        if (Boolean.valueOf(maternityDetails.get(PncConstants.JsonFormKey.REMINDERS))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(PncConstants.FormValue.IS_ENROLLED_IN_MESSAGES);
            jsonObject.put(PncJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reversePhoto(@NonNull String baseEntityId, @NonNull JSONObject jsonObject) throws JSONException {
        try {
            Photo photo = ImageUtils.profilePhotoByClientID(baseEntityId, PncImageUtils.getProfileImageResourceIdentifier());
            if (StringUtils.isNotBlank(photo.getFilePath())) {
                jsonObject.put(PncJsonFormUtils.VALUE, photo.getFilePath());
            }
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    private static void reverseDobUnknown(@NonNull Map<String, String> maternityDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String value = Utils.getValue(maternityDetails, PncConstants.JsonFormKey.DOB_UNKNOWN, false);
        if (!value.isEmpty() && Boolean.valueOf(maternityDetails.get(PncConstants.JsonFormKey.DOB_UNKNOWN))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(PncConstants.FormValue.IS_DOB_UNKNOWN);
            jsonObject.put(PncJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reverseDobEntered(@NonNull Map<String, String> maternityDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String dateString = maternityDetails.get(PncConstants.JsonFormKey.DOB);
        Date date = Utils.dobStringToDate(dateString);
        if (StringUtils.isNotBlank(dateString) && date != null) {
            jsonObject.put(PncJsonFormUtils.VALUE, com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT.format(date));
        }
    }

    private static void reverseHomeAddress(@NonNull JSONObject jsonObject, @Nullable String entity) throws JSONException {
        List<String> entityHierarchy = null;
        if (entity != null) {
            if (entity.equalsIgnoreCase(PncConstants.FormValue.OTHER)) {
                entityHierarchy = new ArrayList<>();
                entityHierarchy.add(entity);
            } else {
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(entity);
                entityHierarchy = LocationHelper.getInstance().getOpenMrsLocationHierarchy(locationId, true);
            }
        }

        String facilityHierarchyString = AssetHandler.javaToJsonString(entityHierarchy, new TypeToken<List<String>>() {}.getType());
        if (StringUtils.isNotBlank(facilityHierarchyString)) {
            jsonObject.put(PncJsonFormUtils.VALUE, facilityHierarchyString);
        }
    }

    protected static String getMappedValue(@NonNull String key, @NonNull Map<String, String> maternityDetails) {
        String value = Utils.getValue(maternityDetails, key, false);
        return !TextUtils.isEmpty(value) ? value : Utils.getValue(maternityDetails, key.toLowerCase(), false);
    }

    private static void reverseAge(@NonNull String value, @NonNull JSONObject jsonObject) throws JSONException {
        jsonObject.put(PncJsonFormUtils.VALUE, value);
    }
}