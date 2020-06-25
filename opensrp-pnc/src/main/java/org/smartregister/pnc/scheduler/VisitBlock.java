package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;

import java.util.List;

public class VisitBlock {

    private final LocalDate deliveryDate;
    private final LocalDate expiryDate;
    private final List<VisitCase> visitCaseList;

    public VisitBlock(LocalDate deliveryDate, LocalDate expiryDate, List<VisitCase> visitCaseList) {
        this.deliveryDate = deliveryDate;
        this.expiryDate = expiryDate;
        this.visitCaseList = visitCaseList;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public List<VisitCase> getVisitCaseList() {
        return visitCaseList;
    }
}
