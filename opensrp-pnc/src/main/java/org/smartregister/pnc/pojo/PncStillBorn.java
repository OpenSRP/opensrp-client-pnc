package org.smartregister.pnc.pojo;

public class PncStillBorn {
    private String medicInfoId;
    private String stillBirthCondition;

    private String eventDate;

    public String getMedicInfoId() {
        return medicInfoId;
    }

    public void setMedicInfoId(String medicInfoId) {
        this.medicInfoId = medicInfoId;
    }

    public String getStillBirthCondition() {
        return stillBirthCondition;
    }

    public void setStillBirthCondition(String stillBirthCondition) {
        this.stillBirthCondition = stillBirthCondition;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
