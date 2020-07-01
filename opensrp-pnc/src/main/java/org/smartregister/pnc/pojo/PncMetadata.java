package org.smartregister.pnc.pojo;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.pnc.utils.DefaultPncLocationUtils;

import java.util.ArrayList;

public class PncMetadata {

    private String pncRegistrationFormName;

    private String tableName;

    private String registerEventType;

    private String updateEventType;

    private String config;

    private Class pncFormActivity;

    private Class profileActivity;

    private boolean formWizardValidateRequiredFieldsBefore;

    private ArrayList<String> locationLevels;

    private ArrayList<String> healthFacilityLevels;

    public PncMetadata(@NonNull String pncRegistrationFormName, @NonNull String tableName, @NonNull String registerEventType, @NonNull String updateEventType,
                       @NonNull String config, @NonNull Class pncFormActivity, @Nullable Class profileActivity, boolean formWizardValidateRequiredFieldsBefore) {
        this.pncRegistrationFormName = pncRegistrationFormName;
        this.tableName = tableName;
        this.registerEventType = registerEventType;
        this.updateEventType = updateEventType;
        this.config = config;
        this.pncFormActivity = pncFormActivity;
        this.profileActivity = profileActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    public String getPncRegistrationFormName() {
        return pncRegistrationFormName;
    }

    public void setPncRegistrationFormName(String pncRegistrationFormName) {
        this.pncRegistrationFormName = pncRegistrationFormName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRegisterEventType() {
        return registerEventType;
    }

    public void setRegisterEventType(String registerEventType) {
        this.registerEventType = registerEventType;
    }

    public String getUpdateEventType() {
        return updateEventType;
    }

    public void setUpdateEventType(String updateEventType) {
        this.updateEventType = updateEventType;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Class getPncFormActivity() {
        return pncFormActivity;
    }

    public void setPncFormActivity(Class pncFormActivity) {
        this.pncFormActivity = pncFormActivity;
    }

    public Class getProfileActivity() {
        return profileActivity;
    }

    public void setProfileActivity(Class profileActivity) {
        this.profileActivity = profileActivity;
    }

    public boolean isFormWizardValidateRequiredFieldsBefore() {
        return formWizardValidateRequiredFieldsBefore;
    }

    public void setFormWizardValidateRequiredFieldsBefore(boolean formWizardValidateRequiredFieldsBefore) {
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    @NonNull
    public ArrayList<String> getLocationLevels() {
        if (locationLevels == null) {
            locationLevels = DefaultPncLocationUtils.getLocationLevels();
        }

        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    @NonNull
    public ArrayList<String> getHealthFacilityLevels() {
        if (healthFacilityLevels == null) {
            healthFacilityLevels = DefaultPncLocationUtils.getHealthFacilityLevels();
        }

        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }
}
