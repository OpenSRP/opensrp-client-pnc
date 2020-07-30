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

        PncVisitScheduler pncVisitScheduler = PncVisitScheduler.getInstance();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.buildStatusTable();

        Assert.assertEquals(pncVisitScheduler.getStatus(), VisitStatus.PNC_DUE);
    }

    @Test
    public void getStatusShouldEqualPncOverDue() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(5);

        PncVisitScheduler pncVisitScheduler = PncVisitScheduler.getInstance();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.buildStatusTable();

        Assert.assertEquals(pncVisitScheduler.getStatus(), VisitStatus.PNC_OVERDUE);
    }

    @Test
    public void getStatusShouldEqualRecordPnc() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(61);
        PncVisitScheduler pncVisitScheduler = PncVisitScheduler.getInstance();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.setLatestVisitDateInMills(String.valueOf(System.currentTimeMillis() - (TimeUnit.DAYS.toMillis(1)) - 1));

        pncVisitScheduler.buildStatusTable();
        Assert.assertEquals(pncVisitScheduler.getStatus(), VisitStatus.RECORD_PNC);
    }
}
