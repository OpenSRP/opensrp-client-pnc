package org.smartregister.pnc.utils;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncEventClient;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class PncJsonFormUtils extends JsonFormUtils {

    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final int REQUEST_CODE_GET_JSON = 2244;
    public static final String STEP2 = "step2";
    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String OPENSRP_ID = "OPENSRP_ID";
    public static final String ZEIR_ID = "zeir_id";
    public static final String CURRENT_ZEIR_ID = "current_zeir_id";
    public static final String READ_ONLY = "read_only";
    public static final String PERSON_IDENTIFIER = "person_identifier";

    public static JSONObject getFormAsJson(@NonNull JSONObject form, @NonNull String formName, @NonNull String id, @NonNull String currentLocationId) throws JSONException {
        return getFormAsJson(form, formName, id, currentLocationId, null);
    }

    public static JSONObject getFormAsJson(@NonNull JSONObject form, @NonNull String formName, @NonNull String id, @NonNull String currentLocationId, @Nullable HashMap<String, String> injectedFieldValues) throws JSONException {
        String entityId = id;
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

        // Inject the field values
        if (injectedFieldValues != null && injectedFieldValues.size() > 0) {
            populateInjectedFields(form, injectedFieldValues);
        }

        if (PncUtils.metadata() != null && PncUtils.metadata().getPncRegistrationFormName().equals(formName)) {
            if (StringUtils.isBlank(entityId)) {
                UniqueIdRepository uniqueIdRepo = PncLibrary.getInstance().getUniqueIdRepository();
                entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                if (entityId.isEmpty()) {
                    Timber.e("PncJsonFormUtils --> UniqueIds are empty");
                    return null;
                }
            }

            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            addRegLocHierarchyQuestions(form);

            // Inject OPenSrp id into the form
            JSONArray jsonArray = FormUtils.getMultiStepFormFields(form);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(PncJsonFormUtils.KEY).equalsIgnoreCase(PncJsonFormUtils.OPENSRP_ID)) {
                    jsonObject.remove(PncJsonFormUtils.VALUE);
                    jsonObject.put(PncJsonFormUtils.VALUE, entityId);
                }
            }

        } else {
            Timber.w("PncJsonFormUtils --> Unsupported form requested for launch %s", formName);
        }

        Timber.d("PncJsonFormUtils --> form is %s", form.toString());
        return form;
    }


    protected static void addRegLocHierarchyQuestions(@NonNull JSONObject form) {
        try {
            JSONArray questions = FormUtils.getMultiStepFormFields(form);
            ArrayList<String> allLevels = PncUtils.metadata().getLocationLevels();
            ArrayList<String> healthFacilities = PncUtils.metadata().getHealthFacilityLevels();

            List<String> defaultLocation = LocationHelper.getInstance().generateDefaultLocationHierarchy(allLevels);
            List<String> defaultFacility = LocationHelper.getInstance().generateDefaultLocationHierarchy(healthFacilities);
            List<FormLocation> entireTree = LocationHelper.getInstance().generateLocationHierarchyTree(true, allLevels);

            String defaultLocationString = AssetHandler.javaToJsonString(defaultLocation, new TypeToken<List<String>>() {
            }.getType());

            String defaultFacilityString = AssetHandler.javaToJsonString(defaultFacility, new TypeToken<List<String>>() {
            }.getType());

            String entireTreeString = AssetHandler.javaToJsonString(entireTree, new TypeToken<List<FormLocation>>() {
            }.getType());

            updateLocationTree(questions, defaultLocationString, defaultFacilityString, entireTreeString);
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> addChildRegLocHierarchyQuestions");
        }
    }

    private static void updateLocationTree(@NonNull JSONArray questions,
                                           @Nullable String defaultLocationString, @Nullable String defaultFacilityString,
                                           @Nullable String entireTreeString) throws JSONException {
        PncMetadata pncMetadata = PncUtils.metadata();
        if (pncMetadata != null && pncMetadata.getFieldsWithLocationHierarchy() != null && !pncMetadata.getFieldsWithLocationHierarchy().isEmpty()) {
            for (int i = 0; i < questions.length(); i++) {
                JSONObject widget = questions.getJSONObject(i);
                String key = widget.optString(JsonFormConstants.KEY);
                if (StringUtils.isNotBlank(key) && pncMetadata.getFieldsWithLocationHierarchy().contains(widget.optString(JsonFormConstants.KEY))) {
                    if (StringUtils.isNotBlank(entireTreeString)) {
                        addLocationTree(key, widget, entireTreeString, JsonFormConstants.TREE);
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        addLocationTreeDefault(key, widget, defaultLocationString);
                    }
                }
            }
        }
    }

    private static void addLocationTree(@NonNull String widgetKey, @NonNull JSONObject
            widget, @NonNull String updateString, @NonNull String treeType) {
        try {
            if (widgetKey.equals(widget.optString(JsonFormConstants.KEY))) {
                widget.put(treeType, new JSONArray(updateString));
            }
        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> addLocationTree");
        }
    }

    private static void addLocationTreeDefault(@NonNull String widgetKey, @NonNull JSONObject
            widget, @NonNull String updateString) {
        addLocationTree(widgetKey, widget, updateString, JsonFormConstants.DEFAULT);
    }

    public static Event tagSyncMetadata(@NonNull Event event) {
        AllSharedPreferences allSharedPreferences = PncUtils.getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));

        String childLocationId = getLocationId(event.getLocationId(), allSharedPreferences);
        event.setChildLocationId(childLocationId);

        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientDatabaseVersion(PncLibrary.getInstance().getDatabaseVersion());
        event.setClientApplicationVersion(PncLibrary.getInstance().getApplicationVersion());
        return event;
    }

    @Nullable
    public static String getLocationId(@NonNull String defaultLocationId, @NonNull AllSharedPreferences allSharedPreferences) {
        String currentLocality = allSharedPreferences.fetchCurrentLocality();

        if (currentLocality != null) {
            String currentLocalityId = LocationHelper.getInstance().getOpenMrsLocationId(currentLocality);
            if (currentLocalityId != null && !defaultLocationId.equals(currentLocalityId)) {
                return currentLocalityId;
            }
        }

        return null;
    }

    public static String locationId(@NonNull AllSharedPreferences allSharedPreferences) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String userLocationId = allSharedPreferences.fetchUserLocalityId(providerId);
        if (StringUtils.isBlank(userLocationId)) {
            userLocationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        }
        return userLocationId;
    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(@NonNull String jsonString) {
        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = null;
        if (jsonForm != null) {
            fields = fields(jsonForm);
        }
        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    protected static void processGender(@NonNull JSONArray fields) {
        try {
            JSONObject genderObject = getFieldJSONObject(fields, PncConstants.SEX);
            if (genderObject == null) {
                Timber.e("JsonArray fields is empty or null");
                return;
            }
            String genderValue = "";
            String rawGender = genderObject.getString(JsonFormConstants.VALUE);
            char rawGenderChar = !TextUtils.isEmpty(rawGender) ? rawGender.charAt(0) : ' ';
            switch (rawGenderChar) {
                case 'm':
                case 'M':
                    genderValue = "Male";
                    break;

                case 'f':
                case 'F':
                    genderValue = "Female";
                    break;

                default:
                    break;

            }

            genderObject.put(PncConstants.KeyConstants.VALUE, genderValue);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected static void processLocationFields(@NonNull JSONArray fields) throws JSONException {
        for (int i = 0; i < fields.length(); i++) {
            if (fields.getJSONObject(i).has(JsonFormConstants.TYPE) &&
                    fields.getJSONObject(i).getString(JsonFormConstants.TYPE).equals(JsonFormConstants.TREE))
                try {
                    String rawValue = fields.getJSONObject(i).getString(JsonFormConstants.VALUE);
                    if (!TextUtils.isEmpty(rawValue)) {
                        JSONArray valueArray = new JSONArray(rawValue);
                        if (valueArray.length() > 0) {
                            String lastLocationName = valueArray.getString(valueArray.length() - 1);
                            String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lastLocationName);
                            fields.getJSONObject(i).put(JsonFormConstants.VALUE, lastLocationId);
                        }
                    }
                } catch (NullPointerException e) {
                    Timber.e(e);
                } catch (IllegalArgumentException | JSONException e) {
                    Timber.e(e);
                }
        }
    }

    public static void lastInteractedWith(@NonNull JSONArray fields) {
        try {
            JSONObject lastInteractedWith = new JSONObject();
            lastInteractedWith.put(PncConstants.KeyConstants.KEY, PncConstants.JsonFormKeyConstants.LAST_INTERACTED_WITH);
            lastInteractedWith.put(PncConstants.KeyConstants.VALUE, Calendar.getInstance().getTimeInMillis());
            fields.put(lastInteractedWith);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected static void dobUnknownUpdateFromAge(@NonNull JSONArray fields) {
        try {
            JSONObject dobUnknownObject = getFieldJSONObject(fields, PncConstants.JsonFormKeyConstants.DOB_UNKNOWN);
            JSONArray options = getJSONArray(dobUnknownObject, PncConstants.JsonFormKeyConstants.OPTIONS);
            JSONObject option = getJSONObject(options, 0);
            String dobUnKnownString = option != null ? option.getString(VALUE) : null;
            if (StringUtils.isNotBlank(dobUnKnownString) && Boolean.valueOf(dobUnKnownString)) {

                String ageString = getFieldValue(fields, PncConstants.JsonFormKeyConstants.AGE_ENTERED);
                if (StringUtils.isNotBlank(ageString) && NumberUtils.isNumber(ageString)) {
                    int age = Integer.valueOf(ageString);
                    JSONObject dobJSONObject = getFieldJSONObject(fields, PncConstants.JsonFormKeyConstants.DOB_ENTERED);
                    dobJSONObject.put(VALUE, PncUtils.getDob(age));

                    //Mark the birth date as an approximation
                    JSONObject isBirthdateApproximate = new JSONObject();
                    isBirthdateApproximate.put(PncConstants.KeyConstants.KEY, FormEntityConstants.Person.birthdate_estimated);
                    isBirthdateApproximate.put(PncConstants.KeyConstants.VALUE, PncConstants.BooleanIntConstants.TRUE);
                    isBirthdateApproximate
                            .put(PncConstants.OpenMrsConstants.ENTITY, PncConstants.EntityConstants.PERSON);//Required for value to be processed
                    isBirthdateApproximate.put(PncConstants.OpenMrsConstants.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);
                    fields.put(isBirthdateApproximate);

                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public static void mergeAndSaveClient(@NonNull Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));
        JSONObject originalClientJsonObject =
                PncLibrary.getInstance().getEcSyncHelper().getClient(baseClient.getBaseEntityId());
        JSONObject mergedJson = JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);
        PncLibrary.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), mergedJson);
    }

    public static void saveImage(@NonNull String providerId, @NonNull String entityId, @NonNull String imageLocation) {
        if (StringUtils.isBlank(imageLocation)) {
            return;
        }

        File file = new File(imageLocation);
        if (!file.exists()) {
            return;
        }

        Bitmap compressedImageFile = null;
        try {
            compressedImageFile = PncLibrary.getInstance().getCompressor().compressToBitmap(file);
        } catch (IOException e) {
            Timber.e(e);
        }

        saveStaticImageToDisk(compressedImageFile, providerId, entityId);

    }

    private static void saveStaticImageToDisk(Bitmap image, String providerId, String entityId) {
        if (image == null || StringUtils.isBlank(providerId) || StringUtils.isBlank(entityId)) {
            return;
        }
        OutputStream os = null;
        try {

            if (!entityId.isEmpty()) {
                String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                File outputFile = new File(absoluteFileName);
                PncUtils.saveImageAndCloseOutputStream(image, outputFile);

                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setAnmId(providerId);
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory("profilepic");
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = PncUtils.context().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Timber.e(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }

    }

    public static JSONArray fields(@NonNull JSONObject jsonForm, @NonNull String step) {
        try {

            JSONObject step1 = jsonForm.has(step) ? jsonForm.getJSONObject(step) : null;
            if (step1 == null) {
                return null;
            }

            return step1.has(FIELDS) ? step1.getJSONArray(FIELDS) : null;

        } catch (JSONException e) {
            Timber.e(e, "PncJsonFormUtils --> fields");
        }
        return null;
    }

    public static FormTag formTag(@NonNull AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = PncLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = PncLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }

    public static String getFieldValue(@NonNull String jsonString, @NonNull String step, @NonNull String key) {
        JSONObject jsonForm = toJSONObject(jsonString);
        if (jsonForm == null) {
            return null;
        }

        JSONArray fields = fields(jsonForm, step);
        if (fields == null) {
            return null;
        }

        return getFieldValue(fields, key);
    }

    @Nullable
    public static PncEventClient processPncRegistrationForm(@NonNull String jsonString, @NonNull FormTag formTag) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getString(jsonForm, ENTITY_ID);
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            processGender(fields);

            processLocationFields(fields);

            lastInteractedWith(fields);

            dobUnknownUpdateFromAge(fields);

            Client baseClient = JsonFormUtils.createBaseClient(fields, formTag, entityId);

            Event baseEvent = JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA),
                    formTag, entityId, jsonForm.optString(PncJsonFormUtils.ENCOUNTER_TYPE),
                    PncUtils.metadata().getTableName());

            tagSyncMetadata(baseEvent);

            return new PncEventClient(baseClient, baseEvent);
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        } catch (NullPointerException e) {
            Timber.e(e);
            return null;
        } catch (IllegalArgumentException e) {
            Timber.e(e);
            return null;
        }
    }

    public static void processReminder(@NonNull JSONArray fields) {
        try {
            JSONObject reminderObject = getFieldJSONObject(fields, PncConstants.JsonFormKeyConstants.REMINDERS);
            if (reminderObject != null) {
                JSONArray options = getJSONArray(reminderObject, PncConstants.JsonFormKeyConstants.OPTIONS);
                JSONObject option = getJSONObject(options, 0);
                String value = option.optString(JsonFormConstants.VALUE);
                int result = value.equals(Boolean.toString(false)) ? 0 : 1;
                reminderObject.put(PncConstants.KeyConstants.VALUE, result);
            }
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    public static void populateInjectedFields(@NonNull JSONObject form, @NotNull HashMap<String, String> injectedFieldValues) throws JSONException {
        if (form.has(JsonFormConstants.COUNT)) {
            int stepCount = Integer.parseInt(form.optString(JsonFormConstants.COUNT));
            for (int index = 0; index < stepCount; index++) {
                String stepName = JsonFormConstants.STEP + (index + 1);
                JSONObject step = form.optJSONObject(stepName);
                if (step != null) {
                    JSONArray stepFields = step.optJSONArray(JsonFormConstants.FIELDS);
                    if (stepFields != null) {
                        for (int k = 0; k < stepFields.length(); k++) {
                            JSONObject jsonObject = stepFields.optJSONObject(k);
                            String fieldKey = jsonObject.optString(PncJsonFormUtils.KEY);
                            String fieldValue = injectedFieldValues.get(fieldKey);
                            if (!TextUtils.isEmpty(fieldValue)) {
                                jsonObject.put(PncJsonFormUtils.VALUE, fieldValue);
                            }
                        }
                    }
                }
            }
        }
    }
}