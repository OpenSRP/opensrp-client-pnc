package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class VisitSchedulerTest {

    @Test
    public void processVisitShouldReturnPncDue() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now();

        List<VisitCase> caseList = new ArrayList<>();
        caseList.add(new VisitCase(deliveryDate, deliveryDate.plusDays(2), VisitStatus.PNC_DUE));

        VisitScheduler visitScheduler = Mockito.spy(VisitScheduler.class);

        Assert.assertEquals(visitScheduler.processVisits(caseList, currentDate), VisitStatus.PNC_DUE);
    }

    @Test
    public void processVisitShouldReturnPncOverDue() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(5);

        List<VisitCase> caseList = new ArrayList<>();
        caseList.add(new VisitCase(deliveryDate.plusDays(5), deliveryDate.plusDays(8), VisitStatus.PNC_OVERDUE));

        VisitScheduler visitScheduler = Mockito.spy(VisitScheduler.class);

        Assert.assertEquals(visitScheduler.processVisits(caseList, currentDate), VisitStatus.PNC_OVERDUE);
    }

    @Test
    public void processVisitShouldReturnNull() {

        LocalDate deliveryDate = LocalDate.now();
        LocalDate currentDate = LocalDate.now().plusDays(4);

        List<VisitCase> caseList = new ArrayList<>();
        caseList.add(new VisitCase(deliveryDate.plusDays(5), deliveryDate.plusDays(8), VisitStatus.PNC_OVERDUE));

        VisitScheduler visitScheduler = Mockito.spy(VisitScheduler.class);

        Assert.assertNull(visitScheduler.processVisits(caseList, currentDate));
    }
}
