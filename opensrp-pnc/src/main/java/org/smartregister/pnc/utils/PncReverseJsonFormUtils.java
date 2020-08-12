package org.smartregister.pnc.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Photo;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.PncLibrary;
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
import java.util.Set;

import timber.log.Timber;

public class PncReverseJsonFormUtils {

    @Nullable
    public static String prepareJsonEditPncRegistrationForm(@NonNull Map<String, String> detailsMap, @NonNull List<String> nonEditableFields, @NonNull Context context) {
        try {
            PncMetadata pncMetadata = PncUtils.metadata();

            if (pncMetadata != null) {
                JSONObject form = new FormUtils(context).getFormJson(pncMetadata.getPncRegistrationFormName());
                Timber.d("Original Form %s", form);
                if (form != null) {
                    form.put(PncConstants.JsonFormKeyConstants.ENTITY_ID, detailsMap.get(PncConstants.KeyConstants.BASE_ENTITY_ID));

                    form.put(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE, pncMetadata.getUpdateEventType());
                    form.put(PncJsonFormUtils.CURRENT_ZEIR_ID, Utils.getValue(detailsMap, PncConstants.KeyConstants.OPENSRP_ID, true).replace("-", ""));

                    form.getJSONObject(PncJsonFormUtils.STEP1).put(PncConstants.JsonFormKeyConstants.FORM_TITLE, PncConstants.JsonFormKeyConstants.PNC_EDIT_FORM_TITLE);

                    JSONObject metadata = form.getJSONObject(PncJsonFormUtils.METADATA);
                    metadata.put(PncJsonFormUtils.ENCOUNTER_LOCATION, PncUtils.getAllSharedPreferences().fetchCurrentLocality());

                    JSONArray jsonArray = com.vijay.jsonwizard.utils.FormUtils.getMultiStepFormFields(form);

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
                Timber.e(new Exception(), "Could not start PNC Edit Registration Form because PncMetadata is null");
            }
        } catch (Exception e) {
            Timber.e(e, "PncJsonFormUtils --> getMetadataForEditForm");
        }
        return null;
    }

    private static void setFormFieldValues(@NonNull Map<String, String> pncDetails, @NonNull List<String> nonEditableFields, @NonNull JSONObject jsonObject) throws JSONException {
        Set<String> fieldsWithLocationHierarchy = PncUtils.metadata().getFieldsWithLocationHierarchy();

        if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.KeyConstants.PHOTO)) {
            reversePhoto(pncDetails.get(PncConstants.KeyConstants.BASE_ENTITY_ID), jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKeyConstants.DOB_UNKNOWN)) {
            reverseDobUnknown(pncDetails, jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKeyConstants.AGE_ENTERED)) {
            reverseAge(Utils.getValue(pncDetails, PncConstants.JsonFormKeyConstants.AGE, false), jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncConstants.JsonFormKeyConstants.DOB_ENTERED)) {
            reverseDobEntered(pncDetails, jsonObject);
        } else if (jsonObject.getString(PncJsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(PncJsonFormUtils.PERSON_IDENTIFIER)) {
            if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncJsonFormUtils.OPENSRP_ID)) {
                jsonObject.put(PncJsonFormUtils.VALUE, pncDetails.get(PncConstants.KeyConstants.OPENSRP_ID));
            } else {
                jsonObject.put(PncJsonFormUtils.VALUE, Utils.getValue(pncDetails, jsonObject.getString(PncJsonFormUtils.OPENMRS_ENTITY_ID)
                        .toLowerCase(), false).replace("-", ""));
            }
        } else if (fieldsWithLocationHierarchy != null && !fieldsWithLocationHierarchy.isEmpty()
                && fieldsWithLocationHierarchy.contains(jsonObject.getString(PncJsonFormUtils.KEY))) {
            reverseLocationField(jsonObject, pncDetails.get(jsonObject.getString(PncJsonFormUtils.KEY)));
        } else {
            String value = getMappedValue(jsonObject.optString(PncJsonFormUtils.KEY), pncDetails);
            if (StringUtils.isNotBlank(value)) {
                if (jsonObject.optString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX)) {
                    try {
                        jsonObject.put(PncJsonFormUtils.VALUE, new JSONArray(value));
                    } catch (JSONException e) {
                        if (!value.contains("[")) {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(getMappedValue(jsonObject.optString(PncJsonFormUtils.KEY), pncDetails).replaceAll(" ", "_").toLowerCase());
                            jsonObject.put(PncJsonFormUtils.VALUE, jsonArray);
                        }
                    }
                } else {
                    jsonObject.put(PncJsonFormUtils.VALUE, getMappedValue(jsonObject.optString(PncJsonFormUtils.KEY), pncDetails));
                }
            } else {
                jsonObject.put(PncJsonFormUtils.VALUE, getMappedValue(jsonObject.getString(PncJsonFormUtils.OPENMRS_ENTITY_ID), pncDetails));
            }
        }
        jsonObject.put(PncJsonFormUtils.READ_ONLY, nonEditableFields.contains(jsonObject.getString(PncJsonFormUtils.KEY)));
    }

    private static void reverseLocationField(@NonNull JSONObject jsonObject, @Nullable String entity) throws JSONException {
        List<String> entityHierarchy = null;
        if (entity != null) {
            if (PncConstants.FormValue.OTHER.equalsIgnoreCase(entity)) {
                entityHierarchy = new ArrayList<>();
                entityHierarchy.add(entity);
            } else {
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(entity);
                entityHierarchy = LocationHelper.getInstance().getOpenMrsLocationHierarchy(locationId, false);
            }
        }
        ArrayList<String> allLevels = PncLibrary.getInstance().getPncConfiguration().getPncMetadata().getHealthFacilityLevels();
        List<FormLocation> entireTree = LocationHelper.getInstance().generateLocationHierarchyTree(true, allLevels);
        String entireTreeString = AssetHandler.javaToJsonString(entireTree, new TypeToken<List<FormLocation>>() {
        }.getType());
        String facilityHierarchyString = AssetHandler.javaToJsonString(entityHierarchy, new TypeToken<List<String>>() {
        }.getType());
        if (StringUtils.isNotBlank(facilityHierarchyString)) {
            jsonObject.put(JsonFormConstants.VALUE, facilityHierarchyString);
            jsonObject.put(JsonFormConstants.TREE, new JSONArray(entireTreeString));
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

    private static void reverseDobUnknown(@NonNull Map<String, String> pncDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String value = Utils.getValue(pncDetails, PncConstants.JsonFormKeyConstants.DOB_UNKNOWN, false);
        if (!value.isEmpty() && Boolean.valueOf(pncDetails.get(PncConstants.JsonFormKeyConstants.DOB_UNKNOWN))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(PncConstants.FormValue.IS_DOB_UNKNOWN);
            jsonObject.put(PncJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reverseDobEntered(@NonNull Map<String, String> pncDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String dateString = pncDetails.get(PncConstants.JsonFormKeyConstants.DOB);
        Date date = Utils.dobStringToDate(dateString);
        if (StringUtils.isNotBlank(dateString) && date != null) {
            jsonObject.put(PncJsonFormUtils.VALUE, com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT.format(date));
        }
    }

    protected static String getMappedValue(@NonNull String key, @NonNull Map<String, String> pncDetails) {
        String value = Utils.getValue(pncDetails, key, false);
        return !TextUtils.isEmpty(value) ? value : Utils.getValue(pncDetails, key.toLowerCase(), false);
    }

    private static void reverseAge(@NonNull String value, @NonNull JSONObject jsonObject) throws JSONException {
        jsonObject.put(PncJsonFormUtils.VALUE, value);
    }
}