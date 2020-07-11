package org.smartregister.pnc.scheduler;

import android.support.annotation.VisibleForTesting;

import org.jetbrains.annotations.TestOnly;
import org.joda.time.LocalDate;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PncVisitScheduler extends VisitScheduler {

    private LocalDate deliveryDate;
    private String parentBaseEntityId;
    private List<VisitBlock> visitBlocks;
    private LocalDate currentDate;
    private Map<String, Object> latestVisit;

    public PncVisitScheduler(LocalDate deliveryDate, String parentBaseEntityId) {
        this.deliveryDate = deliveryDate;
        this.currentDate = LocalDate.now();
        this.parentBaseEntityId = parentBaseEntityId;
        visitBlocks = new ArrayList<>();
        setLatestVisit(PncLibrary.getInstance().getPncVisitInfoRepository().getLatestVisitByParent(getParentBaseEntityId()));
        buildStatusTable();
    }

    @TestOnly
    public PncVisitScheduler(){}

    @VisibleForTesting
    public void buildStatusTable() {

        if (visitBlocks == null) visitBlocks = new ArrayList<>();

        List<VisitCase> caseList1 = new ArrayList<>();
        caseList1.add(new VisitCase(getDeliveryDate(), getDeliveryDate().plusDays(2), VisitStatus.PNC_DUE));

        List<VisitCase> caseList2 = new ArrayList<>();
        caseList2.add(new VisitCase(getDeliveryDate().plusDays(3), getDeliveryDate().plusDays(4), VisitStatus.PNC_DUE));
        caseList2.add(new VisitCase(getDeliveryDate().plusDays(5), getDeliveryDate().plusDays(8), VisitStatus.PNC_OVERDUE));

        List<VisitCase> caseList3 = new ArrayList<>();
        caseList3.add(new VisitCase(getDeliveryDate().plusDays(9), getDeliveryDate().plusDays(18), VisitStatus.PNC_DUE));
        caseList3.add(new VisitCase(getDeliveryDate().plusDays(19), getDeliveryDate().plusDays(28), VisitStatus.PNC_OVERDUE));

        List<VisitCase> caseList4 = new ArrayList<>();
        caseList4.add(new VisitCase(getDeliveryDate().plusDays(29), getDeliveryDate().plusDays(36), VisitStatus.PNC_DUE));
        caseList4.add(new VisitCase(getDeliveryDate().plusDays(37), getDeliveryDate().plusDays(60), VisitStatus.PNC_OVERDUE));

        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(2), caseList1));
        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(8), caseList2));
        addBlock(new VisitBlock(getDeliveryDate(), getDeliveryDate().plusDays(28), caseList3));
        addBlock(new VisitBlock(getDeliveryDate(), null, caseList4));
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

            if (isVisitDoneToday(getLatestVisit())) {
                visitStatus = VisitStatus.PNC_DONE_TODAY;
                break;
            }
            else if (block.getExpiryDate() != null) {
                if (currentDate.isBefore(block.getExpiryDate().plusDays(1))) {
                    visitStatus = processVisits(block.getVisitCaseList(), currentDate);
                    break;
                }
                else if (!blocksIterator.hasNext()) {
                    visitStatus = VisitStatus.PNC_CLOSE;
                    break;
                }
            }
            else {
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
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setParentBaseEntityId(String parentBaseEntityId) {
        this.parentBaseEntityId = parentBaseEntityId;
    }

    public String getParentBaseEntityId() {
        return parentBaseEntityId;
    }

    public void setLatestVisit(Map<String, Object> latestVisit) {
        this.latestVisit = latestVisit;
    }

    public Map<String, Object> getLatestVisit() {
        return latestVisit;
    }

    @VisibleForTesting
    public boolean isVisitDoneToday(Map<String, Object> data) {

        if (data == null) return false;

        long createdAtMillis = Long.parseLong((String)data.get(PncDbConstants.Column.PncVisit.CREATED_AT));

        long diffInMills = System.currentTimeMillis() - createdAtMillis;
        return diffInMills <= TimeUnit.DAYS.toMillis(1);
    }
}
