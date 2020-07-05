package org.smartregister.pnc.utils;

import org.jeasy.rules.api.Facts;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PncFactsUtilTest {

    @Test
    public void putNonNullFactShouldCallFactPutIfNotNUll() {
        String factKey = "onaio";
        String value = "company";
        Facts facts = new Facts();

        assertNull(facts.get(factKey));
        PncFactsUtil.putNonNullFact(facts, factKey, value);

        assertEquals(value, facts.get(factKey));
    }
}
