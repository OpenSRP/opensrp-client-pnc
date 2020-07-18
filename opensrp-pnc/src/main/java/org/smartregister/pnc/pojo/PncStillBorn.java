package org.smartregister.pnc.pojo;

public class PncStillBorn {
    private String motherBaseEntityId;
    private String stillBirthCondition;

    private String eventDate;

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public void setMotherBaseEntityId(String motherBaseEntityId) {
        this.motherBaseEntityId = motherBaseEntityId;
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
