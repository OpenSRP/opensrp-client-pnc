package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class PncVisitSchedulerTest {

    @Test
    public void getStatusShouldEqualPncDue() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(4);

        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.buildStatusTable();

        Assert.assertEquals(pncVisitScheduler.getStatus(), VisitStatus.PNC_DUE);
    }

    @Test
    public void getStatusShouldEqualPncOverDue() {
        System.out.println(System.currentTimeMillis());
        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(5);

        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.buildStatusTable();

        Assert.assertEquals(pncVisitScheduler.getStatus(), VisitStatus.PNC_OVERDUE);
    }

    @Test
    public void getStatusShouldEqualRecordPnc() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(61);
        Map<String, String> data = new HashMap<>();
        data.put(PncDbConstants.Column.PncVisit.CREATED_AT, String.valueOf(System.currentTimeMillis() - (TimeUnit.DAYS.toMillis(1)) - 1));

        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.setLatestVisit(data);

        pncVisitScheduler.buildStatusTable();
        Assert.assertEquals(pncVisitScheduler.getStatus(), VisitStatus.RECORD_PNC);
    }

    @Test
    public void getStatusShouldEqualPncDoneToday() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(62);
        Map<String, String> data = new HashMap<>();
        data.put(PncDbConstants.Column.PncVisit.CREATED_AT, String.valueOf(System.currentTimeMillis() - (TimeUnit.DAYS.toMillis(1))));

        PncVisitScheduler pncVisitScheduler = new PncVisitScheduler();
        pncVisitScheduler.setDeliveryDate(deliveryDate);
        pncVisitScheduler.setCurrentDate(currentDate);
        pncVisitScheduler.setLatestVisit(data);

        pncVisitScheduler.buildStatusTable();
        Assert.assertEquals(VisitStatus.PNC_DONE_TODAY, pncVisitScheduler.getStatus());
    }
}
