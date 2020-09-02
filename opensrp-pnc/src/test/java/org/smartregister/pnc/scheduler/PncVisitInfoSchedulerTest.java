package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class PncVisitInfoSchedulerTest {

    @Test
    public void getStatusShouldEqualPncDue() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(4);

        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.buildStatusTable();

        Assert.assertEquals(VisitStatus.PNC_DUE, pncVisitScheduler.getStatus());
    }

    @Test
    public void getStatusShouldEqualPncOverDue() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(5);

        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.buildStatusTable();

        Assert.assertEquals(VisitStatus.PNC_OVERDUE, pncVisitScheduler.getStatus());
    }

    @Test
    public void getStatusShouldEqualPncClose() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(61);
        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.setLatestVisitDateInMills(String.valueOf(System.currentTimeMillis() - (TimeUnit.DAYS.toMillis(1)) - 1));

        pncVisitScheduler.buildStatusTable();
        Assert.assertEquals(VisitStatus.PNC_CLOSE, pncVisitScheduler.getStatus());
    }

    @Test
    public void getStatusShouldEqualPncRecordPnc() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(13);
        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.setLatestVisitDateInMills(String.valueOf(currentDate.minusDays(1).toDate().getTime()));

        pncVisitScheduler.buildStatusTable();
        Assert.assertEquals(VisitStatus.RECORD_PNC, pncVisitScheduler.getStatus());
    }
}
