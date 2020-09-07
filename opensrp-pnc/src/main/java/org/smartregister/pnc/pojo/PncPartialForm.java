package org.smartregister.pnc.pojo;

import androidx.annotation.NonNull;

public class PncPartialForm {
    private int id;
    private String baseEntityId;
    private String formType;
    private String form;
    private String createdAt;

    public PncPartialForm(@NonNull String baseEntityId, @NonNull String formType) {
        this.baseEntityId = baseEntityId;
        this.formType = formType;
    }

    public PncPartialForm(int id, @NonNull String baseEntityId, @NonNull String form, @NonNull String formType, @NonNull String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.form = form;
        this.formType = formType;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
