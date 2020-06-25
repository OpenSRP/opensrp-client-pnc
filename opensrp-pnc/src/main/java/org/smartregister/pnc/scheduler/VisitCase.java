package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;

public class VisitCase {

    private final LocalDate startDateInclusive;
    private final LocalDate endDateInclusive;
    private final VisitStatus visitStatus;

    public VisitCase(LocalDate startDateInclusive, LocalDate endDateInclusive, VisitStatus visitCase) {
        this.startDateInclusive = startDateInclusive;
        this.endDateInclusive = endDateInclusive;
        this.visitStatus = visitCase;
    }

    public LocalDate getStartDateInclusive() {
        return startDateInclusive;
    }

    public LocalDate getEndDateInclusive() {
        return endDateInclusive;
    }

    public VisitStatus getVisitStatus() {
        return visitStatus;
    }
}
