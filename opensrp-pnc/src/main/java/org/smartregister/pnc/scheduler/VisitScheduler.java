package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;

import java.util.List;

public abstract class VisitScheduler {

    public abstract VisitStatus getStatus();

    public VisitStatus processVisits(List<VisitCase> visits, LocalDate currentDate) {

        VisitStatus visitStatus = null;

        for (VisitCase visitCase : visits) {
            if (currentDate.isAfter(visitCase.getStartDateInclusive().minusDays(1)) && currentDate.isBefore(visitCase.getEndDateInclusive().plusDays(1))) {
                visitStatus = visitCase.getVisitStatus();
                break;
            }
        }

        return visitStatus;
    }
}
