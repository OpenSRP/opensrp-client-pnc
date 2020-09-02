package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VisitCaseTest {

    private VisitCase visitCase;

    @Before
    public void setUp() {
        LocalDate startDateInclusive = LocalDate.parse("11-8-2020", DateTimeFormat.forPattern("dd-MM-yyyy"));
        LocalDate endDateInclusive = LocalDate.parse("15-8-2020", DateTimeFormat.forPattern("dd-MM-yyyy"));
        visitCase = new VisitCase(startDateInclusive, endDateInclusive, VisitStatus.PNC_DUE);
    }

    @After
    public void tearDown() {
        visitCase = null;
    }

    @Test
    public void getStartDateInclusiveShouldBeVerified() {
        assertEquals("2020-08-11", visitCase.getStartDateInclusive().toString());
    }

    @Test
    public void getEndDateInclusiveShouldBeVerified() {
        assertEquals("2020-08-15", visitCase.getEndDateInclusive().toString());
    }

    @Test
    public void getVisitStatusShouldBeMatched() {
        assertEquals(VisitStatus.PNC_DUE, visitCase.getVisitStatus());
    }
}
