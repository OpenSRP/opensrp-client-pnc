package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;

public abstract class VisitScheduler {

    public abstract VisitStatus getStatus();

    public VisitStatus processVisits(List<VisitCase> visits, LocalDate currentDate, String latestVisitDateInMills) {

        VisitStatus visitStatus = null;

        for (VisitCase visitCase : visits) {
            if (currentDate.isAfter(visitCase.getStartDateInclusive().minusDays(1)) &&
                    currentDate.isBefore(visitCase.getEndDateInclusive().plusDays(1))) {
                if (latestVisitDateInMills != null) {
                    LocalDate visitLocalDate = LocalDate.fromDateFields(new Date(Long.parseLong(latestVisitDateInMills)));
                    if (visitLocalDate.getDayOfYear() == currentDate.getDayOfYear()) {
                        visitStatus = VisitStatus.PNC_DONE_TODAY;
                        break;
                    }
                    if (visitLocalDate.isAfter(visitCase.getStartDateInclusive().minusDays(1)) &&
                            visitLocalDate.isBefore(visitCase.getEndDateInclusive().plusDays(1)) &&
                            (visitLocalDate.getDayOfYear() != currentDate.getDayOfYear())) {
                        visitStatus = VisitStatus.RECORD_PNC;
                        break;
                    }
                }
                visitStatus = visitCase.getVisitStatus();
                break;
            }
        }

        return visitStatus;
    }
}
