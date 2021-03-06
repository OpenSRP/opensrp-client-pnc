package org.smartregister.pnc.scheduler;

import androidx.annotation.VisibleForTesting;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PncVisitScheduler extends VisitScheduler {

    private LocalDate deliveryDate;
    private List<VisitBlock> visitBlocks;
    private LocalDate currentDate = LocalDate.now();
    private String latestVisitDateInMills;

    public void buildStatusTable() {
        visitBlocks = new ArrayList<>();

        List<VisitCase> withIn48HoursCases = new ArrayList<>();
        withIn48HoursCases.add(new VisitCase(getDeliveryDate(), getDeliveryDate().plusDays(2), VisitStatus.PNC_DUE));

        List<VisitCase> case3To7Days = new ArrayList<>();
        case3To7Days.add(new VisitCase(getDeliveryDate().plusDays(3), getDeliveryDate().plusDays(4), VisitStatus.PNC_DUE));
        case3To7Days.add(new VisitCase(getDeliveryDate().plusDays(5), getDeliveryDate().plusDays(8), VisitStatus.PNC_OVERDUE));

        List<VisitCase> case8to28Days = new ArrayList<>();
        case8to28Days.add(new VisitCase(getDeliveryDate().plusDays(9), getDeliveryDate().plusDays(18), VisitStatus.PNC_DUE));
        case8to28Days.add(new VisitCase(getDeliveryDate().plusDays(19), getDeliveryDate().plusDays(28), VisitStatus.PNC_OVERDUE));

        List<VisitCase> case29To42Days = new ArrayList<>();
        case29To42Days.add(new VisitCase(getDeliveryDate().plusDays(29), getDeliveryDate().plusDays(36), VisitStatus.PNC_DUE));
        case29To42Days.add(new VisitCase(getDeliveryDate().plusDays(37), getDeliveryDate().plusDays(60), VisitStatus.PNC_OVERDUE));

        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(2), withIn48HoursCases));
        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(8), case3To7Days));
        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(28), case8to28Days));
        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(60), case29To42Days));
    }

    public void addBlock(VisitBlock visitCase) {
        visitBlocks.add(visitCase);
    }

    public List<VisitBlock> getVisitBlocks() {
        return visitBlocks;
    }

    @Override
    public VisitStatus getStatus() {

        VisitStatus visitStatus = null;
        LocalDate currentDate = getCurrentDate();
        Iterator<VisitBlock> blocksIterator = getVisitBlocks().iterator();

        while (blocksIterator.hasNext()) {
            VisitBlock block = blocksIterator.next();

            if (isVisitDoneToday()) {
                visitStatus = VisitStatus.PNC_DONE_TODAY;
                break;
            } else if (block.getExpiryDate() != null) {
                if (currentDate.isBefore(block.getExpiryDate().plusDays(1))) {
                    visitStatus = processVisits(block.getVisitCaseList(), currentDate, getLatestVisitDateInMills());
                    break;
                } else if (!blocksIterator.hasNext()) {
                    visitStatus = VisitStatus.PNC_CLOSE;
                    break;
                }
            } else {
                visitStatus = VisitStatus.RECORD_PNC;
                break;
            }
        }

        return visitStatus;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        buildStatusTable();
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }


    public void setLatestVisitDateInMills(String latestVisitDateInMills) {
        this.latestVisitDateInMills = latestVisitDateInMills;
    }

    public String getLatestVisitDateInMills() {
        return latestVisitDateInMills;
    }

    @VisibleForTesting
    public boolean isVisitDoneToday() {

        if (getLatestVisitDateInMills() == null) return false;

        long createdAtMillis = Long.parseLong(getLatestVisitDateInMills());

        LocalDate localDate = LocalDate.fromDateFields(new Date(createdAtMillis));
        return (localDate.getDayOfYear() == getCurrentDate().getDayOfYear()) && (localDate.getYear() == getCurrentDate().getYear());
    }
}
