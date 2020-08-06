package org.smartregister.pnc.config;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BasePncRegisterProviderMetadataTest {

    private BasePncRegisterProviderMetadata metadata;
    private Map<String, String> clientData;

    @Before
    public void setUp() {
        metadata = new BasePncRegisterProviderMetadata();
        clientData = new HashMap<>();
        clientData.put(PncDbConstants.KEY.FIRST_NAME, "syed");
        clientData.put(PncDbConstants.KEY.MIDDLE_NAME, "owais");
        clientData.put(PncDbConstants.KEY.LAST_NAME, "ali");
        clientData.put(PncDbConstants.KEY.DOB, "8-Nov-1989");
        clientData.put(PncConstants.FormGlobalConstants.DELIVERY_DATE, "02-08-2020");
        clientData.put(PncDbConstants.KEY.REGISTER_ID, "12345");
    }

    @Test
    public void getClientFirstNameShouldBeMatched() {
        String firstName = metadata.getClientFirstName(clientData);
        assertEquals("Syed", firstName);
    }

    @Test
    public void getClientMiddleNameShouldBeMatched() {
        String middleName = metadata.getClientMiddleName(clientData);
        assertEquals("Owais", middleName);
    }

    @Test
    public void getClientLastNameShouldBeMatched() {
        String middleName = metadata.getClientLastName(clientData);
        assertEquals("Ali", middleName);
    }

    @Test
    public void getDobShouldBeMatched() {
        String dob = metadata.getDob(clientData);
        assertEquals(clientData.get(PncDbConstants.KEY.DOB), dob);
    }

    @Test
    public void getDeliveryDaysShouldMatched() {
        String deliveryDate = clientData.get(PncConstants.FormGlobalConstants.DELIVERY_DATE);
        LocalDate date = LocalDate.parse(deliveryDate, DateTimeFormat.forPattern("dd-MM-yyyy"));
        int days = Days.daysBetween(date, LocalDate.now()).getDays();
        assertEquals(days, metadata.getDeliveryDays(clientData));
    }

    @Test
    public void getPatientIDShouldBeMatched() {
        String patientId = metadata.getPatientID(clientData);
        assertEquals(clientData.get(PncDbConstants.KEY.REGISTER_ID), patientId);
    }

    @Test
    public void getSafeValueShouldReturnEmpty() {
        assertEquals("", metadata.getSafeValue(null));
    }

    @Test
    public void getSafeValueShouldReturnSame() {
        assertEquals("PNC", metadata.getSafeValue("PNC"));
    }
}
