package org.smartregister.pnc.pojo;


import androidx.annotation.NonNull;

public class PncOutcomeForm {
    private int id;
    private String baseEntityId;
    private String form;
    private String createdAt;

    public PncOutcomeForm() {
    }

    public PncOutcomeForm(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public PncOutcomeForm(int id, @NonNull String baseEntityId, @NonNull String form, @NonNull String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.form = form;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
