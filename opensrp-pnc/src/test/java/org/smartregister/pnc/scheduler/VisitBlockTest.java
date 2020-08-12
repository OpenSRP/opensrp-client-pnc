package org.smartregister.pnc.scheduler;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class VisitBlockTest {

    private VisitBlock visitBlock;

    @Before
    public void setUp() {
        LocalDate deliveryDate = LocalDate.parse("11-8-2020", DateTimeFormat.forPattern("dd-MM-yyyy"));
        LocalDate expiryDate = LocalDate.parse("15-8-2020", DateTimeFormat.forPattern("dd-MM-yyyy"));

        visitBlock = new VisitBlock(deliveryDate, expiryDate, new ArrayList<>());
    }

    @Test
    public void getDeliveryDateShouldBeVerified() {
        assertEquals("2020-08-11", visitBlock.getDeliveryDate().toString());
    }

    @Test
    public void getExpiryDateShouldBeVerified() {
        assertEquals("2020-08-15", visitBlock.getExpiryDate().toString());
    }

    @Test
    public void getVisitCaseListShouldNotEmpty() {
        assertTrue(visitBlock.getVisitCaseList().isEmpty());
    }

    @After
    public void tearDown() {
        visitBlock = null;
    }
}
